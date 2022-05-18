package bisq.android.ui.notification

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import bisq.android.R
import bisq.android.database.BisqNotification
import bisq.android.model.NotificationType
import bisq.android.util.DateUtil

class NotificationAdapter(
    private var notifications: List<BisqNotification>,
    private val clickListener: (BisqNotification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    companion object {
        var font: Typeface? = null
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        when (notification.type) {
            NotificationType.TRADE.name -> holder.icon.setText(R.string.icon_trade)
            NotificationType.OFFER.name -> holder.icon.setText(R.string.icon_offer)
            NotificationType.DISPUTE.name -> holder.icon.setText(R.string.icon_dispute)
            NotificationType.PRICE.name -> holder.icon.setText(R.string.icon_price)
            NotificationType.MARKET.name -> holder.icon.setText(R.string.icon_market)
            else -> holder.icon.setText(R.string.icon_unknown)
        }

        holder.title.text = notification.title
        holder.time.text = DateUtil.format(notification.sentDate)
        holder.read = notification.read
        if (notification.read) {
            holder.icon.setTextColor(
                ContextCompat.getColor(
                    holder.icon.context,
                    R.color.primary_disabled
                )
            )
            holder.title.setTextColor(
                ContextCompat.getColor(
                    holder.icon.context,
                    R.color.read_title
                )
            )
        } else {
            holder.icon.setTextColor(ContextCompat.getColor(holder.icon.context, R.color.primary))
            holder.title.setTextColor(
                ContextCompat.getColor(
                    holder.icon.context,
                    R.color.unread_title
                )
            )
        }
        holder.bind(notifications[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_cell, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun uid(position: Int): Int {
        return notifications[position].uid
    }

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: TextView
        var title: TextView
        var time: TextView
        var read: Boolean = false

        fun bind(part: BisqNotification, clickListener: (BisqNotification) -> Unit) {
            itemView.setOnClickListener { clickListener(part) }
        }

        init {
            if (font == null) {
                font = Typeface.createFromAsset(
                    view.context.assets,
                    "Font Awesome 5 Free-Solid-900.otf"
                )
            }

            this.icon = view.findViewById(R.id.notification_cell_image) as TextView
            this.icon.typeface = font
            this.icon.textSize = 33F
            this.title = view.findViewById<View>(R.id.notification_cell_title) as TextView
            this.time = view.findViewById<View>(R.id.notification_cell_time) as TextView
        }
    }

}
