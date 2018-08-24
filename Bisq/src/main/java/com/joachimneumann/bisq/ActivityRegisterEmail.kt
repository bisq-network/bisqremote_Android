package com.joachimneumann.bisq

import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class ActivityRegisterEmail : AppCompatActivity() {

    private lateinit var resendEmailButton: Button
    private lateinit var register_email_instructions: TextView
    private var receiver: BisqNotificationReceiver? = null


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
            try {
                val notificationTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(applicationContext, notificationTone).play()

                val i = Intent(Intent(this,ActivityCongratulations::class.java))
                startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        if (receiver == null) {
            receiver = BisqNotificationReceiver(this)
        }
        val filter = IntentFilter()
        filter.addAction(this.getString(R.string.bisq_broadcast))
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private fun createEmail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        val emailBody = getString(R.string.email_content1) + Phone.instance.pairingToken() + getString(R.string.email_content2)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

}