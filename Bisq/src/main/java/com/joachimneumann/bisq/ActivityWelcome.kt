package com.joachimneumann.bisq

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import android.content.IntentFilter


class ActivityWelcome: AppCompatActivity() {
    private lateinit var learnMoreButton: Button
    private lateinit var pairButton: Button
    private var receiver: BisqNotificationReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("Bisq", "ActivityWelcome onCreate()")

        val bundle = intent.extras
        if (bundle != null) {
            val notificationMessage = bundle.get("encrypted")
            if (notificationMessage != null) {
                val broadcastIntent = Intent()
                broadcastIntent.action = this.getString(R.string.bisq_broadcast)
                broadcastIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                broadcastIntent.putExtra("notificationMessage", notificationMessage as String)
                if (receiver == null) { receiver = BisqNotificationReceiver(this) }
                val filter = IntentFilter()
                filter.addAction(this.getString(R.string.bisq_broadcast))
                registerReceiver(receiver, filter)
                sendBroadcast(broadcastIntent)
            }
        }

        // already registered?
//        val registered = Phone.instance.exampleToken() // Phone.instance.readFromPreferences(this)
        val registered = Phone.instance.readFromPreferences(this)
        if (registered) {
            startActivity(Intent(this, ActivityNotificationTable::class.java))
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

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this@ActivityWelcome, OnSuccessListener<InstanceIdResult> { instanceIdResult ->
            Phone.instance.newToken(instanceIdResult.token)
            pairButton.isEnabled = true
        })
    }

    fun checkForToken() {
        if (Phone.instance.token == null) {
            internetDialog()
        } else {
            pairButton.isEnabled = true
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("Bisq", "ActivityWelcome onStart()")

        if (receiver == null) {
            receiver = BisqNotificationReceiver(this)
        }
        val filter = IntentFilter()
        filter.addAction(this.getString(R.string.bisq_broadcast))
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        Log.e("Bisq", "ActivityWelcome onPause()")
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed(java.lang.Runnable {
            if (Phone.instance.token == null) {
                checkForToken()
            }
        }, 10000)
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

// copied from https://medium.com/@quiro91/improving-findviewbyid-with-kotlin-4cf2f8f779bb
fun <T : View> Activity.bind(@IdRes res : Int) : T {
    @Suppress("UNCHECKED_CAST")
    return findViewById(res) as T
}