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

package bisq.android.ui

import android.app.Activity
import android.content.IntentFilter
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import bisq.android.R
import bisq.android.services.IntentReceiver
import bisq.android.services.NotificationReceiver

open class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivity"
        private var notificationReceiver: NotificationReceiver? = null
    }

    private var intentReceiver: IntentReceiver? = null

    fun <T : View> Activity.bind(@IdRes res: Int): T {
        return findViewById(res)
    }

    override fun onStart() {
        super.onStart()
        registerIntentReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterIntentReceiver()
    }

    protected fun registerNotificationReceiver() {
        Log.i(TAG, "Registering notification receiver")
        if (notificationReceiver != null) {
            Log.i(TAG, "Notification receiver already registered")
            return
        }
        notificationReceiver = NotificationReceiver()
        val filter = IntentFilter()
        filter.addAction(getString(R.string.notification_receiver_action))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(notificationReceiver, filter)
        }
        Log.i(TAG, "Notification receiver registered")
    }

    protected fun unregisterNotificationReceiver() {
        Log.i(TAG, "Unregistering notification receiver")
        if (notificationReceiver == null) {
            Log.i(TAG, "Notification receiver already unregistered")
            return
        }
        try {
            unregisterReceiver(notificationReceiver)
        } catch (ignored: IllegalArgumentException) {
            // Receiver not registered, do nothing
        }
        notificationReceiver = null
        Log.i(TAG, "Notification receiver unregistered")
    }

    protected fun registerIntentReceiver() {
        Log.i(TAG, "Registering intent receiver")
        if (intentReceiver != null) {
            Log.i(TAG, "Intent receiver already registered")
            return
        }
        intentReceiver = IntentReceiver(this)
        val filter = IntentFilter()
        filter.addAction(getString(R.string.intent_receiver_action))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(intentReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(intentReceiver, filter)
        }
        Log.i(TAG, "Intent receiver registered")
    }

    protected fun unregisterIntentReceiver() {
        Log.i(TAG, "Unregistering intent receiver")
        if (intentReceiver == null) {
            Log.i(TAG, "Intent receiver already unregistered")
            return
        }
        try {
            unregisterReceiver(intentReceiver)
        } catch (ignored: IllegalArgumentException) {
            // Receiver not registered, do nothing
        }
        intentReceiver = null
        Log.i(TAG, "Intent receiver unregistered")
    }

    protected fun playTone() {
        @Suppress("TooGenericExceptionCaught")
        try {
            val notificationTone =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            MediaPlayer.create(applicationContext, notificationTone).start()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to play notification tone", e)
        }
    }
}
