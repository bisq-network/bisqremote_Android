package com.joachimneumann.bisq.Database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.joachimneumann.bisq.R
import java.text.SimpleDateFormat

class NotificationAdapter(private val context: Context,
                    private val dataSource: List<RawBisqNotification>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.notification_cell , parent, false)
        val titleTextView = rowView.findViewById(R.id.notification_cell_title) as TextView
        titleTextView.text = dataSource[position].title
        val timeTextView = rowView.findViewById(R.id.notification_cell_time) as TextView
        timeTextView.text = SimpleDateFormat("yyyy-mm-dd hh:mm").format(dataSource[position].timestampEvent)
        val imageView = rowView.findViewById<ImageView>(R.id.notification_cell_image)
        imageView.setImageResource(R.drawable.info)
        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

}