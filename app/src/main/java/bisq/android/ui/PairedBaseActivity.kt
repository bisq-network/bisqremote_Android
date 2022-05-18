package bisq.android.ui

import android.content.Intent
import android.widget.Toast
import bisq.android.ui.welcome.WelcomeActivity

open class PairedBaseActivity : BaseActivity() {

    fun pairingRemoved(toastMessage: String) {
        this.runOnUiThread {
            playTone()
            startActivity(Intent(Intent(this, WelcomeActivity::class.java)))
            Toast.makeText(
                this, toastMessage, Toast.LENGTH_LONG
            ).show()
        }
    }

}
