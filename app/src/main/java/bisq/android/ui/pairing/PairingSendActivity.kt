package bisq.android.ui.pairing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import bisq.android.R
import bisq.android.model.Device
import bisq.android.ui.UnpairedBaseActivity

class PairingSendActivity : UnpairedBaseActivity() {

    private lateinit var sendPairingTokenInstructions: TextView
    private lateinit var sendPairingTokenButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_pairing_send)

        sendPairingTokenInstructions = bind(R.id.sendPairingTokenInstructions)

        sendPairingTokenButton = bind(R.id.sendPairingTokenButton)
        sendPairingTokenButton.setOnClickListener {
            onSendPairingTokenButtonClick()
        }
    }

    private fun onSendPairingTokenButtonClick() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_pairing_subject))
        intent.putExtra(Intent.EXTRA_TEXT, Device.instance.pairingToken())
        startActivity(Intent.createChooser(intent, getString(R.string.send_pairing_token)))
    }

}
