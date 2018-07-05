package com.joachimneumann.bisq

import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService


class BisqFirebaseInstanceIdService : FirebaseMessagingService() {

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d(TAG, "Refreshed token: " + p0)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        val phone = Phone.getInstance(this)
        phone.apsToken = p0
    }

    companion object {
        private val TAG = "Bisq"
    }

}
