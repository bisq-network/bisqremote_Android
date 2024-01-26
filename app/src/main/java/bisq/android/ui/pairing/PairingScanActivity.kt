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
    private lateinit var qrPlaceholderText: TextView
    private lateinit var noWebcamButton: Button
    private lateinit var simulatePairingButton: Button

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_pairing_scan)

        qrImage = this.bind(R.id.pairing_scan_qr_image)

        qrPlaceholderText = this.bind(R.id.pairing_scan_qr_placeholder)

        noWebcamButton = bind(R.id.pairing_scan_no_webcam_button)
        noWebcamButton.setOnClickListener {
            onNoWebcam()
        }

        simulatePairingButton = bind(R.id.pairing_scan_simulate_pairing_button)
        if (Device.instance.isEmulator() && Device.instance.status != DeviceStatus.PAIRED) {
            simulatePairingButton.visibility = View.VISIBLE
        }
        simulatePairingButton.setOnClickListener {
            onSimulatePairing()
        }

        mainHandler.post {
            if (Device.instance.pairingToken() == null) {
                return@post
            }
            @Suppress("TooGenericExceptionCaught")
            try {
                val bmp = QrUtil.createQrImage(Device.instance.pairingToken()!!)
                qrImage.setImageBitmap(bmp)
                qrPlaceholderText.visibility = View.INVISIBLE
            } catch (ignored: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.cannot_generate_qr_code),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun onNoWebcam() {
        startActivity(Intent(Intent(this, PairingSendActivity::class.java)))
    }

    private fun onSimulatePairing() {
        Device.instance.status = DeviceStatus.PAIRED
        Device.instance.saveToPreferences(this)
        pairingConfirmed()
    }
}
