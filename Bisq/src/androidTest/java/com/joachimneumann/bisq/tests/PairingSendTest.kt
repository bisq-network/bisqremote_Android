package com.joachimneumann.bisq.tests

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.BundleMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joachimneumann.bisq.model.Device
import com.joachimneumann.bisq.ui.pairing.PairingSendActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PairingSendTest : BaseTest() {

    @Test
    fun clickSendPairingTokenButton() {
        Device.instance.newToken(
            "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_"
        )
        ActivityScenario.launch(PairingSendActivity::class.java).use {
            pairingSendScreen.sendPairingTokenButton.click()
            Intents.intended(hasAction(Intent.ACTION_CHOOSER))
            Intents.intended(
                hasExtras(
                    BundleMatchers.hasValue(
                        hasExtras(
                            BundleMatchers.hasValue(Device.instance.pairingToken())
                        )
                    )
                )
            )
        }
    }

}
