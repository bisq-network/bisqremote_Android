package com.joachimneumann.bisq.Database

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.joachimneumann.bisq.R
import java.text.SimpleDateFormat

class NotificationAdapter(private val context: Context,
                    private val nList: List<BisqNotification>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val n = nList[position]
        val rowView = inflater.inflate(R.layout.notification_cell , parent, false)
        val titleTextView = rowView.findViewById(R.id.notification_cell_title) as TextView
        val timeTextView = rowView.findViewById(R.id.notification_cell_time) as TextView
        val imageView = rowView.findViewById<ImageView>(R.id.notification_cell_image)

        if (position == 3) n.read = true
        if (n.read) {
            imageView.setImageResource(R.drawable.info_read)
            titleTextView.setTextColor(ContextCompat.getColor(context, R.color.readNotificationTitle));
        } else {
            imageView.setImageResource(R.drawable.info)
            titleTextView.setTextColor(Color.BLACK);
        }
        titleTextView.text = n.title
        timeTextView.text = SimpleDateFormat("yyyy-mm-dd hh:mm").format(n.timestampEvent)
        return rowView
    }

    override fun getItem(position: Int): Any {
        return nList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return nList.size
    }

}