package com.joachimneumann.bisq

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class ActivityRegisterEmail : AppCompatActivity() {

    private lateinit var resendEmailButton: Button
    private lateinit var register_email_instructions: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)
        resendEmailButton = bind(R.id.resend_email_button)
        resendEmailButton.setOnClickListener { createEmail() }
        register_email_instructions = bind(R.id.register_email_instructions)
        createEmail()
    }

    fun confirmed() {
        this.runOnUiThread(java.lang.Runnable {
            register_email_instructions.setText(getString(R.string.register_qr_confirmation_received))
        })
    }

    private fun createEmail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        val emailBody = getString(R.string.email_content1) + Phone.instance.phoneID() + getString(R.string.email_content2)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

}