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

package bisq.android.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import bisq.android.Application
import bisq.android.BISQ_MOBILE_URL
import bisq.android.BISQ_NETWORK_URL
import bisq.android.R
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.services.BisqFirebaseMessagingService
import bisq.android.ui.DialogBuilder
import bisq.android.ui.ThemeProvider
import bisq.android.ui.UiUtil.loadWebPage
import bisq.android.ui.notification.NotificationViewModel
import bisq.android.ui.pairing.PairingScanActivity
import bisq.android.ui.welcome.WelcomeActivity

@Suppress("TooManyFunctions")
class SettingsFragment : PreferenceFragmentCompat() {
    private val themeProvider by lazy { ThemeProvider(requireContext()) }
    private val themePreference by lazy {
        findPreference<ListPreference>(getString(R.string.theme_preferences_key))
    }
    private val resetPairingPreference by lazy {
        findPreference<Preference>(getString(R.string.reset_pairing_preferences_key))
    }
    private val scanPairingTokenPreference by lazy {
        findPreference<Preference>(getString(R.string.scan_pairing_token_preferences_key))
    }
    private val aboutBisqPreference by lazy {
        findPreference<Preference>(getString(R.string.about_bisq_preferences_key))
    }
    private val aboutAppPreference by lazy {
        findPreference<Preference>(getString(R.string.about_app_preferences_key))
    }
    private val versionPreference by lazy {
        findPreference<Preference>(getString(R.string.version_preferences_key))
    }

    private lateinit var viewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        setThemePreference()
        setResetPairingPreference()
        setScanPairingTokenPreference()
        setAboutBisqPreference()
        setAboutAppPreference()
        setVersionPreference()
    }

    private fun setThemePreference() {
        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val theme = themeProvider.getTheme(newValue)
                    AppCompatDelegate.setDefaultNightMode(theme)
                }
                true
            }
        themePreference?.summaryProvider = getThemeSummaryProvider()
    }

    private fun getThemeSummaryProvider() =
        Preference.SummaryProvider<ListPreference> { preference ->
            themeProvider.getThemeDescriptionForPreference(preference.value)
        }

    private fun setResetPairingPreference() {
        resetPairingPreference?.setOnPreferenceClickListener {
            this.context?.let { context ->
                onResetPairing(context)
            }
            true
        }
    }

    private fun onResetPairing(context: Context) {
        DialogBuilder.choicePrompt(
            context,
            getString(R.string.confirm),
            getString(R.string.register_again_confirmation),
            getString(R.string.yes),
            getString(R.string.no),
            { _, _ ->
                Device.instance.reset()
                Device.instance.clearPreferences(context)
                viewModel.nukeTable()
                Device.instance.status = DeviceStatus.ERASED
                BisqFirebaseMessagingService.refreshFcmToken()
                val intent = Intent(context, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        ).show()
    }

    private fun setScanPairingTokenPreference() {
        scanPairingTokenPreference?.setOnPreferenceClickListener {
            startActivity(Intent(Intent(context, PairingScanActivity::class.java)))
            true
        }
    }

    private fun setAboutBisqPreference() {
        aboutBisqPreference?.setOnPreferenceClickListener {
            this.context?.let { context -> loadWebPage(context, BISQ_NETWORK_URL) }
            true
        }
    }

    private fun setAboutAppPreference() {
        aboutAppPreference?.setOnPreferenceClickListener {
            this.context?.let { context -> loadWebPage(context, BISQ_MOBILE_URL) }
            true
        }
    }

    private fun setVersionPreference() {
        versionPreference?.title = getString(R.string.version, Application.getAppVersion())
    }
}
