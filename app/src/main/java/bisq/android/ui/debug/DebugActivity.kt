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

package bisq.android.ui.debug

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import bisq.android.R
import bisq.android.database.DebugLogLevel
import bisq.android.model.Device
import bisq.android.ui.BaseActivity

class DebugActivity : BaseActivity() {
    private lateinit var viewModel: DebugViewModel
    private lateinit var deviceStatusText: TextView
    private lateinit var showDebugLogsLabel: TextView
    private lateinit var showDebugLogsSwitch: Switch
    private lateinit var logText: TextView
    private lateinit var clearLogButton: Button
    private lateinit var sendLogButton: Button

    private var showDebugLogs: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[DebugViewModel::class.java]

        initView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.allLogs.observe(this) { _ ->
            updateView()
        }
    }

    private fun initView() {
        setContentView(R.layout.activity_debug)

        deviceStatusText = bind(R.id.device_status_value)
        deviceStatusText.text = Device.instance.status.toString()

        showDebugLogsSwitch = bind(R.id.show_debug_log_switch)
        showDebugLogsSwitch.setOnCheckedChangeListener { _, isChecked ->
            showDebugLogs = isChecked
            updateView()
        }

        showDebugLogsLabel = bind(R.id.show_debug_log_label)
        showDebugLogsLabel.setOnClickListener {
            showDebugLogsSwitch.isChecked = !showDebugLogsSwitch.isChecked
        }

        logText = bind(R.id.log_text)

        clearLogButton = bind(R.id.clear_log_button)
        clearLogButton.setOnClickListener {
            onClearLog()
        }

        sendLogButton = bind(R.id.send_log_button)
        sendLogButton.setOnClickListener {
            onSendLog()
        }
    }

    private fun updateView() {
        val allLogs = viewModel.allLogs.value ?: emptyList()

        val displayedLogs = if (!showDebugLogs) {
            allLogs.filter { log -> log.level != DebugLogLevel.DEBUG }
        } else {
            allLogs
        }

        logText.text = displayedLogs.joinToString(separator = "\n----------------------------------------------\n") {
            it.toString()
        }
    }

    private fun onClearLog() {
        viewModel.nukeTable()
    }

    private fun onSendLog() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_log_subject))
            putExtra(Intent.EXTRA_TEXT, logText.text)
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_log)))
    }
}
