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
import android.widget.Button
import android.widget.TextView
import bisq.android.R
import bisq.android.model.Device
import bisq.android.ui.UnpairedBaseActivity

class PairingSendActivity : UnpairedBaseActivity() {

    private lateinit var sendPairingTokenInstructions: TextView
    private lateinit var sendPairingTokenButton: Button

    override fun getRootLayoutId() = R.id.pairing_send_layout
    override fun getStatusBarScrimId() = R.id.pairing_send_status_bar_background

    override fun initView() {
        setContentView(R.layout.activity_pairing_send)

        sendPairingTokenInstructions = bind(R.id.pairing_send_pairing_token_instructions)

        sendPairingTokenButton = bind(R.id.pairing_send_pairing_token_button)
        sendPairingTokenButton.setOnClickListener {
            onSendPairingToken()
        }
    }

    private fun onSendPairingToken() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_pairing_subject))
            putExtra(Intent.EXTRA_TEXT, Device.instance.pairingToken())
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_pairing_token)))
    }
}
