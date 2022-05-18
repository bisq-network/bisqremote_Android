package bisq.android.screens.dialogs

import android.R
import bisq.android.screens.elements.ButtonElement

class ChoicePromptDialog(private val message: String) : Dialog(message) {

    val positiveButton = ButtonElement(R.id.button1)
    val negativeButton = ButtonElement(R.id.button2)

}
