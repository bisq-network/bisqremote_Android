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

import android.content.Context
import android.content.Intent
import bisq.android.Logging
import bisq.android.R
import bisq.android.database.BisqNotification
import bisq.android.database.DebugLogRepository
import bisq.android.database.NotificationRepository
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.model.NotificationType
import bisq.android.services.BisqFirebaseMessagingService.Companion.refreshFcmToken

object NotificationHandler {

    private const val TAG = "NotificationHandler"

    @Suppress("ReturnCount")
    suspend fun handleNotification(bisqNotification: BisqNotification, context: Context) {
        val notificationRepository = NotificationRepository(context)
        val debugRepository = DebugLogRepository(context)

        when (bisqNotification.type) {
            NotificationType.SETUP_CONFIRMATION.name -> {
                Logging().debug(TAG, "Setup confirmation")
                if (Device.instance.token == null) {
                    Logging().error(TAG, "Device token is null")
                    return
                }
                if (Device.instance.key == null) {
                    Logging().error(TAG, "Device key is null")
                    return
                }
                if (Device.instance.status == DeviceStatus.PAIRED) {
                    Logging().warn(TAG, "Device is already paired")
                    return
                }
                Device.instance.status = DeviceStatus.PAIRED
                Device.instance.saveToPreferences(context)
            }
            NotificationType.ERASE.name -> {
                Logging().debug(TAG, "Erase pairing")
                Device.instance.reset()
                Device.instance.clearPreferences(context)
                notificationRepository.deleteAll()
                debugRepository.deleteAll()
                Device.instance.status = DeviceStatus.REMOTE_ERASED
                refreshFcmToken()
            }

            null -> {
                Logging().error(TAG, "Notification type is null: $bisqNotification")
            }
            else -> {
                Logging().debug(TAG, "Inserting ${bisqNotification.type} notification to repository")
                notificationRepository.insert(bisqNotification)
            }
        }

        Logging().debug(TAG, "Broadcasting " + context.getString(R.string.intent_receiver_action))
        Intent().also { broadcastIntent ->
            broadcastIntent.action = context.getString(R.string.intent_receiver_action)
            broadcastIntent.putExtra("type", bisqNotification.type)
            context.sendBroadcast(broadcastIntent)
        }
    }
}
