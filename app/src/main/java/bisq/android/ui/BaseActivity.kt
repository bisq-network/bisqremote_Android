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
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate ${this::class.simpleName}")
        super.onCreate(savedInstanceState)
        registerNotificationReceiver()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy ${this::class.simpleName}")
        super.onDestroy()
        unregisterNotificationReceiver()
    }

    override fun onPause() {
        Log.d(TAG, "onPause ${this::class.simpleName}")
        super.onPause()
        unregisterIntentReceiver()
    }

    override fun onResume() {
        Log.d(TAG, "onResume ${this::class.simpleName}")
        super.onResume()
        registerIntentReceiver()
    }

    private fun registerNotificationReceiver() {
        Log.d(TAG, "Registering notification receiver for ${this::class.simpleName}")
        if (notificationReceiver != null) {
            Log.d(TAG, "Notification receiver already registered")
            return
        }
        notificationReceiver = NotificationReceiver()
        val filter = IntentFilter()
        filter.addAction(getString(R.string.notification_receiver_action))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(notificationReceiver, filter)
        }
        Log.d(TAG, "Notification receiver registered for ${this::class.simpleName}")
    }

    private fun unregisterNotificationReceiver() {
        Log.d(TAG, "Unregistering notification receiver for ${this::class.simpleName}")
        if (notificationReceiver == null) {
            Log.d(TAG, "Notification receiver already unregistered")
            return
        }
        try {
            unregisterReceiver(notificationReceiver)
        } catch (ignored: IllegalArgumentException) {
            // Receiver not registered, do nothing
        }
        notificationReceiver = null
        Log.d(TAG, "Notification receiver unregistered for ${this::class.simpleName}")
    }

    private fun registerIntentReceiver() {
        Log.d(TAG, "Registering intent receiver for ${this::class.simpleName}")
        if (intentReceiver != null) {
            Log.d(TAG, "Intent receiver already registered")
            return
        }
        intentReceiver = IntentReceiver(this)
        val filter = IntentFilter()
        filter.addAction(getString(R.string.intent_receiver_action))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(intentReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(intentReceiver, filter)
        }
        Log.d(TAG, "Intent receiver registered for ${this::class.simpleName}")
    }

    private fun unregisterIntentReceiver() {
        Log.d(TAG, "Unregistering intent receiver for ${this::class.simpleName}")
        if (intentReceiver == null) {
            Log.d(TAG, "Intent receiver already unregistered")
            return
        }
        try {
            unregisterReceiver(intentReceiver)
        } catch (ignored: IllegalArgumentException) {
            // Receiver not registered, do nothing
        }
        intentReceiver = null
        Log.d(TAG, "Intent receiver unregistered for ${this::class.simpleName}")
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
