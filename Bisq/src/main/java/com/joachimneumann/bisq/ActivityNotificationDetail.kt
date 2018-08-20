package com.joachimneumann.bisq

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
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
        action = bind(R.id.detail_action)
        event_time = bind(R.id.detail_event_time)
        receive_time = bind(R.id.detail_received_time)
        transactionID = bind(R.id.detail_transaction_id)
        val uid = intent.getIntExtra("uid", 0)

        val mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)
        val n = mViewModel.getFromUid(uid)
        if (n != null) {
            title.text = n.title
            if (n.message != null && n.message!!.count() > 0) {
                message.text = n.message
            } else {
                message.visibility = View.GONE
            }
            if (n.actionRequired != null && n.actionRequired!!.count() > 0) {
                action.text = n.actionRequired
            } else {
                action.visibility = View.GONE
            }
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            event_time.text =   "event:    "+sdf.format(Date(n.sentDate!!))
            receive_time.text = "received: "+sdf.format(Date(n.receivedDate!!))
            if (n.txId != null && n.txId!!.count() > 0) {
                transactionID.text = "txID:     "+n.txId
            } else {
                transactionID.visibility = View.GONE
            }
        }
    }
}