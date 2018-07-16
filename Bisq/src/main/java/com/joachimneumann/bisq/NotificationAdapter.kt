package com.joachimneumann.bisq

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import com.joachimneumann.bisq.Database.BisqNotification
import java.text.SimpleDateFormat


class NotificationAdapter(private var nList: List<BisqNotification>) :
        RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    companion object {
        var font: Typeface? = null
    }


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        var n = nList[position]
        holder.icon.setText(R.string.icon_trade)
        if (n.type == "TRADE") { holder.icon.setText(R.string.icon_trade) }
        if (n.type == "OFFER") { holder.icon.setText(R.string.icon_offer) }
        if (n.type == "DISPUTE") { holder.icon.setText(R.string.icon_dispute) }
        if (n.type == "PRICE") { holder.icon.setText(R.string.icon_price) }
        if (n.type == "MARKET") { holder.icon.setText(R.string.icon_market) }

        if (n.read) {
            holder.title.setTextColor(Color.GRAY)
        } else {
            holder.title.setTextColor(Color.BLACK)
        }
        holder.title.text = n.title
        if (n.sentDate != null) {
            holder.time.text = SimpleDateFormat("yyyy-mm-dd hh:mm").format(n.sentDate) // for debugging database access add:  +" "+n.uid
        } else {
            holder.time.text = "time: ??"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_cell, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return nList.size
    }


    fun uid(postition: Int): Int {
        return nList[postition].uid
    }

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: TextView
        var title: TextView
        var time: TextView

        init {
            if (NotificationAdapter.font == null) {
                NotificationAdapter.font = Typeface.createFromAsset(view.context.getAssets(), "Font Awesome 5 Free-Solid-900.otf")
            }
            icon = view.findViewById(R.id.notification_cell_image) as TextView
            icon.typeface = NotificationAdapter.font
            icon.textSize = 33F
            this.title = view.findViewById<View>(R.id.notification_cell_title) as TextView
            this.time = view.findViewById<View>(R.id.notification_cell_time) as TextView
        }
    }

}