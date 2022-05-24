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

package bisq.android.services

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import bisq.android.R
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.model.NotificationType
import bisq.android.ui.PairedBaseActivity
import bisq.android.ui.UnpairedBaseActivity

class BisqNotificationReceiver(private val activity: Activity? = null) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == null || !intent.action.equals("bisqNotification")) {
            return
        }

        if (intent.hasExtra("error")) {
            Toast.makeText(context, intent.getStringExtra("error"), Toast.LENGTH_LONG).show()
            return
        }

        if (intent.hasExtra("type")) {
            val type = intent.getStringExtra("type")
            if (type == NotificationType.SETUP_CONFIRMATION.name && activity is UnpairedBaseActivity) {
                activity.pairingConfirmed()
            } else if (type == NotificationType.ERASE.name && activity is PairedBaseActivity) {
                Device.instance.status = DeviceStatus.UNPAIRED
                activity.pairingRemoved(context.getString(R.string.pairing_erased))
            }
        }
    }
}
