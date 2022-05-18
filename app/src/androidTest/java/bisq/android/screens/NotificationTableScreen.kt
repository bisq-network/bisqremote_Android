package bisq.android.screens

import bisq.android.R
import bisq.android.screens.elements.ButtonElement
import bisq.android.screens.elements.RecyclerViewElement

class NotificationTableScreen : Screen() {

    val settingsButton = ButtonElement(R.id.action_settings)
    val notificationRecylerView = RecyclerViewElement(R.id.notification_recycler_view)

}
