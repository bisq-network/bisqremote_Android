package com.joachimneumann.bisq

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.joachimneumann.bisq.Database.BisqNotification
import com.joachimneumann.bisq.Database.DateDeserializer
import com.joachimneumann.bisq.Database.NotificationRepository

import java.util.Date


class BisqFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        val notificationMessage = remoteMessage!!.data["encrypted"]
        if (notificationMessage != null) {
            processNotification(notificationMessage)
        }
    }

    private fun processNotification(notificationMessage: String) {
        val initializationVector: String
        val encryptedJson: String
        val array = notificationMessage.split("\\|".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        if (array.size == 3) {
            if (array[0] == BISQ_MESSAGE_ANDROID_MAGIC) {
                if (array[1].length == 16) {
                    initializationVector = array[1]
                    encryptedJson = array[2]
                    val phone = Phone.getInstance(this)
                    Log.i("Bisq", "key = " + phone.key)
                    Log.i("Bisq", "iv = $initializationVector")
                    Log.i("Bisq", "encryptedJson = $encryptedJson")
                    var success: String? = null
                    try {
                        success = phone.decrypt(encryptedJson, initializationVector)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (success != null) {
                        if (success.startsWith("{\"times")) {
                            // TODO add to database
                            val gsonBuilder = GsonBuilder()
                            gsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer())
                            val gson = gsonBuilder.create()
                            val newNotification = gson.fromJson<BisqNotification>(success, BisqNotification::class.java)
                            val notificationRepository = NotificationRepository(this)
                            notificationRepository.insert(newNotification)

                            val intent = Intent(this, ActivityRegister::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            // Create the pending intent to launch the activity
                            val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                    PendingIntent.FLAG_ONE_SHOT)


                            val notificationBuilder = NotificationCompat.Builder(this, "Bisq")
                                    .setSmallIcon(R.drawable.help)
                                    .setContentTitle("bisq")
                                    .setContentText(success)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)

                            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
                            Log.i("Bisq", "added to database: $success")
                        } else {
                            Log.i("Bisq", "ERROR decrypting json: $success")
                        }
                    }
                }
            }
        }
    }

    companion object {
        val BISQ_MESSAGE_ANDROID_MAGIC = "BisqMessageAndroid"
    }
}
