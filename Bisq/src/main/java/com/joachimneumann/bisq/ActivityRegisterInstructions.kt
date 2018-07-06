package com.joachimneumann.bisq

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.app.Activity



class ActivityRegisterInstructions : AppCompatActivity() {
    private lateinit var webcamButton: Button
    private lateinit var emailButton: Button

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_instructions)
        webcamButton = bind(R.id.register_webcam_button)
        webcamButton.setOnClickListener { startActivity(Intent(this,ActivityQR::class.java)) }
        emailButton = bind(R.id.register_email_button)
        emailButton.setOnClickListener { createEmail() }
    }

    public fun createEmail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        var phone = Phone.getInstance(this)
        val x = phone.apsToken
        intent.putExtra(Intent.EXTRA_EMAIL, "your_email_address")
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        val emailBody = getString(R.string.email_content1) + phone.phoneID() + getString(R.string.email_content2)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
        startActivityForResult(Intent.createChooser(intent, "Send Email"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        startActivity(Intent(this,ActivityEmail::class.java))
    }
}
