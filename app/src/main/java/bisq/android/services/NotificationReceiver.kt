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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import bisq.android.R
import bisq.android.database.BisqNotification
import bisq.android.ext.goAsync
import bisq.android.model.Device

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Notification received")

        if (Device.instance.key == null) {
            Log.w(TAG, "Ignoring notification, device does not have a key")
            return
        }

        Log.i(TAG, "Processing notification")
        val bisqNotification: BisqNotification
        try {
            bisqNotification = NotificationProcessor.processNotification(
                intent.extras?.get("encrypted").toString()
            )
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            Intent().also { broadcastIntent ->
                broadcastIntent.action = context.getString(R.string.intent_receiver_action)
                broadcastIntent.putExtra(
                    "error",
                    context.getString(R.string.failed_to_process_notification)
                )
                context.sendBroadcast(broadcastIntent)
            }
            return
        }

        Log.i(TAG, "Handling ${bisqNotification.type} notification")
        goAsync {
            NotificationHandler.handleNotification(bisqNotification, context)
        }
    }
}
