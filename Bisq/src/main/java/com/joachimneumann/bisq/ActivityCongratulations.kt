package com.joachimneumann.bisq

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class ActivityCongratulations : AppCompatActivity() {

    private lateinit var done_button: Button

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_congratulations)
            done_button = bind(R.id.done_button)
            done_button.setOnClickListener { donePressed() }
        }

    private fun donePressed() {
        val i = Intent(Intent(this,ActivityNotificationTable::class.java))
        startActivity(i)
    }
}