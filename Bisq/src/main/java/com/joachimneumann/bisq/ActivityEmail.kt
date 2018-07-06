package com.joachimneumann.bisq

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class ActivityEmail : AppCompatActivity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)
        status = bind(R.id.register_email_status_textview)
        status.setText(getString(R.string.register_email_status)+": not yet registered")
    }

}