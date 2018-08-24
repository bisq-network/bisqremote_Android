package com.joachimneumann.bisq

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.google.gson.GsonBuilder
import com.joachimneumann.bisq.Database.BisqNotification
import com.joachimneumann.bisq.Database.DateDeserializer
import com.joachimneumann.bisq.Database.NotificationRepository
import com.joachimneumann.bisq.Database.NotificationType
import java.util.*

const val BISQ_MESSAGE_ANDROID_MAGIC = "BisqMessageAndroid"

class BisqNotificationReceiver(val activity: Activity? = null) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Bisq", "notification received");

        if(intent.getAction() != null && intent.getAction().equals(context.getString(R.string.bisq_broadcast))) {

            val notificationMessage = intent.getStringExtra("notificationMessage")

            val array = notificationMessage.split("\\|".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (array.size != 3) return
            if (array[0] != BISQ_MESSAGE_ANDROID_MAGIC) return
            if (array[1].length != 16) return
            val initializationVector = array[1]
            val encryptedJson = array[2]
            if (Phone.instance.key == null) { return }
            Log.i("Bisq", "key = " + Phone.instance.key)
            Log.i("Bisq", "iv = $initializationVector")
            Log.i("Bisq", "encryptedJson = $encryptedJson")
            var success: String? = null
            try {
                val c = CryptoHelper(Phone.instance.key!!)
                success = c.decrypt(encryptedJson, initializationVector)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (success != null) {
                // TODO add to database
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer())
                val gson = gsonBuilder.create()
                val newNotification = gson.fromJson<BisqNotification>(success, BisqNotification::class.java)
                val now = Date()
                newNotification.receivedDate = now.time
                val notificationRepository = NotificationRepository(activity!!)

//                if (localNotification) {
//                    var title = "Bisq"
//                    var body = newNotification.title
//                    val mNotific = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//                    val name = "Bisq"
//                    val desc = "Bisq notifications"
//                    val ChannelID =  activity.getString(R.string.default_notification_channel_id)
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        val mChannel = NotificationChannel(ChannelID, name,
//                                NotificationManager.IMPORTANCE_HIGH)
//                        mChannel.description = desc
//                        mChannel.lightColor = Color.CYAN
//                        mChannel.canShowBadge()
//                        mChannel.setShowBadge(true)
//                        mNotific.createNotificationChannel(mChannel)
//                        val ncode = 101
//
//                        val n = Notification.Builder(activity, ChannelID)
//                                .setContentTitle(title)
//                                .setContentText(body)
//                                .setBadgeIconType(R.mipmap.ic_launcher)
//                                .setNumber(5)
//                                .setSmallIcon(R.mipmap.ic_launcher_round)
//                                .setAutoCancel(true)
//                                .build()
//
//                        // Create pending intent, mention the Activity which needs to be
//                        // triggered when user clicks on notification(StopScript.class in this case)
//                        val notificationIntent: Intent = Intent(activity, ActivityNotificationTable::class.java)
//                        var contentIntent = PendingIntent.getActivity(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//                        n.contentIntent = contentIntent
//                        mNotific.notify(ncode, n);
//                    }
//                }


                when (newNotification.type) {
                    NotificationType.SETUP_CONFIRMATION.name -> {
                        Phone.instance.confirmed = true
                        Phone.instance.saveToPreferences(activity) // only confirmed phones are saved to the preferences
                        if (activity is ActivityRegisterQR) {
                            activity.confirmed()
                        }
                        if (activity is ActivityRegisterEmail) {
                            activity.confirmed()
                        }
                    }
                    NotificationType.ERASE.name -> {
                        notificationRepository.nukeTable()
                        Phone.instance.reset()
                    }
                    else -> {
                        // notification from Bisq
                        notificationRepository.insert(newNotification)
                    }
                }
                Log.i("Bisq", "added to database: $success")
            } else {
                Log.i("Bisq", "ERROR decrypting json: $success")
            }

        }
    }
}