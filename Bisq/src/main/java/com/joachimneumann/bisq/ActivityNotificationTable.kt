package com.joachimneumann.bisq

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.joachimneumann.bisq.Database.BisqNotification
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.Toast

class ActivityNotificationTable : AppCompatActivity() {
    private lateinit var mViewModel: BisqNotificationViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private var receiver: BisqNotificationReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)

        val liveData = mViewModel.bisqNotifications
        liveData.observe(this, Observer { bisqNotifications -> updateGUI(bisqNotifications!!) })

        setContentView(R.layout.activity_notificationtable)

        toolbar = bind(R.id.bisq_toolbar)
        setSupportActionBar(toolbar)

        recyclerView = bind(R.id.notification_recycler_view)

        // TODO   No horizontal divider line visible
        // recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))

        recyclerView.setLayoutManager(LinearLayoutManager (this));

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as NotificationAdapter
                mViewModel.delete(mViewModel.getFromUid(adapter.uid(viewHolder.adapterPosition))!!)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onStart() {
        Log.e("Bisq", "ActivityNotificationTable onStart()")
        super.onStart()

        if (receiver == null) {
            receiver = BisqNotificationReceiver(this)
        }
        val filter = IntentFilter()
        filter.addAction(this.getString(R.string.bisq_broadcast))
        registerReceiver(receiver, filter)
    }

    override fun onResume() {
        Log.e("Bisq", "ActivityNotificationTable onResume()")
        super.onResume()

    }

    override fun onPause() {
        Log.e("Bisq", "ActivityNotificationTable onPause()")

        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
        receiver = null
    }


    override fun onBackPressed() {
        // no back button in this screen // super.onBackPressed()
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

    private fun bisqNotificationClicked(item : BisqNotification) {

        val intent = Intent(this, ActivityNotificationDetail::class.java)
        intent.putExtra("uid", item.uid)
        mViewModel.markAsRead(item.uid)
        startActivity(intent)
    }

    private fun updateGUI(bisqNotifications: List<BisqNotification>) {
        recyclerView.adapter = NotificationAdapter(bisqNotifications, { item: BisqNotification -> bisqNotificationClicked(item)})    }

}
