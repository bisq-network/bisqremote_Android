package com.joachimneumann.bisq.tests

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joachimneumann.bisq.BISQ_MOBILE_URL
import com.joachimneumann.bisq.BISQ_NETWORK_URL
import com.joachimneumann.bisq.model.Device
import com.joachimneumann.bisq.ui.notification.NotificationTableActivity
import com.joachimneumann.bisq.ui.settings.SettingsActivity
import com.joachimneumann.bisq.ui.welcome.WelcomeActivity
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest : BaseTest() {

    @Test
    fun clickResetButtonWipesPairingAndLoadsWelcomeScreen() {
        val token =
            "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_"
        Device.instance.newToken(token)
        val key = Device.instance.key
        Device.instance.confirmed = true
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.resetButton.click()
            intended(IntentMatchers.hasComponent(WelcomeActivity::class.java.name))
            Assert.assertNotNull(Device.instance.key)
            Assert.assertNotEquals(key, Device.instance.key)
            Assert.assertNotNull(Device.instance.token)
            Assert.assertNotEquals(token, Device.instance.token)
            Assert.assertFalse(Device.instance.confirmed)
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
                Assert.assertEquals(false, readState)
            }

            notificationTableScreen.settingsButton.click()
            settingsScreen.markAsReadButton.click()

            for (position in 0 until count - 1) {
                val readState =
                    notificationTableScreen.notificationRecylerView.getReadStateAtPosition(position)
                Assert.assertEquals(true, readState)
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
