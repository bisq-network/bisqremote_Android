package bisq.android.screens

import bisq.android.R
import bisq.android.screens.elements.TextElement

class NotificationDetailScreen : Screen() {

    val title = TextElement(R.id.detail_title)
    val message = TextElement(R.id.detail_message)
    val action = TextElement(R.id.detail_action)
    val eventTime = TextElement(R.id.detail_event_time)
    val receivedTime = TextElement(R.id.detail_received_time)

}
