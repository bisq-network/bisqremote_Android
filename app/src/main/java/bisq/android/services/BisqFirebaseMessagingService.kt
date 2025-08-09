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
import bisq.android.Application
import bisq.android.Application.Companion.isAppInBackground
import bisq.android.Logging
import bisq.android.R
import bisq.android.database.BisqNotification
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.ui.notification.NotificationSender
import bisq.android.ui.welcome.WelcomeActivity
import bisq.android.util.MaskingUtil.maskSensitive
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BisqFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMsgSvc"
        private var tokenBeingFetched: Boolean = false

        fun isGooglePlayServicesAvailable(context: Context): Boolean {
            val googleApiAvailability = GoogleApiAvailabilityLight.getInstance()
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

        fun isTokenBeingFetched(): Boolean = tokenBeingFetched

        fun fetchFcmToken(onComplete: () -> Unit = {}) {
            if (!isFirebaseMessagingInitialized()) {
                Logging().error(TAG, "FirebaseMessaging is not initialized")
                onComplete()
                return
            }
            tokenBeingFetched = true
            FirebaseMessaging.getInstance().apply {
                token.addOnCompleteListener(
                    OnCompleteListener { getTokenTask ->
                        if (!getTokenTask.isSuccessful) {
                            Logging().error(TAG, "Fetching FCM token failed: ${getTokenTask.exception}")
                            Device.instance.token = null
                            onComplete()
                            tokenBeingFetched = false
                            return@OnCompleteListener
                        }
                        val token: String? = getTokenTask.result
                        if (token == null) {
                            Logging().error(TAG, "FCM token is null")
                            Device.instance.token = null
                            onComplete()
                            tokenBeingFetched = false
                            return@OnCompleteListener
                        }
                        if (Device.instance.token == token) {
                            Logging().info(TAG, "FCM token has already been fetched")
                            onComplete()
                            tokenBeingFetched = false
                            return@OnCompleteListener
                        }
                        Device.instance.newToken(token)
                        Logging().info(TAG, "New FCM token: ${maskSensitive(token)}")
                        Logging().info(TAG, "Pairing token: ${maskSensitive(Device.instance.pairingToken())}")
                        onComplete()
                        tokenBeingFetched = false
                    }
                )
            }
        }

        @Suppress("ForbiddenComment")
        fun refreshFcmToken(onComplete: () -> Unit = {}) {
            if (!isFirebaseMessagingInitialized()) {
                Logging().error(TAG, "FirebaseMessaging is not initialized")
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
//                        Logging().error(TAG, "Deleting FCM token failed: ${deleteTokenTask.exception}")
//                    } else {
//                        Logging().debug(TAG, "FCM token deleted")
//                    }
//                    fetchFcmToken(onComplete)
//                    tokenBeingFetched = false
//                }
//            }
        }
    }

    /*
     * Firebase notifications behave differently depending on the foreground/background state of the receiving app
     * and whether the message contains notification data or not.
     * For more details, see https://firebase.google.com/docs/cloud-messaging/android/receive.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Logging().debug(TAG, "Message received")
        super.onMessageReceived(remoteMessage)

        val encryptedData = remoteMessage.data["encrypted"]
        if (encryptedData == null) {
            Logging().warn(TAG, "Received message does not contain encrypted data; ${remoteMessage.data}")
            return
        }

        if (remoteMessage.notification != null) {
            // If the message contains notification data, then this method is only called while the app is in
            // the foreground. Since the app is running and the NotificationReceiver should be registered, only
            // need to broadcast the notification so the NotificationReceiver can process it.
            Logging().debug(
                TAG,
                "Notification message received, broadcasting " + getString(R.string.notification_receiver_action)
            )
            Intent().also { broadcastIntent ->
                broadcastIntent.action = getString(R.string.notification_receiver_action)
                broadcastIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                broadcastIntent.putExtra("encrypted", encryptedData)
                sendBroadcast(broadcastIntent)
            }
        } else {
            // Otherwise, if the message does not contain notification data, then this method is called when the app
            // is in the foreground or background. The NotificationReceiver may not be registered if the app is in the
            // background, so cannot simply broadcast the notification. Instead, send it directly to the
            // NotificationReceiver.
            Logging().debug(TAG, "Data message received")

            Intent().also { notificationIntent ->
                notificationIntent.putExtra(
                    "encrypted",
                    encryptedData
                )
                NotificationReceiver().onReceive(Application.applicationContext(), notificationIntent)
            }

            // Since this is a data-only message, will need to show a notification if the app is not in the foreground
            if (isAppInBackground()) {
                processNotification(encryptedData)?.let { bisqNotification ->
                    NotificationSender.sendNotification(
                        bisqNotification.title ?: getString(R.string.you_have_received_notification),
                        bisqNotification.message
                    )
                } ?: NotificationSender.sendNotification(getString(R.string.you_have_received_notification), null)
            }
        }
    }

    private fun processNotification(encryptedData: String): BisqNotification? = try {
        NotificationProcessor.processNotification(encryptedData)
    } catch (e: ProcessingException) {
        e.message?.let { Logging().error(TAG, it) }
        null
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
            Logging().info(
                TAG,
                "New FCM token received, app needs to be re-paired: ${maskSensitive(newToken)}"
            )
            Device.instance.reset()
            Device.instance.clearPreferences(this)
            Device.instance.status = DeviceStatus.NEEDS_REPAIR
            Device.instance.newToken(newToken)
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}
