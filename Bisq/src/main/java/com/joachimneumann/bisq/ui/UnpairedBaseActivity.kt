package bisq.android.ui

import android.content.Intent
import bisq.android.ui.pairing.PairingSuccessActivity

open class UnpairedBaseActivity : BaseActivity() {

    fun pairingConfirmed() {
        this.runOnUiThread {
            playTone()
            startActivity(Intent(Intent(this, PairingSuccessActivity::class.java)))
        }
    }

}
