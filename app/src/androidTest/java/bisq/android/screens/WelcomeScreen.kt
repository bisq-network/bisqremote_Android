package bisq.android.screens

import bisq.android.BISQ_MOBILE_URL
import bisq.android.R
import bisq.android.screens.dialogs.ChoicePromptDialog
import bisq.android.screens.elements.ButtonElement

class WelcomeScreen : Screen() {

    val pairButton = ButtonElement(R.id.pairButton)
    val learnMoreButton = ButtonElement(R.id.learnMoreButton)
    val alertDialogCannotRetrieveDeviceToken = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.cannot_retrieve_fcm_token)
    )
    val alertDialogLoadBisqMobileUrl = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.load_web_page_text, BISQ_MOBILE_URL)
    )

}
