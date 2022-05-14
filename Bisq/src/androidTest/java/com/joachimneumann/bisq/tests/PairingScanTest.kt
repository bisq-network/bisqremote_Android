package com.joachimneumann.bisq.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joachimneumann.bisq.ui.pairing.PairingScanActivity
import com.joachimneumann.bisq.ui.pairing.PairingSendActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PairingScanTest : BaseTest() {

    @Test
    fun clickNoWebcamButtonLoadsPairingSendActivity() {
        ActivityScenario.launch(PairingScanActivity::class.java).use {
            pairingScanScreen.noWebcamButton.click()
            Intents.intended(IntentMatchers.hasComponent(PairingSendActivity::class.java.name))
        }
    }

}
