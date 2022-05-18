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
