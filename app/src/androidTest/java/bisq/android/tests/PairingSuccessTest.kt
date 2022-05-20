package bisq.android.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.notification.NotificationTableActivity
import bisq.android.ui.pairing.PairingSuccessActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PairingSuccessTest : BaseTest() {

    @Before
    override fun setup() {
        super.setup()
        pairDevice()
    }

    @Test
    fun clickPairingSuccessButtonLoadsNotificationTableScreen() {
        ActivityScenario.launch(PairingSuccessActivity::class.java).use {
            pairingSuccessScreen.pairingCompleteButton.click()
            Intents.intended(IntentMatchers.hasComponent(NotificationTableActivity::class.java.name))
        }
    }

}
