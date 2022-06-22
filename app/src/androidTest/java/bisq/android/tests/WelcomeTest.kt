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
import android.os.Build
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import bisq.android.BISQ_MOBILE_URL
import bisq.android.mocks.FirebaseMock
import bisq.android.model.Device
import bisq.android.ui.pairing.PairingScanActivity
import bisq.android.ui.welcome.WelcomeActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    fun testClickPairButtonWhenGooglePlayServicesUnavailableShowsPrompt() {
        FirebaseMock.mockGooglePlayServicesNotAvailable()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogGooglePlayServicesUnavailable.isDisplayed())

            welcomeScreen.alertDialogGooglePlayServicesUnavailable.button.click()
            assertFalse(welcomeScreen.alertDialogGooglePlayServicesUnavailable.isDisplayed())

            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogGooglePlayServicesUnavailable.isDisplayed())
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    fun clickPairButtonAfterReceivingFcmTokenLoadsPairingScanActivity() {
        FirebaseMock.mockFirebaseTokenSuccessful()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            intended(hasComponent(PairingScanActivity::class.java.name))
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    fun clickPairButtonAfterFailingToReceiveFcmTokenShowsPromptToRetryFetchingToken() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    fun clickCancelOnTokenFailurePromptAllowsClickingPairButtonAgain() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())

            welcomeScreen.alertDialogCannotRetrieveDeviceToken.negativeButton.click()
            assertFalse(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())

            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    fun clickTryAgainOnTokenFailurePromptRetriesFetchingToken() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())

            FirebaseMock.mockFirebaseTokenSuccessful()
            welcomeScreen.alertDialogCannotRetrieveDeviceToken.positiveButton.click()
            intended(hasComponent(PairingScanActivity::class.java.name))
            assertEquals(
                "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqn" +
                    "Ql8owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHY" +
                    "E7XPFUQDD7UlnVB-giAI",
                Device.instance.token
            )
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    fun clickLearnMoreButtonLoadsBisqMobileWebpage() {
        FirebaseMock.mockFirebaseTokenSuccessful()
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.learnMoreButton.click()
            assertTrue(welcomeScreen.alertDialogLoadBisqMobileUrl.isDisplayed())

            welcomeScreen.alertDialogLoadBisqMobileUrl.positiveButton.click()
            intended(hasAction(Intent.ACTION_VIEW))
            intended(hasData(BISQ_MOBILE_URL))
        }
    }
}
