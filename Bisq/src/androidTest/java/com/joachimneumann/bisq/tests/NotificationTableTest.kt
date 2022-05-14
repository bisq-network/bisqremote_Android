package com.joachimneumann.bisq.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joachimneumann.bisq.ui.notification.NotificationDetailActivity
import com.joachimneumann.bisq.ui.notification.NotificationTableActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationTableTest : BaseTest() {

    @Test
    fun clickNotificationLoadsNotificationDetailActivity() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.settingsButton.click()
            settingsScreen.addExampleNotificationsButton.click()
            notificationTableScreen.notificationRecylerView.clickAtPosition(0)
            Intents.intended(IntentMatchers.hasComponent(NotificationDetailActivity::class.java.name))
        }
    }

    @Test
    fun viewedNotificationIsMarkedAsRead() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.settingsButton.click()
            settingsScreen.addExampleNotificationsButton.click()
            var readState =
                notificationTableScreen.notificationRecylerView.getReadStateAtPosition(0)
            assertEquals(false, readState)
            notificationTableScreen.notificationRecylerView.clickAtPosition(0)
            Intents.intended(IntentMatchers.hasComponent(NotificationDetailActivity::class.java.name))
            pressBack()
            readState = notificationTableScreen.notificationRecylerView.getReadStateAtPosition(0)
            assertEquals(true, readState)
        }
    }

    @Test
    fun swipeToDeleteNotificationDeletesNotification() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.settingsButton.click()
            settingsScreen.addExampleNotificationsButton.click()
            val countBeforeSwipe = notificationTableScreen.notificationRecylerView.getItemCount()
            notificationTableScreen.notificationRecylerView.swipeToDeleteAtPosition(0)
            val countAfterSwipe = notificationTableScreen.notificationRecylerView.getItemCount()
            assertEquals(countBeforeSwipe - 1, countAfterSwipe)
        }
    }

    @Test
    fun scrollPositionIsRetainedWhenNavigatingBack() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            for (counter in 1..5) {
                notificationTableScreen.settingsButton.click()
                settingsScreen.addExampleNotificationsButton.click()
            }
            notificationTableScreen.notificationRecylerView.scrollToPosition(
                notificationTableScreen.notificationRecylerView.getItemCount() - 1
            )
            val positionBeforeClick = notificationTableScreen.notificationRecylerView
                .getScrollPosition()
            notificationTableScreen.notificationRecylerView.clickAtPosition(
                notificationTableScreen.notificationRecylerView.getItemCount() - 1
            )
            Intents.intended(IntentMatchers.hasComponent(NotificationDetailActivity::class.java.name))
            pressBack()
            assertEquals(
                positionBeforeClick,
                notificationTableScreen.notificationRecylerView.getScrollPosition()
            )
        }
    }

}
