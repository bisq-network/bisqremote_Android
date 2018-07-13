package com.joachimneumann.bisq.Database

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.joachimneumann.bisq.R
import android.view.LayoutInflater
import java.text.SimpleDateFormat


class NotificationAdapter(private var nList: List<BisqNotification>) :
        RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val n = nList[position]
        if (n.read) {
            holder.icon.setImageResource(R.drawable.info_read)
            holder.title.setTextColor(Color.GRAY)
        } else {
            holder.icon.setImageResource(R.drawable.info)
            holder.title.setTextColor(Color.BLACK)
        }
        holder.title.text = n.title
        holder.time.text = SimpleDateFormat("yyyy-mm-dd hh:mm").format(n.timestampEvent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_cell, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return nList.size
    }

    fun removeItem(position: Int) {
        var mList: MutableList<BisqNotification> = nList.toMutableList()
        mList.removeAt(position)
        nList = mList
    }

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView
        var title: TextView
        var time: TextView

        init {
            this.icon = view.findViewById(R.id.notification_cell_image) as ImageView
            this.title = view.findViewById<View>(R.id.notification_cell_title) as TextView
            this.time = view.findViewById<View>(R.id.notification_cell_time) as TextView
        }
    }

}