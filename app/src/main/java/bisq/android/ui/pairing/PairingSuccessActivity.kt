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
import android.widget.Button
import bisq.android.R
import bisq.android.ui.PairedBaseActivity
import bisq.android.ui.notification.NotificationTableActivity

class PairingSuccessActivity : PairedBaseActivity() {

    private lateinit var pairingCompleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_pairing_success)

        pairingCompleteButton = bind(R.id.pairing_scan_pairing_complete_button)
        pairingCompleteButton.setOnClickListener {
            onPairingComplete()
        }
    }

    private fun onPairingComplete() {
        startActivity(Intent(Intent(this, NotificationTableActivity::class.java)))
    }
}
