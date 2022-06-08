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
import bisq.android.R
import bisq.android.screens.dialogs.ChoicePromptDialog
import bisq.android.screens.dialogs.PromptDialog
import bisq.android.screens.elements.ButtonElement

class WelcomeScreen : Screen() {

    val pairButton = ButtonElement(R.id.pairButton)
    val learnMoreButton = ButtonElement(R.id.learnMoreButton)
    val alertDialogGooglePlayServicesUnavailable = PromptDialog(
        applicationContext.resources.getString(R.string.google_play_services_unavailable)
    )
    val alertDialogCannotRetrieveDeviceToken = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.cannot_retrieve_fcm_token)
    )
    val alertDialogLoadBisqMobileUrl = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.load_web_page_text, BISQ_MOBILE_URL)
    )
}
