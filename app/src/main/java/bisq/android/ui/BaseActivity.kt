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
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import bisq.android.R
import bisq.android.model.Device
import bisq.android.services.BisqNotificationReceiver
import bisq.android.ui.welcome.WelcomeActivity

open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    private var receiver: BisqNotificationReceiver? = null

    fun <T : View> Activity.bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res)
    }

    override fun onStart() {
        super.onStart()
        if (!Device.instance.readFromPreferences(this) && this is PairedBaseActivity) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        clearNotifications()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
        receiver = null
    }

    private fun clearNotifications() {
        val nManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.cancelAll()
    }

    private fun registerBroadcastReceiver() {
        if (receiver == null) {
            receiver = BisqNotificationReceiver(this)
        }
        val filter = IntentFilter()
        filter.addAction("bisqNotification")
        registerReceiver(receiver, filter)
    }

    protected fun playTone() {
        try {
            val notificationTone =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            RingtoneManager.getRingtone(applicationContext, notificationTone).play()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to play notification tone")
            e.printStackTrace()
        }
    }

    protected fun loadWebPage(uri: String) {
        DialogBuilder.choicePrompt(
            this, getString(R.string.warning), getString(R.string.load_web_page_text, uri),
            getString(R.string.yes), getString(R.string.no),
            { _, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        this, getString(R.string.cannot_launch_browser), Toast.LENGTH_LONG
                    ).show()
                }
            }
        ).show()
    }

}
