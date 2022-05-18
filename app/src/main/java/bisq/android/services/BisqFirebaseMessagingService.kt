package bisq.android.services

import android.content.Intent
import android.util.Log
import bisq.android.model.Device
import bisq.android.ui.welcome.WelcomeActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class BisqFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMsgSvc"

        fun fetchFcmToken(onComplete: () -> Unit = {}) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
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
            })
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
        if (Device.instance.readFromPreferences(this)) {
            Log.i(TAG, "New FCM token received, app needs to be re-paired: $newToken")
            Device.instance.reset()
            Device.instance.confirmed = true
            Device.instance.clearPreferences(this)
            startActivity(Intent(Intent(this, WelcomeActivity::class.java)))
        }
    }

}
