package com.joachimneumann.bisq

import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService


class BisqFirebaseInstanceIdService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)
        Phone.instance.token = newToken
    }

}
