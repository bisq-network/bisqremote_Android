/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.android.tests

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import bisq.android.database.BisqNotification
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.model.NotificationMessage.Companion.BISQ_MESSAGE_ANDROID_MAGIC
import bisq.android.model.NotificationType
import bisq.android.rules.FirebasePushNotificationTestRule
import bisq.android.rules.ScreenshotRule
import bisq.android.screens.NotificationTableScreen
import bisq.android.services.BisqFirebaseMessagingService
import bisq.android.util.CryptoUtil
import bisq.android.util.DateUtil
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class NotificationTest {
    companion object {
        private const val WAIT_CONDITION_TIMEOUT_MS: Long = 1000
    }

    private val fcmTestRule = FirebasePushNotificationTestRule(BisqFirebaseMessagingService())

    private val permissionRule: GrantPermissionRule
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            GrantPermissionRule.grant()
        }

    // Define a RuleChain to ensure screenshots are taken BEFORE the teardown of activity rules
    @get:Rule
    val ruleChain: RuleChain = RuleChain
        .outerRule(permissionRule)
        .around(fcmTestRule)
        .around(ScreenshotRule())

    private val applicationContext: Context = ApplicationProvider.getApplicationContext()

    private val deviceToken = "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN" +
        "3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_"

    private val bisqNotification = buildBisqNotification()

    @Before
    fun setupDevice() {
        pairDevice()
    }

    @After
    fun closeNotificationPanel() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressBack()
    }

    @Test
    fun whenReceivingEncryptedNotification_thenDecryptedContentShownInNotificationPanel() {
        val remoteMessage = buildRemoteMessage(bisqNotification)

        fcmTestRule.sendPush(remoteMessage)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.openNotification()
        device.wait(Until.hasObject(By.text("Bisq")), WAIT_CONDITION_TIMEOUT_MS)
        val title: UiObject2 = device.getObject(By.text(bisqNotification.title!!))
        val text: UiObject2 = device.getObject(By.text(bisqNotification.message!!))
        assertThat(title.text).isEqualTo(bisqNotification.title)
        assertThat(text.text).isEqualTo(bisqNotification.message)
    }

    @Test
    fun whenReceivingMultipleNotifications_thenAllShownInNotificationPanel() {
        val bisqNotifications = mutableListOf<BisqNotification>()
        for (counter in 1..5) {
            val bisqNotification = buildBisqNotification()
            bisqNotifications.add(bisqNotification)
            fcmTestRule.sendPush(buildRemoteMessage(bisqNotification))
        }

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.openNotification()
        bisqNotifications.forEach { bisqNotification ->
            device.wait(Until.hasObject(By.text("Bisq")), WAIT_CONDITION_TIMEOUT_MS)
            val title: UiObject2 = device.getObject(By.text(bisqNotification.title!!))
            val text: UiObject2 = device.getObject(By.text(bisqNotification.message!!))
            assertThat(title.text).isEqualTo(bisqNotification.title)
            assertThat(text.text).isEqualTo(bisqNotification.message)
        }
    }

    @Test
    fun whenClickingReceivedNotification_thenAppOpenedToNotificationView() {
        val remoteMessage = buildRemoteMessage(bisqNotification)

        fcmTestRule.sendPush(remoteMessage)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.openNotification()
        device.wait(Until.hasObject(By.text("Bisq")), WAIT_CONDITION_TIMEOUT_MS)
        val title: UiObject2 = device.getObject(By.text(bisqNotification.title!!))

        title.click()

        val notificationTableScreen = NotificationTableScreen()
        assertThat(notificationTableScreen.notificationRecylerView.isDisplayed()).isTrue()
        val notificationCount = notificationTableScreen.notificationRecylerView.getItemCount()
        assertThat(notificationCount)
            .describedAs("Notification count")
            .isEqualTo(1)
    }

    private fun pairDevice() {
        Device.instance.newToken(deviceToken)
        Device.instance.status = DeviceStatus.PAIRED
        Device.instance.saveToPreferences(applicationContext)
    }

    private fun buildBisqNotification(): BisqNotification {
        val now = Date()
        val tradeId = (100000..999999).random()
        val bisqNotification = BisqNotification()
        bisqNotification.type = NotificationType.TRADE.name
        bisqNotification.title = "Trade confirmed"
        bisqNotification.message = "The trade with ID $tradeId is confirmed."
        bisqNotification.sentDate = now.time - 1000 * 60
        bisqNotification.receivedDate = now.time
        return bisqNotification
    }

    private fun serializeNotificationPayload(bisqNotification: BisqNotification): String {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Date::class.java, DateUtil())
        val gson = gsonBuilder.create()
        return gson.toJson(bisqNotification)
    }

    private fun buildRemoteMessage(bisqNotification: BisqNotification): RemoteMessage {
        val initializationVector = (1000000000000000..9999999999999999).random().toString()
        val encryptedContent = CryptoUtil(Device.instance.key!!).encrypt(
            serializeNotificationPayload(bisqNotification),
            initializationVector
        )

        return RemoteMessage.Builder(deviceToken).addData(
            "encrypted",
            "${BISQ_MESSAGE_ANDROID_MAGIC}|$initializationVector|$encryptedContent"
        ).build()
    }

    private fun UiDevice.getObject(selector: BySelector): UiObject2 =
        findObject(selector) ?: error("Object not found for: $selector")
}
