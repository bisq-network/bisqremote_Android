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
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.joachimneumann.bisq.Database.BisqNotification
import android.support.v7.widget.helper.ItemTouchHelper

class ActivityNotificationTable : AppCompatActivity() {
    private lateinit var mViewModel: BisqNotificationViewModel
    private var notificationManager: NotificationManager? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)

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

        val liveData = mViewModel.bisqNotifications
        liveData.observe(this, Observer { bisqNotifications -> updateGUI(bisqNotifications!!) })

        setContentView(R.layout.activity_notificationtable)

        toolbar = bind(R.id.bisq_toolbar)
        setSupportActionBar(toolbar)

        recyclerView = bind(R.id.notification_recycler_view)

        // TODO   No horizontal divider line visible
        // recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))

        var mLayoutManager = LinearLayoutManager (this);
        recyclerView.setLayoutManager(mLayoutManager);

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as NotificationAdapter
                mViewModel.delete(mViewModel.getFromUid(adapter.uid(viewHolder.adapterPosition))!!)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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

}
