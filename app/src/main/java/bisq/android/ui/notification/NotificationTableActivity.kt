/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.android.ui.notification

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bisq.android.R
import bisq.android.database.BisqNotification
import bisq.android.model.Device
import bisq.android.model.NotificationType
import bisq.android.ui.DialogBuilder
import bisq.android.ui.PairedBaseActivity
import bisq.android.ui.settings.SettingsActivity
import java.util.Date

@Suppress("TooManyFunctions")
class NotificationTableActivity : PairedBaseActivity() {

    private lateinit var viewModel: NotificationViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    private var scrollFirstVisibleItemPosition: Int = 0
    private var scrollLastVisibleItemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        initView()
    }

    // TODO close all data-only notifications
    override fun onStart() {
        super.onStart()
        viewModel.bisqNotifications.observe(this) { bisqNotifications ->
            updateView(
                bisqNotifications!!
            )
        }
    }

    private fun initView() {
        setContentView(R.layout.activity_notification_table)

        toolbar = bind(R.id.notification_table_toolbar)
        setSupportActionBar(toolbar)

        recyclerView = bind(R.id.notification_table_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as NotificationAdapter
                viewModel.delete(viewModel.getFromUid(adapter.uid(viewHolder.bindingAdapterPosition))!!)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun updateView(bisqNotifications: List<BisqNotification>) {
        recyclerView.adapter = NotificationAdapter(bisqNotifications) { item: BisqNotification ->
            onNotificationClicked(item)
        }
    }

    override fun onResume() {
        super.onResume()
        scrollToPreviousPosition()
    }

    private fun scrollToPreviousPosition() {
        // This is a hack so that the scrollview lands sort of where it belongs,
        // which is close to the previously selected notification.
        recyclerView.scrollToPosition(scrollLastVisibleItemPosition)
        if (scrollFirstVisibleItemPosition == 0) {
            recyclerView.scrollToPosition(scrollFirstVisibleItemPosition)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        if (Device.instance.isEmulator()) {
            menu.setGroupVisible(R.id.debug, true)
        } else {
            menu.setGroupVisible(R.id.debug, false)
        }
        viewModel.bisqNotifications.observe(this) { bisqNotifications ->
            if (bisqNotifications.isEmpty()) {
                menu.setGroupEnabled(R.id.notifications, false)
            } else {
                menu.setGroupEnabled(R.id.notifications, true)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_example_notifications -> {
                addExampleNotifications()
            }

            R.id.action_mark_all_read -> {
                viewModel.markAllAsRead()
            }

            R.id.action_delete_all -> {
                DialogBuilder.choicePrompt(
                    this,
                    getString(R.string.confirm),
                    getString(R.string.delete_all_notifications_confirmation),
                    getString(R.string.yes),
                    getString(R.string.no),
                    { _, _ ->
                        viewModel.nukeTable()
                    }
                ).show()
            }

            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }

    private fun onNotificationClicked(item: BisqNotification) {
        val intent = Intent(this, NotificationDetailActivity::class.java)
        intent.putExtra("uid", item.uid)

        rememberScrollPosition()

        startActivity(intent)
    }

    private fun rememberScrollPosition() {
        val llm = recyclerView.layoutManager as LinearLayoutManager
        scrollFirstVisibleItemPosition = llm.findFirstVisibleItemPosition()
        scrollLastVisibleItemPosition = llm.findLastVisibleItemPosition()
    }

    @Suppress("MagicNumber")
    private fun addExampleNotifications() {
        for (counter in 1..5) {
            val now = Date()
            val bisqNotification = BisqNotification()
            bisqNotification.receivedDate = now.time + counter * 1000
            bisqNotification.sentDate = bisqNotification.receivedDate - 1000 * 30
            when (counter) {
                1 -> {
                    bisqNotification.type = NotificationType.TRADE.name
                    bisqNotification.title = "Trade confirmed"
                    bisqNotification.message = "The trade with ID 38765384 is confirmed."
                }

                2 -> {
                    bisqNotification.type = NotificationType.OFFER.name
                    bisqNotification.title = "Offer taken"
                    bisqNotification.message = "Your offer with ID 39847534 was taken"
                }

                3 -> {
                    bisqNotification.type = NotificationType.DISPUTE.name
                    bisqNotification.title = "Dispute message"
                    bisqNotification.actionRequired = "Please contact the arbitrator"
                    bisqNotification.message =
                        "You received a dispute message for trade with ID 34059340"
                    bisqNotification.txId = "34059340"
                }

                4 -> {
                    bisqNotification.type = NotificationType.PRICE.name
                    bisqNotification.title = "Price alert for United States Dollar"
                    bisqNotification.message = "Your price alert got triggered. The current" +
                        " United States Dollar price is 35351.08 BTC/USD"
                }

                5 -> {
                    bisqNotification.type = NotificationType.MARKET.name
                    bisqNotification.title = "New offer"
                    bisqNotification.message = "A new offer with price 36000 USD" +
                        " (1% above market price) and payment method Zelle was published to" +
                        " the Bisq offerbook.\nThe offer ID is 34534"
                }
            }
            viewModel.insert(bisqNotification)
        }
    }
}

abstract class SwipeToDeleteCallback(context: NotificationTableActivity) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24)
    private val intrinsicWidth = deleteIcon?.intrinsicWidth!!
    private val intrinsicHeight = deleteIcon?.intrinsicHeight!!
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#f44336")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        drawBackground(itemView, dX, c)

        drawDeleteIcon(itemView, itemHeight, c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawBackground(
        itemView: View,
        dX: Float,
        c: Canvas
    ) {
        background.color = backgroundColor
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)
    }

    private fun drawDeleteIcon(
        itemView: View,
        itemHeight: Int,
        c: Canvas
    ) {
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon?.draw(c)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
