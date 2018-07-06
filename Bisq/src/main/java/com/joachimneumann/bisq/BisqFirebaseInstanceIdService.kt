package com.joachimneumann.bisq

import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService


class BisqFirebaseInstanceIdService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)
        Log.d(TAG, "Refreshed token: " + newToken)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        val phone = Phone.getInstance(this)
        phone.apsToken = newToken
    }

    companion object {
        private val TAG = "Bisq"
    }

}
