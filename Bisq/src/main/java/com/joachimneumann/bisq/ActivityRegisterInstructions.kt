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
        emailButton.setOnClickListener { startActivity(Intent(this,ActivityEmail::class.java)) }
    }
}
