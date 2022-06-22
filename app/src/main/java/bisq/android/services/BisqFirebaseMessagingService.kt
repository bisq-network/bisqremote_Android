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
import android.util.Log
import bisq.android.R
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.ui.welcome.WelcomeActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BisqFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMsgSvc"
        private var tokenBeingFetched: Boolean = false

        fun isGooglePlayServicesAvailable(context: Context): Boolean {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            return resultCode == ConnectionResult.SUCCESS
        }

        fun isFirebaseMessagingInitialized(): Boolean {
            try {
                FirebaseMessaging.getInstance()
            } catch (ignored: IllegalStateException) {
                return false
            }
            return true
        }

        fun isTokenBeingFetched(): Boolean {
            return tokenBeingFetched
        }

        fun fetchFcmToken(onComplete: () -> Unit = {}) {
            if (!isFirebaseMessagingInitialized()) {
                Log.e(TAG, "FirebaseMessaging is not initialized")
                onComplete()
                return
            }
            tokenBeingFetched = true
            FirebaseMessaging.getInstance().apply {
                token.addOnCompleteListener(
                    OnCompleteListener { getTokenTask ->
                        if (!getTokenTask.isSuccessful) {
                            Log.e(TAG, "Fetching FCM token failed: ${getTokenTask.exception}")
                            Device.instance.token = null
                            onComplete()
                            tokenBeingFetched = false
                            return@OnCompleteListener
                        }
                        val token: String? = getTokenTask.result
                        if (token == null) {
                            Log.e(TAG, "FCM token is null")
                            Device.instance.token = null
                            onComplete()
                            tokenBeingFetched = false
                            return@OnCompleteListener
                        }
                        if (Device.instance.token == token) {
                            Log.i(TAG, "FCM token has already been fetched")
                            onComplete()
                            tokenBeingFetched = false
                            return@OnCompleteListener
                        }
                        Device.instance.newToken(token)
                        Log.i(TAG, "New FCM token: $token")
                        Log.i(TAG, "Pairing token: ${Device.instance.pairingToken()}")
                        onComplete()
                        tokenBeingFetched = false
                    }
                )
            }
        }

        @Suppress("ForbiddenComment")
        fun refreshFcmToken(onComplete: () -> Unit = {}) {
            if (!isFirebaseMessagingInitialized()) {
                Log.e(TAG, "FirebaseMessaging is not initialized")
                onComplete()
                return
            }
            // TODO: This works on an emulator, but not on a Pixel 3a running CalyxOS with Android 12
            //  Deleting the FCM token encounters IOException: SERVICE_NOT_AVAILABLE
            //  And fetching the FCM token returns the same token
            //  And trying to use that same token then gets rejected by the FCM gateway -
            //  Requested entity was not found. (UNREGISTERED)
            //  The only way to recover is to uninstall the app or perhaps clear app data
            //  It's possible that this is an issue only with CalyxOS, but leaving this commented
            //  out for now is best until it can be confirmed if it works on a non-CalyxOS device
            //  Ref: https://stackoverflow.com/questions/43193215/firebase-cloud-messaging-handling-logout
//            tokenBeingFetched = true
//            FirebaseMessaging.getInstance().apply {
//                deleteToken().addOnCompleteListener { deleteTokenTask ->
//                    if (!deleteTokenTask.isSuccessful) {
//                        Log.e(TAG, "Deleting FCM token failed: ${deleteTokenTask.exception}")
//                    } else {
//                        Log.i(TAG, "FCM token deleted")
//                    }
//                    fetchFcmToken(onComplete)
//                    tokenBeingFetched = false
//                }
//            }
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
            Device.instance.clearPreferences(this)
            Device.instance.status = DeviceStatus.NEEDS_REPAIR
            Device.instance.newToken(newToken)
            startActivity(Intent(Intent(this, WelcomeActivity::class.java)))
        }
    }
}
