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
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.intent.Intents
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.screens.NotificationDetailScreen
import bisq.android.screens.NotificationTableScreen
import bisq.android.screens.PairingScanScreen
import bisq.android.screens.PairingSendScreen
import bisq.android.screens.PairingSuccessScreen
import bisq.android.screens.SettingsScreen
import bisq.android.screens.WelcomeScreen
import org.junit.After
import org.junit.Before
import java.util.Locale

abstract class BaseTest {
    companion object {
        private const val TAG = "BaseTest"
        private const val MAX_ANR_COUNT = 3
    }

    protected val applicationContext: Context = ApplicationProvider.getApplicationContext()

    protected val welcomeScreen = WelcomeScreen()
    protected val settingsScreen = SettingsScreen()
    protected val pairingScanScreen = PairingScanScreen()
    protected val pairingSendScreen = PairingSendScreen()
    protected val pairingSuccessScreen = PairingSuccessScreen()
    protected val notificationDetailScreen = NotificationDetailScreen()
    protected val notificationTableScreen = NotificationTableScreen()

    // Running count of the number of Android Not Responding dialogs to prevent endless dismissal
    private var anrCount = 0

    // `RootViewWithoutFocusException` class is private, need to match the message (instead of using
    // type matching)
    private val rootViewWithoutFocusExceptionMsg = java.lang.String.format(
        Locale.ROOT,
        "Waited for the root of the view hierarchy to have " +
            "window focus and not request layout for 10 seconds. If you specified a non " +
            "default root matcher, it may be picking a root that never takes focus. " +
            "Root:"
    )

    @Before
    open fun setup() {
        Espresso.setFailureHandler { error, viewMatcher ->
            if (error.message!!.contains(rootViewWithoutFocusExceptionMsg) &&
                anrCount < MAX_ANR_COUNT
            ) {
                Log.i(TAG, "Handling Android Not Responding dialog")
                anrCount++
                handleAnrDialog()
            } else {
                Log.w(
                    TAG,
                    "Handled too many Android Not Responding dialogs, passing off to the " +
                        "default espresso handler"
                )
                DefaultFailureHandler(applicationContext).handle(error, viewMatcher)
            }
        }
        Intents.init()
    }

    private fun handleAnrDialog() {
        val device = UiDevice.getInstance(getInstrumentation())
        // Assumes the device is running in English locale
        val waitButton = device.findObject(UiSelector().textContains("wait"))
        if (waitButton.exists()) {
            waitButton.click()
        }
    }

    @After
    open fun cleanup() {
        Intents.release()
    }

    fun pairDevice() {
        val token =
            "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN" +
                "3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_"
        Device.instance.newToken(token)
        Device.instance.status = DeviceStatus.PAIRED
        Device.instance.saveToPreferences(applicationContext)
    }
}
