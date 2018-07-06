package com.joachimneumann.bisq

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.NotificationCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button

import java.util.ArrayList

class ActivityRegister : AppCompatActivity(), View.OnClickListener {
    private var notificationManager: NotificationManager? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transfer_code)
        val bisqToolbar = findViewById<Toolbar>(R.id.toolbar)
        bisqToolbar.title = ""
        setSupportActionBar(bisqToolbar)

        val nextButton = findViewById<Button>(R.id.registerDoneButton)
        nextButton.setOnClickListener(this)

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


        val phone = Phone.getInstance(this)
        Log.i("bisq", "phone: " + phone.phoneID())
        val tabLayout = findViewById<View>(R.id.tab_layout) as TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("QR Code"))
        tabLayout.addTab(tabLayout.newTab().setText("Email"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val viewPager = findViewById<View>(R.id.pager) as ViewPager
        val adapter = TransferPageAdapter(supportFragmentManager, tabLayout.tabCount, phone.phoneID())
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    fun donePressed() {
        val testNotification = NotificationCompat.Builder(this, "Bisq")
                .setContentTitle("XXX")
                .setSmallIcon(R.drawable.help)

        notificationManager!!.notify((System.currentTimeMillis() / 1000).toInt(), testNotification.build())
        val myIntent = Intent(this@ActivityRegister, NotificationTable::class.java)
        startActivity(myIntent)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.registerDoneButton) {
            donePressed()
        }
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}
