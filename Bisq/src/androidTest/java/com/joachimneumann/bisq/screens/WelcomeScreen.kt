package com.joachimneumann.bisq.screens

import com.joachimneumann.bisq.BISQ_MOBILE_URL
import com.joachimneumann.bisq.R
import com.joachimneumann.bisq.screens.dialogs.ChoicePromptDialog
import com.joachimneumann.bisq.screens.elements.ButtonElement

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
