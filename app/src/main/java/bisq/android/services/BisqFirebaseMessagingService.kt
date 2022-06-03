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

import android.content.Intent
import android.util.Log
import bisq.android.R
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.ui.welcome.WelcomeActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BisqFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMsgSvc"

        fun isFirebaseMessagingInitialized(): Boolean {
            try {
                FirebaseMessaging.getInstance()
            } catch (e: IllegalStateException) {
                return false
            }
            return true
        }

        fun fetchFcmToken(onComplete: () -> Unit = {}) {
            if (!isFirebaseMessagingInitialized()) {
                Log.e(TAG, "FirebaseMessaging is not initialized")
                onComplete()
                return
            }
            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e(TAG, "Fetching FCM token failed: " + task.exception)
                        onComplete()
                        return@OnCompleteListener
                    }
                    val token: String? = task.result
                    if (token == null) {
                        Log.e(TAG, "FCM token is null")
                        onComplete()
                        return@OnCompleteListener
                    }
                    Device.instance.newToken(token)
                    Log.i(TAG, "FCM token: $token")
                    Log.i(TAG, "Pairing token: " + Device.instance.pairingToken())
                    onComplete()
                }
            )
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "Message received")
        super.onMessageReceived(remoteMessage)
        val notificationMessage = remoteMessage.data["encrypted"]
        if (notificationMessage != null) {
            Log.i(TAG, "Broadcasting " + getString(R.string.notification_receiver_action))
            Intent().also { broadcastIntent ->
                broadcastIntent.action = getString(R.string.notification_receiver_action)
                broadcastIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                broadcastIntent.putExtra("encrypted", notificationMessage)
                sendBroadcast(broadcastIntent)
            }
        }
    }

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     *
     * If the app has already been paired, force the user to re-pair so that they update their
     * Bisq desktop application with the new token, without losing existing received notifications.
     */
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        if (Device.instance.readFromPreferences(this)) {
            Log.i(TAG, "New FCM token received, app needs to be re-paired: $newToken")
            Device.instance.reset()
            Device.instance.status = DeviceStatus.NEEDS_REPAIR
            Device.instance.clearPreferences(this)
            startActivity(Intent(Intent(this, WelcomeActivity::class.java)))
        }
    }
}
