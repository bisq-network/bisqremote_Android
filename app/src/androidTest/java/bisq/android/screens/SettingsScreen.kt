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
import bisq.android.screens.elements.ButtonElement

class SettingsScreen : Screen() {

    val aboutBisqButton = ButtonElement(R.id.settingsAboutBisqButton)
    val aboutAppButton = ButtonElement(R.id.settingsAboutAppButton)
    val alertDialogLoadBisqNetworkUrl = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.load_web_page_text, BISQ_NETWORK_URL)
    )
    val alertDialogLoadBisqMobileUrl = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.load_web_page_text, BISQ_MOBILE_URL)
    )
    val resetButton = ButtonElement(R.id.settingsRegisterAgainButton)
    val deleteNotificationsButton = ButtonElement(R.id.settingsDeleteAllNotificationsButton)
    val markAsReadButton = ButtonElement(R.id.settingsMarkAsReadButton)
    val addExampleNotificationsButton = ButtonElement(R.id.settingsAddExampleButton)

}
