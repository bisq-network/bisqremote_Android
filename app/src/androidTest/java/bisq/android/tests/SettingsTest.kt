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
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Durations.TEN_SECONDS
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilNotNull
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert
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
        settingsActivityRule.launch()

        settingsScreen.themePreference.click()
        assertThat(settingsScreen.themePromptDialog.isDisplayed())
            .describedAs("Theme prompt dialog is displayed")
            .isTrue()

        settingsScreen.themePromptDialog.cancelButton.click()
        intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun clickDarkThemeChangesToDarkTheme() {
        settingsActivityRule.launch()

        settingsScreen.themePreference.click()
        assertThat(settingsScreen.themePromptDialog.isDisplayed())
            .describedAs("Theme prompt dialog is displayed")
            .isTrue()

        settingsScreen.themePromptDialog.darkThemeSelection.click()

        assertThat(AppCompatDelegate.getDefaultNightMode())
            .describedAs("Night mode")
            .isEqualTo(UiModeManager.MODE_NIGHT_YES)
    }

    @Test
    fun clickLightThemeChangesToLightTheme() {
        settingsActivityRule.launch()

        settingsScreen.themePreference.click()
        assertThat(settingsScreen.themePromptDialog.isDisplayed())
            .describedAs("Theme prompt dialog is displayed")
            .isTrue()

        settingsScreen.themePromptDialog.lightThemeSelection.click()

        assertThat(AppCompatDelegate.getDefaultNightMode())
            .describedAs("Night mode")
            .isEqualTo(UiModeManager.MODE_NIGHT_NO)
    }

    @Test
    fun clickSystemThemeChangesToSystemTheme() {
        settingsActivityRule.launch()

        settingsScreen.themePreference.click()
        assertThat(settingsScreen.themePromptDialog.isDisplayed())
            .describedAs("Theme prompt dialog is displayed")
            .isTrue()

        settingsScreen.themePromptDialog.systemThemeSelection.click()

        assertThat(AppCompatDelegate.getDefaultNightMode())
            .describedAs("Night mode")
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    @Test
    fun clickResetPairingAndNotAcceptingConfirmationDoesNotWipePairing() {
        if (!isFirebaseMessagingInitialized()) {
            FirebaseMock.mockFirebaseTokenSuccessful()
        }
        val key = Device.instance.key
        val token = Device.instance.token
        settingsActivityRule.launch()

        settingsScreen.resetPairingPreference.click()
        assertThat(settingsScreen.alertDialogResetPairing.isDisplayed())
            .describedAs("Reset pairing alert dialog is displayed")
            .isTrue()

        settingsScreen.alertDialogResetPairing.negativeButton.click()

        intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isEqualTo(key)
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(token)
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.PAIRED)
    }

    @Test
    fun clickResetPairingAndAcceptingConfirmationWipesPairingAndLoadsWelcomeScreen() {
        if (!isFirebaseMessagingInitialized()) {
            FirebaseMock.mockFirebaseTokenSuccessful()
        }
        val key = Device.instance.key
        val token = Device.instance.token
        settingsActivityRule.launch()

        settingsScreen.resetPairingPreference.click()
        assertThat(settingsScreen.alertDialogResetPairing.isDisplayed())
            .describedAs("Reset pairing alert dialog is displayed")
            .isTrue()

        settingsScreen.alertDialogResetPairing.positiveButton.click()

        intended(IntentMatchers.hasComponent(WelcomeActivity::class.java.name))
        await atMost TEN_SECONDS untilNotNull { Device.instance.key }
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNotNull()
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNotEqualTo(key)
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isNotNull()
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isNotEqualTo(token)
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    fun clickScanPairingTokenLoadsPairingScanActivity() {
        if (!isFirebaseMessagingInitialized()) {
            FirebaseMock.mockFirebaseTokenSuccessful()
        }
        settingsActivityRule.launch()

        settingsScreen.scanPairingTokenPreference.click()
        intended(IntentMatchers.hasComponent(PairingScanActivity::class.java.name))
    }

    @Test
    fun clickAboutBisqAndNotAcceptingConfirmationDoesNotLoadBisqNetworkWebpage() {
        settingsActivityRule.launch()

        settingsScreen.aboutBisqPreference.click()
        assertThat(settingsScreen.alertDialogLoadBisqNetworkUrl.isDisplayed())
            .describedAs("Load Bisq Network URL alert dialog is displayed")
            .isTrue()

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

    @Test
    fun clickAboutBisqAndAcceptingConfirmationLoadsBisqNetworkWebpage() {
        settingsActivityRule.launch()

        intending(IntentMatchers.hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )

        settingsScreen.aboutBisqPreference.click()
        assertThat(settingsScreen.alertDialogLoadBisqNetworkUrl.isDisplayed())
            .describedAs("Load Bisq Network URL alert dialog is displayed")
            .isTrue()

        settingsScreen.alertDialogLoadBisqNetworkUrl.positiveButton.click()

        val expectedIntent = AllOf.allOf(
            IntentMatchers.hasAction(Intent.ACTION_VIEW),
            IntentMatchers.hasData(BISQ_NETWORK_URL)
        )
        intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
        intended(expectedIntent)
    }

    @Test
    fun clickAboutAppAndNotAcceptingConfirmationDoesNotLoadBisqMobileWebpage() {
        settingsActivityRule.launch()

        settingsScreen.aboutAppPreference.click()
        assertThat(settingsScreen.alertDialogLoadBisqMobileUrl.isDisplayed())
            .describedAs("Load Bisq mobile URL alert dialog is displayed")
            .isTrue()

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

    @Test
    fun clickAboutAppAndAcceptingConfirmationLoadsBisqMobileWebpage() {
        settingsActivityRule.launch()

        intending(IntentMatchers.hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )

        settingsScreen.aboutAppPreference.click()
        assertThat(settingsScreen.alertDialogLoadBisqMobileUrl.isDisplayed())
            .describedAs("Load Bisq mobile URL alert dialog is displayed")
            .isTrue()

        settingsScreen.alertDialogLoadBisqMobileUrl.positiveButton.click()

        val expectedIntent = AllOf.allOf(
            IntentMatchers.hasAction(Intent.ACTION_VIEW),
            IntentMatchers.hasData(BISQ_MOBILE_URL)
        )
        intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
        intended(expectedIntent)
    }
}
