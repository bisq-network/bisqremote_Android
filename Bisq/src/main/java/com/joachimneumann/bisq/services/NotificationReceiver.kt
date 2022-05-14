package com.joachimneumann.bisq.services

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.joachimneumann.bisq.R
import com.joachimneumann.bisq.database.NotificationRepository
import com.joachimneumann.bisq.ext.goAsync
import com.joachimneumann.bisq.model.Device
import com.joachimneumann.bisq.model.NotificationMessage
import com.joachimneumann.bisq.model.NotificationType
import com.joachimneumann.bisq.ui.PairedBaseActivity
import com.joachimneumann.bisq.ui.UnpairedBaseActivity
import java.util.*

const val BISQ_MESSAGE_ANDROID_MAGIC = "BisqMessageAndroid"

class NotificationReceiver(private val activity: Activity? = null) : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Device.instance.key == null ||
            intent.action == null ||
            !intent.action.equals(context.getString(R.string.bisq_broadcast))
        ) {
            return
        }

        Log.i(TAG, "Notification received")
        processNotification(intent.getStringExtra("notificationMessage"))
    }

    private fun processNotification(notification: String?) {
        val notificationMessage: NotificationMessage
        try {
            notificationMessage = NotificationMessage(notification)
        } catch (e: Exception) {
            Toast.makeText(activity, "${e.message}; try pairing again", Toast.LENGTH_LONG).show()
            return
        }
        val bisqNotification = notificationMessage.bisqNotification
        bisqNotification.receivedDate = Date().time

        Log.i(TAG, "${bisqNotification.type} notification")

        val notificationRepository = NotificationRepository(activity!!)

        when (bisqNotification.type) {
            NotificationType.SETUP_CONFIRMATION.name -> {
                if (Device.instance.key != null &&
                    Device.instance.token != null &&
                    activity is UnpairedBaseActivity
                ) {
                    Device.instance.confirmed = true
                    Device.instance.saveToPreferences(activity)
                    activity.pairingConfirmed()
                    Log.i(TAG, "Setup confirmed")
                }
            }
            NotificationType.ERASE.name -> {
                if (activity is PairedBaseActivity) {
                    goAsync {
                        notificationRepository.deleteAll()
                    }
                    Device.instance.reset()
                    Device.instance.clearPreferences(activity)
                    activity.pairingRemoved(activity.getString(R.string.pairing_erased))
                    Log.i(TAG, "Pairing erased")
                }
            }
            else -> {
                goAsync {
                    notificationRepository.insert(bisqNotification)
                    Log.i(TAG, "Notification inserted")
                }
            }
        }
    }

}
