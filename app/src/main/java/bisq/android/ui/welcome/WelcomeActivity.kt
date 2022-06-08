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

package bisq.android.ui.welcome

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
import bisq.android.BISQ_MOBILE_URL
import bisq.android.R
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.services.BisqFirebaseMessagingService
import bisq.android.services.BisqFirebaseMessagingService.Companion.isGooglePlayServicesAvailable
import bisq.android.ui.DialogBuilder
import bisq.android.ui.UnpairedBaseActivity
import bisq.android.ui.notification.NotificationTableActivity
import bisq.android.ui.pairing.PairingScanActivity

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

        if (!isGooglePlayServicesAvailable(this)) {
            pairButton.setOnClickListener {
                promptGooglePlayServicesUnavailable()
            }
            return
        }

        if (Device.instance.readFromPreferences(this)) {
            startActivity(Intent(this, NotificationTableActivity::class.java))
            return
        }

        if (Device.instance.status == DeviceStatus.NEEDS_REPAIR) {
            Toast.makeText(
                this, getString(R.string.pairing_erased_new_token),
                Toast.LENGTH_LONG
            ).show()
            Device.instance.reset()
        } else if (Device.instance.status == DeviceStatus.ERASED) {
            Toast.makeText(
                this, getString(R.string.pairing_erased),
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

    private fun promptGooglePlayServicesUnavailable() {
        DialogBuilder.prompt(
            this,
            getString(R.string.error),
            getString(R.string.google_play_services_unavailable),
            getString(R.string.ok)
        ).show()
    }

    private fun promptIfMissingFcmToken(onTryAgainFetchFcmTokenComplete: () -> Unit = {}) {
        if (Device.instance.token != null) {
            return
        }
        DialogBuilder.choicePrompt(
            this,
            getString(R.string.error),
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
