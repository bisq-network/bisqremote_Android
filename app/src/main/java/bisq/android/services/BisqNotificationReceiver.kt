package bisq.android.services

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import bisq.android.R
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.model.NotificationType
import bisq.android.ui.PairedBaseActivity
import bisq.android.ui.UnpairedBaseActivity

class BisqNotificationReceiver(private val activity: Activity? = null) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == null || !intent.action.equals("bisqNotification")) {
            return
        }

        if (intent.hasExtra("error")) {
            Toast.makeText(context, intent.getStringExtra("error"), Toast.LENGTH_LONG).show()
            return
        }

        if (intent.hasExtra("type")) {
            val type = intent.getStringExtra("type")
            if (type == NotificationType.SETUP_CONFIRMATION.name && activity is UnpairedBaseActivity) {
                activity.pairingConfirmed()
            } else if (type == NotificationType.ERASE.name && activity is PairedBaseActivity) {
                Device.instance.status = DeviceStatus.UNPAIRED
                activity.pairingRemoved(context.getString(R.string.pairing_erased))
            }
        }
    }

}
