package com.joachimneumann.bisq

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import java.util.Date

class ActivityNotificationDetail : AppCompatActivity() {
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var action: TextView
    private lateinit var event_time: TextView
    private lateinit var receive_time: TextView
    private lateinit var transactionID: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationdetail)
        title = bind(R.id.detail_title)
        message = bind(R.id.detail_message)
        action = bind(R.id.detail_message)
        event_time = bind(R.id.detail_event_time)
        receive_time = bind(R.id.detail_received_time)
        transactionID = bind(R.id.detail_transaction_id)
        val uid = intent.getIntExtra("uid", 0)

        val mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)
        val n = mViewModel.getFromUid(uid)
        if (n != null) {
            title.text = n.title
            message.text = n.message
            action.text = n.message
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            event_time.text = sdf.format(Date(n.sentDate!!))
            receive_time.text = sdf.format(Date(n.receivedDate!!))
            transactionID.text = n.txId
        }
    }
}