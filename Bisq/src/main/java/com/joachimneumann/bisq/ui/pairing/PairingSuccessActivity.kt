package com.joachimneumann.bisq.ui.pairing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.joachimneumann.bisq.R
import com.joachimneumann.bisq.ui.PairedBaseActivity
import com.joachimneumann.bisq.ui.notification.NotificationTableActivity

class PairingSuccessActivity : PairedBaseActivity() {

    private lateinit var pairingCompleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_pairing_success)

        pairingCompleteButton = bind(R.id.pairing_complete_button)
        pairingCompleteButton.setOnClickListener {
            onPairingCompleteButtonClick()
        }
    }

    private fun onPairingCompleteButtonClick() {
        startActivity(Intent(Intent(this, NotificationTableActivity::class.java)))
    }

}
