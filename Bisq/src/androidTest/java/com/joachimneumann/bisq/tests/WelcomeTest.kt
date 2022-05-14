package com.joachimneumann.bisq.tests

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joachimneumann.bisq.BISQ_MOBILE_URL
import com.joachimneumann.bisq.mocks.Firebase
import com.joachimneumann.bisq.model.Device
import com.joachimneumann.bisq.ui.pairing.PairingScanActivity
import com.joachimneumann.bisq.ui.welcome.WelcomeActivity
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
