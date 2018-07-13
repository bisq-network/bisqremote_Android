package com.joachimneumann.bisq

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.joachimneumann.bisq.Database.BisqNotification
import com.joachimneumann.bisq.Database.NotificationAdapter
import android.support.v7.widget.helper.ItemTouchHelper
import android.arch.lifecycle.LiveData



class ActivityNotificationTable : AppCompatActivity(), View.OnClickListener {
    private var mViewModel: BisqNotificationViewModel? = null
    private var notificationManager: NotificationManager? = null
    private lateinit var settingsButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

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

        mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)
        val x = mViewModel!!.bisqNotifications
        x.observe(this, Observer { bisqNotifications -> updateGUI(bisqNotifications!!) })

        setContentView(R.layout.activity_notificationtable)

        toolbar = bind(R.id.bisq_toolbar)
        setSupportActionBar(toolbar)

        recyclerView = bind(R.id.notification_recycler_view)
        var mLayoutManager = LinearLayoutManager (this);
        recyclerView.setLayoutManager(mLayoutManager);

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as NotificationAdapter
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

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
        recyclerView.adapter = NotificationAdapter(bisqNotifications)
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