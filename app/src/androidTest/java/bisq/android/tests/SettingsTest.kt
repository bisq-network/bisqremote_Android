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

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.BISQ_MOBILE_URL
import bisq.android.BISQ_NETWORK_URL
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.ui.notification.NotificationTableActivity
import bisq.android.ui.settings.SettingsActivity
import bisq.android.ui.welcome.WelcomeActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest : BaseTest() {

    @Before
    override fun setup() {
        super.setup()
        pairDevice()
    }

    @Test
    fun clickResetButtonWipesPairingAndLoadsWelcomeScreen() {
        val key = Device.instance.key
        val token = Device.instance.token
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.resetButton.click()
            intended(IntentMatchers.hasComponent(WelcomeActivity::class.java.name))
            assertNotNull(Device.instance.key)
            assertNotEquals(key, Device.instance.key)
            assertNotNull(Device.instance.token)
            assertNotEquals(token, Device.instance.token)
            assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
        }
    }

    @Test
    fun clickDeleteAllNotificationsButtonDeletesAllNotifications() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.settingsButton.click()
            settingsScreen.addExampleNotificationsButton.click()
            assertTrue(notificationTableScreen.notificationRecylerView.getItemCount() > 0)
            notificationTableScreen.settingsButton.click()
            settingsScreen.deleteNotificationsButton.click()
            assertTrue(notificationTableScreen.notificationRecylerView.getItemCount() == 0)
        }
    }

    @Test
    fun clickMarkAsReadButtonMarksAllNotificationsAsRead() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.settingsButton.click()
            settingsScreen.addExampleNotificationsButton.click()

            val count = notificationTableScreen.notificationRecylerView.getItemCount()
            for (position in 0 until count - 1) {
                val readState =
                    notificationTableScreen.notificationRecylerView.getReadStateAtPosition(position)
                assertEquals(false, readState)
            }

            notificationTableScreen.settingsButton.click()
            settingsScreen.markAsReadButton.click()

            for (position in 0 until count - 1) {
                val readState =
                    notificationTableScreen.notificationRecylerView.getReadStateAtPosition(position)
                assertEquals(true, readState)
            }
        }
    }

    @Test
    fun clickAboutBisqButtonLoadsBisqNetworkWebpage() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            intending(IntentMatchers.hasAction(Intent.ACTION_VIEW)).respondWith(
                Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
            )
            settingsScreen.aboutBisqButton.click()
            assertTrue(settingsScreen.alertDialogLoadBisqNetworkUrl.isDisplayed())
            settingsScreen.alertDialogLoadBisqNetworkUrl.positiveButton.click()
            intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
            intended(IntentMatchers.hasData(BISQ_NETWORK_URL))
        }
    }

    @Test
    fun clickAboutAppButtonLoadsBisqMobileWebpage() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            intending(IntentMatchers.hasAction(Intent.ACTION_VIEW)).respondWith(
                Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
            )
            settingsScreen.aboutAppButton.click()
            assertTrue(settingsScreen.alertDialogLoadBisqMobileUrl.isDisplayed())
            settingsScreen.alertDialogLoadBisqMobileUrl.positiveButton.click()
            intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
            intended(IntentMatchers.hasData(BISQ_MOBILE_URL))
        }
    }
}
