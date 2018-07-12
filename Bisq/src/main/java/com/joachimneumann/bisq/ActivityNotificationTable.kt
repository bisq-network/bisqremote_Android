package com.joachimneumann.bisq

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import com.joachimneumann.bisq.Database.BisqNotification
import com.joachimneumann.bisq.Database.NotificationAdapter

class ActivityNotificationTable : AppCompatActivity, View.OnClickListener {
    private var mViewModel: BisqNotificationViewModel? = null
    private var notificationManager: NotificationManager? = null
    private lateinit var settingsButton: Button
    private lateinit var listView: ListView
    private lateinit var toolbar: Toolbar

    constructor() : super()


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

        setContentView(R.layout.activity_notificationtable)

        toolbar = bind(R.id.bisq_toolbar)
        setSupportActionBar(toolbar)

        mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)
        mViewModel!!.bisqNotifications.observe(this, Observer { bisqNotifications -> updateGUI(bisqNotifications!!) })

//        settingsButton = bind(R.id.settingsButton)
//        settingsButton.setOnClickListener(this)

        listView = bind(R.id.notificationListView)

//        val authors = arrayOf("Conan Doyle, Arthur", "Christie, Agatha", "Collins, Wilkie");
//        val adapter = ArrayAdapter<String>(this, android.R.layout.notification_cell, authors);
//        listView.adapter = adapter


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.action_settings) {
            startActivity(Intent(this,ActivitySettings::class.java))
        }
        return true
    }

    private fun updateGUI(bisqNotifications: List<BisqNotification>) {
        val adapter = NotificationAdapter(this, bisqNotifications)
        listView.adapter = adapter

    }

    override fun onClick(view: View) {
//        if (view.id == R.id.settingsButton) {
//            val intent = Intent(this, ActivitySettings::class.java)
//            startActivity(intent)
//        }
    }
}

// copied from https://medium.com/@quiro91/improving-findviewbyid-with-kotlin-4cf2f8f779bb
fun <T : View> Activity.bind(@IdRes res : Int) : T {
    @Suppress("UNCHECKED_CAST")
    return findViewById(res) as T
}