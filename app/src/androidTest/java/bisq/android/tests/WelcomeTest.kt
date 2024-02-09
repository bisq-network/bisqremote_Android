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
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.BISQ_MOBILE_URL
import bisq.android.model.Device
import bisq.android.testCommon.mocks.FirebaseMock
import bisq.android.ui.pairing.PairingScanActivity
import junit.framework.AssertionFailedError
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeTest : BaseTest() {
    @Before
    override fun setup() {
        super.setup()
        Device.instance.reset()
        Device.instance.clearPreferences(applicationContext)
    }

    @After
    override fun cleanup() {
        super.cleanup()
        FirebaseMock.unmockFirebaseMessaging()
    }

    @Test
    fun testClickPairButtonWhenGooglePlayServicesUnavailableShowsPrompt() {
        FirebaseMock.mockGooglePlayServicesNotAvailable()
        welcomeActivityRule.launch()

        welcomeScreen.pairButton.click()
        assertThat(welcomeScreen.alertDialogGooglePlayServicesUnavailable.isDisplayed())
            .describedAs("Google Play Services unavailable alert dialog is displayed")
            .isTrue()

        welcomeScreen.alertDialogGooglePlayServicesUnavailable.dismissButton.click()
        assertThat(welcomeScreen.alertDialogGooglePlayServicesUnavailable.isDisplayed())
            .describedAs("Google Play Services unavailable alert dialog is not displayed")
            .isFalse()

        welcomeScreen.pairButton.click()
        assertThat(welcomeScreen.alertDialogGooglePlayServicesUnavailable.isDisplayed())
            .describedAs("Google Play Services unavailable alert dialog is displayed")
            .isTrue()
    }

    @Test
    fun clickPairButtonAfterReceivingFcmTokenLoadsPairingScanActivity() {
        FirebaseMock.mockFirebaseTokenSuccessful()
        welcomeActivityRule.launch()

        welcomeScreen.pairButton.click()
        intended(hasComponent(PairingScanActivity::class.java.name))
    }

    @Test
    fun clickPairButtonAfterFailingToReceiveFcmTokenShowsPromptToRetryFetchingToken() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        welcomeActivityRule.launch()

        welcomeScreen.pairButton.click()
        assertThat(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
            .describedAs("Cannot retrieve device token alert dialog is displayed")
            .isTrue()
    }

    @Test
    fun clickCancelOnTokenFailurePromptAllowsClickingPairButtonAgain() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        welcomeActivityRule.launch()

        welcomeScreen.pairButton.click()
        assertThat(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
            .describedAs("Cannot retrieve device token alert dialog is displayed")
            .isTrue()

        welcomeScreen.alertDialogCannotRetrieveDeviceToken.negativeButton.click()
        assertThat(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
            .describedAs("Cannot retrieve device token alert dialog is not displayed")
            .isFalse()

        welcomeScreen.pairButton.click()
        assertThat(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
            .describedAs("Cannot retrieve device token alert dialog is displayed")
            .isTrue()
    }

    @Test
    fun clickTryAgainOnTokenFailurePromptRetriesFetchingToken() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        welcomeActivityRule.launch()

        welcomeScreen.pairButton.click()
        assertThat(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
            .describedAs("Cannot retrieve device token alert dialog is displayed")
            .isTrue()

        FirebaseMock.mockFirebaseTokenSuccessful()
        welcomeScreen.alertDialogCannotRetrieveDeviceToken.positiveButton.click()
        intended(hasComponent(PairingScanActivity::class.java.name))
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(
                "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqn" +
                    "Ql8owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHY" +
                    "E7XPFUQDD7UlnVB-giAI"
            )
    }

    @Test
    fun clickLearnMoreButtonAndNotAcceptingConfirmationDoesNotLoadBisqMobileWebpage() {
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )
        welcomeActivityRule.launch()

        welcomeScreen.learnMoreButton.click()
        assertThat(welcomeScreen.alertDialogLoadBisqMobileUrl.isDisplayed())
            .describedAs("Load Bisq mobile URL alert dialog is displayed")
            .isTrue()

        welcomeScreen.alertDialogLoadBisqMobileUrl.negativeButton.click()

        try {
            val expectedIntent = allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(BISQ_MOBILE_URL)
            )
            intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
            intended(expectedIntent)
        } catch (e: AssertionFailedError) {
            // We want the assertion to fail, since trying to negate the intended
            // doesn't seem to work
            return
        }
        fail("Loaded web page after clicking cancel")
    }

    @Test
    fun clickLearnMoreButtonAndAcceptingConfirmationLoadsBisqMobileWebpage() {
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )
        welcomeActivityRule.launch()

        intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )

        welcomeScreen.learnMoreButton.click()
        assertThat(welcomeScreen.alertDialogLoadBisqMobileUrl.isDisplayed())
            .describedAs("Load Bisq mobile URL alert dialog is displayed")
            .isTrue()

        welcomeScreen.alertDialogLoadBisqMobileUrl.positiveButton.click()

        val expectedIntent = allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(BISQ_MOBILE_URL)
        )
        intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
        intended(expectedIntent)
    }
}
