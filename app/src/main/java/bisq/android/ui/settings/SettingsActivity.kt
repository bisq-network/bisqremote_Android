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

package bisq.android.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import bisq.android.BISQ_MOBILE_URL
import bisq.android.BISQ_NETWORK_URL
import bisq.android.R
import bisq.android.database.BisqNotification
import bisq.android.model.Device
import bisq.android.model.NotificationType
import bisq.android.ui.PairedBaseActivity
import bisq.android.ui.notification.NotificationViewModel
import bisq.android.ui.welcome.WelcomeActivity
import java.util.Date

class SettingsActivity : PairedBaseActivity() {

    private lateinit var viewModel: NotificationViewModel
    private lateinit var registerAgainButton: Button
    private lateinit var deleteAllNotificationsButton: Button
    private lateinit var markAllAsReadButton: Button
    private lateinit var addExampleNotificationsButton: Button
    private lateinit var aboutBisqButton: Button
    private lateinit var aboutAppButton: Button
    private lateinit var versionTextView: TextView
    private lateinit var keyTextView: TextView
    private lateinit var tokenTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_settings)

        registerAgainButton = bind(R.id.settingsRegisterAgainButton)
        registerAgainButton.setOnClickListener {
            onRegisterAgainButtonClick()
        }

        deleteAllNotificationsButton = bind(R.id.settingsDeleteAllNotificationsButton)
        deleteAllNotificationsButton.setOnClickListener {
            onDeleteAllNotificationsButtonClick()
        }

        markAllAsReadButton = bind(R.id.settingsMarkAsReadButton)
        markAllAsReadButton.setOnClickListener {
            onMarkAllAsReadButtonClick()
        }

        addExampleNotificationsButton = bind(R.id.settingsAddExampleButton)
        if (Device.instance.isEmulator()) {
            addExampleNotificationsButton.visibility = View.VISIBLE
        }
        addExampleNotificationsButton.setOnClickListener {
            onAddExampleNotificationsButtonClick()
        }

        aboutBisqButton = bind(R.id.settingsAboutBisqButton)
        aboutBisqButton.setOnClickListener {
            onAboutBisqButtonClick()
        }

        aboutAppButton = bind(R.id.settingsAboutAppButton)
        aboutAppButton.setOnClickListener {
            onAboutAppButtonClick()
        }

        versionTextView = bind(R.id.settingsVersionTextView)
        versionTextView.text = getString(R.string.version, getAppVersion())

        keyTextView = bind(R.id.settingsKeyTextView)
        keyTextView.text = getString(
            R.string.key,
            Device.instance.key?.substring(0, 10) + "..."
        )

        tokenTextView = bind(R.id.settingsTokenTextView)
        tokenTextView.text = getString(
            R.string.token,
            Device.instance.token?.substring(0, 10) + "..."
        )
    }

    private fun onAboutAppButtonClick() {
        loadWebPage(BISQ_MOBILE_URL)
    }

    private fun onAboutBisqButtonClick() {
        loadWebPage(BISQ_NETWORK_URL)
    }

    private fun onAddExampleNotificationsButtonClick() {
        addExampleNotifications()
        finish()
    }

    private fun onMarkAllAsReadButtonClick() {
        viewModel.markAllAsRead()
        finish()
    }

    private fun onDeleteAllNotificationsButtonClick() {
        viewModel.nukeTable()
        finish()
    }

    private fun onRegisterAgainButtonClick() {
        viewModel.nukeTable()
        Device.instance.reset()
        Device.instance.clearPreferences(this)
        startActivity(Intent(this, WelcomeActivity::class.java))
    }

    private fun getAppVersion(): String {
        val version = try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            R.string.unknown
        }
        return version.toString()
    }

    private fun addExampleNotifications() {
        for (counter in 1..5) {
            val now = Date()
            val bisqNotification = BisqNotification()
            bisqNotification.receivedDate = now.time + counter * 1000
            bisqNotification.sentDate = bisqNotification.receivedDate - 1000 * 30
            when (counter) {
                1 -> {
                    bisqNotification.type = NotificationType.TRADE.name
                    bisqNotification.title = "(example) Trade confirmed"
                    bisqNotification.message = "The trade with ID 38765384 is confirmed."
                }
                2 -> {
                    bisqNotification.type = NotificationType.OFFER.name
                    bisqNotification.title = "(example) Offer taken"
                    bisqNotification.message = "Your offer with ID 39847534 was taken"
                }
                3 -> {
                    bisqNotification.type = NotificationType.DISPUTE.name
                    bisqNotification.title = "(example) Dispute message"
                    bisqNotification.actionRequired = "Please contact the arbitrator"
                    bisqNotification.message =
                        "You received a dispute message for trade with ID 34059340"
                    bisqNotification.txId = "34059340"
                }
                4 -> {
                    bisqNotification.type = NotificationType.PRICE.name
                    bisqNotification.title = "(example) Price alert for United States Dollar"
                    bisqNotification.message = "Your price alert got triggered. The current" +
                        " United States Dollar price is 35351.08 BTC/USD"
                }
                5 -> {
                    bisqNotification.type = NotificationType.MARKET.name
                    bisqNotification.title = "(example) New offer"
                    bisqNotification.message = "A new offer offer with price 36000 USD" +
                        " (1% above market price) and payment method Zelle was published to" +
                        " the Bisq offerbook.\nThe offer ID is 34534"
                }
            }
            viewModel.insert(bisqNotification)
        }
    }
}
