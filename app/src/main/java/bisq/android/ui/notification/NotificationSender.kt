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
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import bisq.android.Application
import bisq.android.R
import java.util.Date

object NotificationSender {
    private const val TAG = "NotificationSender"
    private const val MILLISECONDS_PER_SECOND = 1000L

    fun sendNotification(contentTitle: String, contentText: String?) {
        if (ActivityCompat.checkSelfPermission(
                Application.applicationContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "*** Unable to send notification; POST_NOTIFICATIONS permission not granted")
            return
        }

        val intent = Intent(Application.applicationContext(), NotificationTableActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val requestCode = 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            Application.applicationContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = NotificationCompat.Builder(
            Application.applicationContext(),
            Application.applicationContext().getString(R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.bisq_mark)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setGroup(Application.applicationContext().getString(R.string.default_notification_group_key))
            .build()

        val summaryNotification = NotificationCompat.Builder(
            Application.applicationContext(),
            Application.applicationContext().getString(R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.bisq_mark)
            .setGroup(Application.applicationContext().getString(R.string.default_notification_group_key))
            .setGroupSummary(true)
            .build()

        // TODO investigate potential issue where first notification received is not
        //  included in summary group, while subsequent notifications are included
        NotificationManagerCompat.from(Application.applicationContext()).apply {
            // The notification ID must be unique for each notification
            val notificationId = (Date().time / MILLISECONDS_PER_SECOND % Int.MAX_VALUE).toInt()
            notify(notificationId, notification)

            // The summary notification ID must stay the same so that it's only posted once
            notify(0, summaryNotification)
        }
    }
}
