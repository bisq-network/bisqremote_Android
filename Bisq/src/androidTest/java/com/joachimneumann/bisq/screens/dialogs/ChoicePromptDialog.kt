package com.joachimneumann.bisq.screens.dialogs

import android.R
import com.joachimneumann.bisq.screens.elements.ButtonElement

class ChoicePromptDialog(private val message: String) : Dialog(message) {

    val positiveButton = ButtonElement(R.id.button1)
    val negativeButton = ButtonElement(R.id.button2)

}
