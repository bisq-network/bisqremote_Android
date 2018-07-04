package com.joachimneumann.bisq

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

import com.joachimneumann.bisq.Database.RawBisqNotification

class NotificationTable : AppCompatActivity() {
    private var tabletext: TextView? = null

    private var mViewModel: RawBisqNotificationViewModel? = null
    private var notificationManager: NotificationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel: NotificationChannel

            notificationChannel = NotificationChannel("Bisq", "Bisq", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }

        setContentView(R.layout.activity_notifcationtable)
        tabletext = findViewById(R.id.tableText)
        tabletext!!.text = "list"

        mViewModel = ViewModelProviders.of(this).get(RawBisqNotificationViewModel::class.java)
        mViewModel!!.bisqNotifications.observe(this, Observer { bisqNotifications -> updateGUI(bisqNotifications!!) })
    }

    private fun updateGUI(rawBisqNotifications: List<RawBisqNotification>) {
        tabletext!!.text = "n = " + rawBisqNotifications.size

    }
}