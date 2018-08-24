package com.joachimneumann.bisq

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class BisqFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage != null) {
            val notificationMessage = remoteMessage.data["encrypted"]
            if (notificationMessage != null) {
                val broadcastIntent = Intent()
                broadcastIntent.action = this.getString(R.string.bisq_broadcast)
                broadcastIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                broadcastIntent.putExtra("notificationMessage", notificationMessage)
                sendBroadcast(broadcastIntent)
            }
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}
