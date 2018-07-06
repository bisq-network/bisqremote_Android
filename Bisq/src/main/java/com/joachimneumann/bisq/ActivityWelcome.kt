package com.joachimneumann.bisq

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast

class ActivityWelcome : AppCompatActivity() {
    private lateinit var learn_more_button: Button
    private lateinit var register_button: Button

    private val mPopupWindow: PopupWindow? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)
        learn_more_button = bind(R.id.learn_more_button)
        learn_more_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                bisqWebpagePressed()
            }
        })

        register_button = bind(R.id.register_button)
        register_button.setOnClickListener { startActivity(Intent(this,ActivityRegisterInstructions::class.java)) }
    }

    fun bisqWebpagePressed() {
        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bisq.network"))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application can handle this request." + " Please install a webbrowser", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

}
