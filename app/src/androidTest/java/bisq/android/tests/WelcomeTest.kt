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
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.BISQ_MOBILE_URL
import bisq.android.mocks.Firebase
import bisq.android.model.Device
import bisq.android.ui.pairing.PairingScanActivity
import bisq.android.ui.welcome.WelcomeActivity
import org.junit.Assert.*
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

    @Test
    fun clickPairButtonAfterReceivingFcmTokenLoadsPairingScanActivity() {
        Firebase.mockFirebaseTokenSuccessful()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            intended(hasComponent(PairingScanActivity::class.java.name))
        }
    }

    @Test
    fun clickPairButtonAfterFailingToReceiveFcmTokenShowsPromptToRetryFetchingToken() {
        Firebase.mockFirebaseTokenUnsuccessful()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())
        }
    }

    @Test
    fun clickCancelOnTokenFailurePromptAllowsClickingPairButtonAgain() {
        Firebase.mockFirebaseTokenUnsuccessful()
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
    fun clickTryAgainOnTokenFailurePromptRetriesFetchingToken() {
        Firebase.mockFirebaseTokenUnsuccessful()
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            welcomeScreen.pairButton.click()
            assertTrue(welcomeScreen.alertDialogCannotRetrieveDeviceToken.isDisplayed())

            Firebase.mockFirebaseTokenSuccessful()
            welcomeScreen.alertDialogCannotRetrieveDeviceToken.positiveButton.click()
            intended(hasComponent(PairingScanActivity::class.java.name))
            assertEquals(
                "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_",
                Device.instance.token
            )
        }
    }

    @Test
    fun clickLearnMoreButtonLoadsBisqMobileWebpage() {
        Firebase.mockFirebaseTokenSuccessful()
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
