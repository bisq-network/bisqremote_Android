package com.joachimneumann.bisq

import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import com.joachimneumann.bisq.Database.BisqNotification
import java.text.SimpleDateFormat


class NotificationAdapter(private var nList: List<BisqNotification>, val clickListener: (BisqNotification) -> Unit):
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

        holder.title.text = n.title
        if (n.sentDate != null) {
            holder.time.text = SimpleDateFormat("yyyy-MM-dd HH:mm").format(n.sentDate) // for debugging database access add:  +" "+n.uid
        } else {
            holder.time.text = "time: ??"
        }
        if (n.read) {
            holder.icon.setTextColor(ContextCompat.getColor(holder.icon.context, R.color.primary_disabled))
            holder.title.setTextColor(ContextCompat.getColor(holder.icon.context, R.color.read_title))
        } else {
            holder.icon.setTextColor(ContextCompat.getColor(holder.icon.context, R.color.primary))
            holder.title.setTextColor(ContextCompat.getColor(holder.icon.context, R.color.unread_title))
        }
        holder.bind(nList.get(position), clickListener)
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

        fun bind(part: BisqNotification, clickListener: (BisqNotification) -> Unit) {
            itemView.setOnClickListener { clickListener(part)}
        }


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