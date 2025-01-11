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

package bisq.android.screens

import bisq.android.BISQ_MOBILE_URL
import bisq.android.BISQ_NETWORK_URL
import bisq.android.R
import bisq.android.screens.dialogs.ChoicePromptDialog
import bisq.android.screens.dialogs.ThemePromptDialog
import bisq.android.screens.elements.PreferenceElement

class SettingsScreen : Screen() {
    val themePreference = PreferenceElement(applicationContext.getString(R.string.theme))
    val themePromptDialog = ThemePromptDialog()
    val resetPairingPreference = PreferenceElement(applicationContext.getString(R.string.reset_pairing))
    val alertDialogResetPairing = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.register_again_confirmation)
    )
    val scanPairingTokenPreference = PreferenceElement(applicationContext.getString(R.string.scan_pairing_token))
    val aboutBisqPreference = PreferenceElement(applicationContext.getString(R.string.about_bisq))
    val aboutAppPreference = PreferenceElement(applicationContext.getString(R.string.about_this_app))
    val alertDialogLoadBisqNetworkUrl = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.load_web_page_confirmation, BISQ_NETWORK_URL)
    )
    val alertDialogLoadBisqMobileUrl = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.load_web_page_confirmation, BISQ_MOBILE_URL)
    )
}
