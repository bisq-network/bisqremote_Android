package com.joachimneumann.bisq.ui

import android.content.Intent
import com.joachimneumann.bisq.ui.pairing.PairingSuccessActivity

open class UnpairedBaseActivity : BaseActivity() {

    fun pairingConfirmed() {
        this.runOnUiThread {
            playTone()
            startActivity(Intent(Intent(this, PairingSuccessActivity::class.java)))
        }
    }

}
