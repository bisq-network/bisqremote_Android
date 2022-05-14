package com.joachimneumann.bisq.ui.welcome

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.joachimneumann.bisq.BISQ_MOBILE_URL
import com.joachimneumann.bisq.R
import com.joachimneumann.bisq.model.Device
import com.joachimneumann.bisq.services.BisqFirebaseMessagingService
import com.joachimneumann.bisq.ui.DialogBuilder
import com.joachimneumann.bisq.ui.UnpairedBaseActivity
import com.joachimneumann.bisq.ui.notification.NotificationTableActivity
import com.joachimneumann.bisq.ui.pairing.PairingScanActivity

class WelcomeActivity : UnpairedBaseActivity() {

    private lateinit var learnMoreButton: Button
    private lateinit var pairButton: Button
    private lateinit var progressBar: ProgressBar

    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "WelcomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        checkForOpenedNotificationMessageAndRetrieveData()

        if (Device.instance.readFromPreferences(this)) {
            startActivity(Intent(this, NotificationTableActivity::class.java))
            return
        }

        if (Device.instance.confirmed) {
            Toast.makeText(
                this, getString(R.string.pairing_erased_new_token),
                Toast.LENGTH_LONG
            ).show()
            Device.instance.reset()
        }

        fetchFcmToken()
    }

    private fun initView() {
        setContentView(R.layout.activity_welcome)

        pairButton = bind(R.id.pairButton)
        pairButton.setOnClickListener {
            onPairButtonClick()
        }

        learnMoreButton = bind(R.id.learnMoreButton)
        learnMoreButton.setOnClickListener {
            onLearnMoreButtonClick()
        }

        progressBar = bind(R.id.circularProgressbar)
        progressBar.progressDrawable =
            ContextCompat.getDrawable(this, R.drawable.circular_progressbar)
        Thread {
            var progress = progressBar.progress
            while (true) {
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progress += 1
                if (progress > 100) {
                    progress = 0
                }
                mainHandler.post {
                    progressBar.progress = progress
                }
            }
        }.start()
    }

    private fun checkForOpenedNotificationMessageAndRetrieveData() {
        val bundle = intent.extras
        if (bundle != null) {
            val notificationMessage = bundle.get("encrypted")
            if (notificationMessage != null) {
                registerNotificationReceiver()

                val broadcastIntent = Intent()
                broadcastIntent.action = this.getString(R.string.bisq_broadcast)
                broadcastIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                broadcastIntent.putExtra("notificationMessage", notificationMessage as String)
                sendBroadcast(broadcastIntent)
            }
        }
    }

    private fun onPairButtonClick() {
        maybeProceedToPairingScanActivity()
    }

    private fun onLearnMoreButtonClick() {
        loadWebPage(BISQ_MOBILE_URL)
    }

    private fun fetchFcmToken(onFetchFcmTokenComplete: () -> Unit = {}) {
        Log.i(TAG, "Fetching FCM token")
        disablePairButton()
        BisqFirebaseMessagingService.fetchFcmToken {
            enablePairButton()
            onFetchFcmTokenComplete()
        }
    }

    private fun promptIfMissingFcmToken(onTryAgainFetchFcmTokenComplete: () -> Unit = {}) {
        if (Device.instance.token != null) {
            return
        }
        DialogBuilder.choicePrompt(
            this, getString(R.string.error),
            getString(R.string.cannot_retrieve_fcm_token),
            getString(R.string.try_again),
            getString(R.string.cancel),
            { _, _ ->
                fetchFcmToken {
                    onTryAgainFetchFcmTokenComplete()
                }
            }
        ).show()
    }

    private fun maybeProceedToPairingScanActivity() {
        if (Device.instance.token != null) {
            startActivity(
                Intent(
                    this,
                    PairingScanActivity::class.java
                )
            )
        }
        promptIfMissingFcmToken { onPairButtonClick() }
    }

    private fun enablePairButton() {
        pairButton.isEnabled = true
        pairButton.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
        pairButton.text = applicationContext.resources.getString(R.string.button_pair)
        progressBar.visibility = View.INVISIBLE
    }

    private fun disablePairButton() {
        pairButton.isEnabled = false
        pairButton.setBackgroundColor(Color.LTGRAY)
        pairButton.text = ""
        progressBar.visibility = View.VISIBLE
    }

}
