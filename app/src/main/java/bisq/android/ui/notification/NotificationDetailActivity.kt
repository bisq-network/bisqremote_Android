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

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import bisq.android.R
import bisq.android.database.BisqNotification
import bisq.android.ui.PairedBaseActivity
import bisq.android.util.DateUtil

class NotificationDetailActivity : PairedBaseActivity() {

    private lateinit var viewModel: NotificationViewModel
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var action: TextView
    private lateinit var eventTime: TextView
    private lateinit var receivedTime: TextView
    private lateinit var deleteButton: Button

    override fun getRootLayoutId() = R.id.notification_detail_layout
    override fun getStatusBarScrimId() = R.id.notification_detail_status_bar_background

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        super.onCreate(savedInstanceState)

        val notification = getNotification() ?: return

        updateView(notification)

        viewModel.markAsRead(notification.uid)
    }

    private fun getNotification(): BisqNotification? {
        val uid = intent.getIntExtra("uid", 0)
        return viewModel.getFromUid(uid)
    }

    override fun initView() {
        setContentView(R.layout.activity_notification_detail)
        title = bind(R.id.notification_detail_title)
        message = bind(R.id.notification_detail_message)
        action = bind(R.id.notification_detail_action)
        eventTime = bind(R.id.notification_detail_event_time)
        receivedTime = bind(R.id.notification_detail_received_time)
        deleteButton = bind(R.id.notification_delete_button)
        deleteButton.setOnClickListener {
            getNotification()?.let { notification -> viewModel.delete(notification) }
            finish()
        }
    }

    private fun updateView(notification: BisqNotification) {
        title.text = notification.title

        if (notification.message != null && notification.message!!.isNotEmpty()) {
            message.text = notification.message
        } else {
            message.visibility = View.GONE
        }

        if (notification.actionRequired != null && notification.actionRequired!!.isNotEmpty()) {
            action.text = notification.actionRequired
        } else {
            action.visibility = View.GONE
        }

        eventTime.text = if (notification.sentDate > 0) {
            getString(R.string.event_occurred_at, DateUtil.format(notification.sentDate))
        } else {
            ""
        }
        receivedTime.text = if (notification.receivedDate > 0) {
            getString(R.string.event_received_at, DateUtil.format(notification.receivedDate))
        } else {
            ""
        }
    }
}
