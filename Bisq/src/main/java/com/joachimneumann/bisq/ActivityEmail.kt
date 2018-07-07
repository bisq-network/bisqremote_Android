package com.joachimneumann.bisq

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class ActivityEmail : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var resend_email_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)
        status = bind(R.id.register_email_status_textview)
        status.setText("...waiting for confirmation")
        resend_email_button = bind(R.id.resend_email_button)
        resend_email_button.setOnClickListener { createEmail() }
        createEmail()
    }

    private fun createEmail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        var phone: Phone = Phone.getInstance(this)
        val x = phone.apsToken
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        val emailBody = getString(R.string.email_content1) + phone.phoneID() + getString(R.string.email_content2)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

}