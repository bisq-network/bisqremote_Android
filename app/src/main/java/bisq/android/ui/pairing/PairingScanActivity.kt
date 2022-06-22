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

package bisq.android.ui.pairing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import bisq.android.R
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.ui.UnpairedBaseActivity
import bisq.android.util.QrUtil

class PairingScanActivity : UnpairedBaseActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var qrText: TextView
    private lateinit var noWebcamButton: Button
    private lateinit var simulatePairingButton: Button

    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "PairingScanActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        Log.i(TAG, "Pairing token: ${Device.instance.pairingToken()}")
    }

    private fun initView() {
        setContentView(R.layout.activity_pairing_scan)

        qrImage = this.bind(R.id.qrImageView)

        qrText = this.bind(R.id.qrTextView)

        noWebcamButton = bind(R.id.noWebcamButton)
        noWebcamButton.setOnClickListener {
            onNoWebcamButtonClick()
        }

        simulatePairingButton = bind(R.id.simulatePairingButton)
        if (Device.instance.isEmulator()) {
            simulatePairingButton.visibility = View.VISIBLE
        }
        simulatePairingButton.setOnClickListener {
            onSimulatePairingButtonClick()
        }

        mainHandler.post {
            if (Device.instance.pairingToken() == null) {
                return@post
            }
            @Suppress("TooGenericExceptionCaught")
            try {
                val bmp = QrUtil.createQrImage(Device.instance.pairingToken()!!)
                qrImage.setImageBitmap(bmp)
                qrText.visibility = View.INVISIBLE
            } catch (ignored: Exception) {
                Toast.makeText(
                    this, getString(R.string.cannot_generate_qr_code),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun onNoWebcamButtonClick() {
        startActivity(Intent(Intent(this, PairingSendActivity::class.java)))
    }

    private fun onSimulatePairingButtonClick() {
        Device.instance.status = DeviceStatus.PAIRED
        Device.instance.saveToPreferences(this)
        pairingConfirmed()
    }
}
