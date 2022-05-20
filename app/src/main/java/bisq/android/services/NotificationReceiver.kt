package bisq.android.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import bisq.android.database.NotificationRepository
import bisq.android.ext.goAsync
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.model.NotificationMessage
import bisq.android.model.NotificationType
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Device.instance.key == null || intent.action == null) {
            return
        }

        Log.i(TAG, "Notification received")
        val encryptedMessage = intent.extras?.get("encrypted").toString()
        processNotification(encryptedMessage, context)
    }

    private fun processNotification(encryptedMessage: String?, context: Context) {
        val notificationMessage: NotificationMessage
        try {
            notificationMessage = NotificationMessage(encryptedMessage)
        } catch (e: Exception) {
            Intent().also { intent ->
                intent.action = "bisqNotification"
                intent.putExtra("error", "${e.message}; try pairing again")
                context.sendBroadcast(intent)
            }
            abortBroadcast()
            return
        }
        val bisqNotification = notificationMessage.bisqNotification
        bisqNotification.receivedDate = Date().time

        Log.i(TAG, "${bisqNotification.type} notification")

        val notificationRepository = NotificationRepository(context)

        when (bisqNotification.type) {
            NotificationType.SETUP_CONFIRMATION.name -> {
                if (Device.instance.token == null) {
                    Log.e(TAG, "Device token is null")
                    return
                }
                if (Device.instance.key == null) {
                    Log.e(TAG, "Device key is null")
                    return
                }
                if (Device.instance.status == DeviceStatus.PAIRED) {
                    Log.w(TAG, "Device is already paired")
                    return
                }
                Device.instance.status = DeviceStatus.PAIRED
                Device.instance.saveToPreferences(context)
                Log.i(TAG, "Setup confirmed")
            }
            NotificationType.ERASE.name -> {
                goAsync {
                    notificationRepository.deleteAll()
                }
                Device.instance.reset()
                Device.instance.clearPreferences(context)
                Device.instance.status = DeviceStatus.ERASED
                Log.i(TAG, "Pairing erased")
            }
            else -> {
                goAsync {
                    notificationRepository.insert(bisqNotification)
                    Log.i(TAG, "Notification inserted")
                }
            }
        }

        Intent().also { intent ->
            intent.action = "bisqNotification"
            intent.putExtra("type", bisqNotification.type)
            context.sendBroadcast(intent)
        }

    }

}
