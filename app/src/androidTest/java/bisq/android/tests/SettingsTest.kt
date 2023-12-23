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
import android.app.UiModeManager
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.BISQ_MOBILE_URL
import bisq.android.BISQ_NETWORK_URL
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.services.BisqFirebaseMessagingService.Companion.isFirebaseMessagingInitialized
import bisq.android.testCommon.mocks.FirebaseMock
import bisq.android.ui.pairing.PairingScanActivity
import bisq.android.ui.settings.SettingsActivity
import bisq.android.ui.welcome.WelcomeActivity
import junit.framework.AssertionFailedError
import org.awaitility.Durations.TEN_SECONDS
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilNotNull
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert
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

    @After
    override fun cleanup() {
        super.cleanup()
        FirebaseMock.unmockFirebaseMessaging()
    }

    @Test
    fun clickThemePromptsToChangeTheme() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.themePreference.click()
            assertTrue(settingsScreen.themePromptDialog.isDisplayed())

            settingsScreen.themePromptDialog.cancelButton.click()
            intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
        }
    }

    @Test
    fun clickDarkThemeChangesToDarkTheme() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.themePreference.click()
            assertTrue(settingsScreen.themePromptDialog.isDisplayed())

            settingsScreen.themePromptDialog.darkThemeSelection.click()

            assertEquals(UiModeManager.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
        }
    }

    @Test
    fun clickLightThemeChangesToLightTheme() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.themePreference.click()
            assertTrue(settingsScreen.themePromptDialog.isDisplayed())

            settingsScreen.themePromptDialog.lightThemeSelection.click()

            assertEquals(UiModeManager.MODE_NIGHT_NO, AppCompatDelegate.getDefaultNightMode())
        }
    }

    @Test
    fun clickSystemThemeChangesToSystemTheme() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.themePreference.click()
            assertTrue(settingsScreen.themePromptDialog.isDisplayed())

            settingsScreen.themePromptDialog.systemThemeSelection.click()

            assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.getDefaultNightMode())
        }
    }

    @Test
    fun clickResetPairingAndNotAcceptingConfirmationDoesNotWipePairing() {
        if (!isFirebaseMessagingInitialized()) {
            FirebaseMock.mockFirebaseTokenSuccessful()
        }
        val key = Device.instance.key
        val token = Device.instance.token
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.resetPairingPreference.click()
            assertTrue(settingsScreen.alertDialogResetPairing.isDisplayed())

            settingsScreen.alertDialogResetPairing.negativeButton.click()

            intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
            assertEquals(key, Device.instance.key)
            assertEquals(token, Device.instance.token)
            assertEquals(DeviceStatus.PAIRED, Device.instance.status)
        }
    }

    @Test
    fun clickResetPairingAndAcceptingConfirmationWipesPairingAndLoadsWelcomeScreen() {
        if (!isFirebaseMessagingInitialized()) {
            FirebaseMock.mockFirebaseTokenSuccessful()
        }
        val key = Device.instance.key
        val token = Device.instance.token
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.resetPairingPreference.click()
            assertTrue(settingsScreen.alertDialogResetPairing.isDisplayed())

            settingsScreen.alertDialogResetPairing.positiveButton.click()

            intended(IntentMatchers.hasComponent(WelcomeActivity::class.java.name))
            await atMost TEN_SECONDS untilNotNull { Device.instance.key }
            assertNotNull(Device.instance.key)
            assertNotEquals(key, Device.instance.key)
            assertNotNull(Device.instance.token)
            assertNotEquals(token, Device.instance.token)
            assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
        }
    }

    @Test
    fun clickScanPairingTokenLoadsPairingScanActivity() {
        if (!isFirebaseMessagingInitialized()) {
            FirebaseMock.mockFirebaseTokenSuccessful()
        }
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.scanPairingTokenPreference.click()
            intended(IntentMatchers.hasComponent(PairingScanActivity::class.java.name))
        }
    }

    @Test
    fun clickAboutBisqAndNotAcceptingConfirmationDoesNotLoadBisqNetworkWebpage() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.aboutBisqPreference.click()
            assertTrue(settingsScreen.alertDialogLoadBisqNetworkUrl.isDisplayed())

            settingsScreen.alertDialogLoadBisqNetworkUrl.negativeButton.click()

            try {
                val expectedIntent = AllOf.allOf(
                    IntentMatchers.hasAction(Intent.ACTION_VIEW),
                    IntentMatchers.hasData(BISQ_NETWORK_URL)
                )
                intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
                intended(expectedIntent)
            } catch (e: AssertionFailedError) {
                // We want the assertion to fail, since trying to negate the intended
                // doesn't seem to work
                return
            }
            Assert.fail("Loaded web page after clicking cancel")
        }
    }

    @Test
    fun clickAboutBisqAndAcceptingConfirmationLoadsBisqNetworkWebpage() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            intending(IntentMatchers.hasAction(Intent.ACTION_VIEW)).respondWith(
                Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
            )

            settingsScreen.aboutBisqPreference.click()
            assertTrue(settingsScreen.alertDialogLoadBisqNetworkUrl.isDisplayed())

            settingsScreen.alertDialogLoadBisqNetworkUrl.positiveButton.click()

            val expectedIntent = AllOf.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(BISQ_NETWORK_URL)
            )
            intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
            intended(expectedIntent)
        }
    }

    @Test
    fun clickAboutAppAndNotAcceptingConfirmationDoesNotLoadBisqMobileWebpage() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            settingsScreen.aboutAppPreference.click()
            assertTrue(settingsScreen.alertDialogLoadBisqMobileUrl.isDisplayed())

            settingsScreen.alertDialogLoadBisqMobileUrl.negativeButton.click()

            try {
                val expectedIntent = AllOf.allOf(
                    IntentMatchers.hasAction(Intent.ACTION_VIEW),
                    IntentMatchers.hasData(BISQ_MOBILE_URL)
                )
                intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
                intended(expectedIntent)
            } catch (e: AssertionFailedError) {
                // We want the assertion to fail, since trying to negate the intended
                // doesn't seem to work
                return
            }
            Assert.fail("Loaded web page after clicking cancel")
        }
    }

    @Test
    fun clickAboutAppAndAcceptingConfirmationLoadsBisqMobileWebpage() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            intending(IntentMatchers.hasAction(Intent.ACTION_VIEW)).respondWith(
                Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
            )

            settingsScreen.aboutAppPreference.click()
            assertTrue(settingsScreen.alertDialogLoadBisqMobileUrl.isDisplayed())

            settingsScreen.alertDialogLoadBisqMobileUrl.positiveButton.click()

            val expectedIntent = AllOf.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(BISQ_MOBILE_URL)
            )
            intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
            intended(expectedIntent)
        }
    }
}
