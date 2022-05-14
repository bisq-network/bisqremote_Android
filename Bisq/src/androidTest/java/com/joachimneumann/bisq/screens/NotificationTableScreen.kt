package com.joachimneumann.bisq.screens

import com.joachimneumann.bisq.R
import com.joachimneumann.bisq.screens.elements.ButtonElement
import com.joachimneumann.bisq.screens.elements.RecyclerViewElement

class NotificationTableScreen : Screen() {

    val settingsButton = ButtonElement(R.id.action_settings)
    val notificationRecylerView = RecyclerViewElement(R.id.notification_recycler_view)

}
