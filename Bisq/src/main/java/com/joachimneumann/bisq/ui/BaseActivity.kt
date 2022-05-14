package com.joachimneumann.bisq.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.joachimneumann.bisq.R
import com.joachimneumann.bisq.services.NotificationReceiver

open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    private var receiver: NotificationReceiver? = null

    fun <T : View> Activity.bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res)
    }

    override fun onStart() {
        super.onStart()
        registerNotificationReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
        receiver = null
    }

    protected fun registerNotificationReceiver() {
        if (receiver == null) {
            receiver = NotificationReceiver(this)
        }
        val filter = IntentFilter()
        filter.addAction(getString(R.string.bisq_broadcast))
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
