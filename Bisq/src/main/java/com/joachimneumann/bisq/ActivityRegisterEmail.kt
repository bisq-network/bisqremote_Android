package com.joachimneumann.bisq

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class ActivityRegisterEmail : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var resendEmailButton: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)
        status = bind(R.id.register_email_status_textview)
        status.setText(getString(R.string.waiting))
        resendEmailButton = bind(R.id.resend_email_button)
        resendEmailButton.setOnClickListener { createEmail() }
        progressBar = bind(R.id.registerEmailProgressBar)
        progressBar.visibility = View.INVISIBLE
        createEmail()
    }

    fun confirmed() {
        this.runOnUiThread(java.lang.Runnable {
            status.setText(getString(R.string.register_qr_confirmation_received))
            progressBar.visibility = View.VISIBLE
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