package com.joachimneumann.bisq

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult

class ActivityWelcome: AppCompatActivity() {
    private lateinit var learnMoreButton: Button
    private lateinit var pairButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // already registered?
        val registered = Phone.instance.readFromPreferences(this)
        if (registered) {
            startActivity(Intent(this,ActivityNotificationTable::class.java))
            return
        }

        // start fresh
        Phone.instance.reset()

        setContentView(R.layout.activity_welcome)

        learnMoreButton = bind(R.id.learn_more_button)
        learnMoreButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                bisqWebpagePressed()
            }
        })

        pairButton = bind(R.id.pair_button)
        pairButton.setOnClickListener { startActivity(Intent(this,ActivityRegisterQR::class.java)) }
        pairButton.isEnabled = false

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@ActivityWelcome, OnSuccessListener<InstanceIdResult> { instanceIdResult ->
            Phone.instance.newToken(instanceIdResult.token)
            pairButton.isEnabled = true
            checkForToken()
        })
    }

    fun checkForToken() {
        if (Phone.instance.token == null) {
            internetDialog()
        } else {
            pairButton.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed(java.lang.Runnable {
            if (Phone.instance.token == null) {
                checkForToken()
            }
        }, 4000)
    }

    fun internetDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialog: AlertDialog = builder.setTitle("Downloading notification token...")
                .setMessage("Are you connected to the Internet?")
                .setPositiveButton("Try again") { dialog, _ ->
                    Handler().postDelayed(java.lang.Runnable {
                        if (Phone.instance.token == null) {
                            checkForToken()
                        }
                    }, 1000)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY)
    }
//
    fun bisqWebpagePressed() {
        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bisq.network"))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Please install a webbrowser", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

}
