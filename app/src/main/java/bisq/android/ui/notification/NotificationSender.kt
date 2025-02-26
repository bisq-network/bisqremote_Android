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

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import bisq.android.Application
import bisq.android.Logging
import bisq.android.R
import java.util.Date

object NotificationSender {
    private const val TAG = "NotificationSender"
    private const val SUMMARY_NOTIFICATION_TAG = "summary"

    fun sendNotification(contentTitle: String, contentText: String?) {
        val context = Application.applicationContext()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Logging().warn(TAG, "*** Unable to send notification; POST_NOTIFICATIONS permission not granted")
            return
        }

        val intent = Intent(context, NotificationTableActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val requestCode = 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val groupKey = context.getString(R.string.default_notification_group_key)
        val channelId = context.getString(R.string.default_notification_channel_id)
        val notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bisq_mark)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setGroup(groupKey)
            .build()

        val summaryNotification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bisq_mark)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .build()

        // Get the current active notifications, other than summary notification
        val activeNotifications = notificationManager.activeNotifications.filter {
            it.groupKey.endsWith(groupKey) && it.tag != SUMMARY_NOTIFICATION_TAG
        }

        // Post the summary notification only if there are already previous notifications.
        // In other words, only if there are multiple notifications shown will they be grouped.
        if (activeNotifications.isNotEmpty()) {
            // The summary notification tag/id pair must stay the same so that it's only posted once
            notificationManager.notify(SUMMARY_NOTIFICATION_TAG, 0, summaryNotification)

            // Previous notifications must be updated to prevent an issue where the first notification
            // received is not included in the summary group, while subsequent notifications are included.
            // So as to prevent unnecessary updates, this only needs to be done if there are 2 or less
            // active notifications.
            if (activeNotifications.size <= 2) {
                activeNotifications.forEach { activeNotification ->
                    notificationManager.cancel(activeNotification.tag, activeNotification.id)
                    notificationManager.notify(
                        activeNotification.tag,
                        activeNotification.id,
                        activeNotification.notification
                    )
                }
            }
        }

        // Post the new notification.
        // Each notification must have a unique id or combination of tag/id pair.
        // Using the current timestamp down to the millisecond should ensure uniqueness.
        // Since the id only accepts an int, which does not have enough resolution, use the timestamp as the tag.
        notificationManager.notify(Date().time.toString(), activeNotifications.size + 1, notification)
    }
}
