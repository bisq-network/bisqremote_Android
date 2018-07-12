package com.joachimneumann.bisq.Database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.joachimneumann.bisq.R

class NotificationAdapter(private val context: Context,
                    private val dataSource: List<RawBisqNotification>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.simple_list_item_1 , parent, false)
        val titleTextView = rowView.findViewById(R.id.notification_cell_title) as TextView
        titleTextView.text = dataSource[position].title
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