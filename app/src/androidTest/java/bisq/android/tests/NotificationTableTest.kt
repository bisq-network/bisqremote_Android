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

package bisq.android.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.notification.NotificationAdapter
import bisq.android.ui.notification.NotificationDetailActivity
import bisq.android.ui.notification.NotificationTableActivity
import bisq.android.ui.settings.SettingsActivity
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationTableTest : BaseTest() {
    @Before
    override fun setup() {
        super.setup()
        pairDevice()
    }

    @Test
    fun clickNotificationLoadsNotificationDetailActivity() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()
            notificationTableScreen.notificationRecylerView.clickAtPosition(0)
            Intents.intended(IntentMatchers.hasComponent(NotificationDetailActivity::class.java.name))
        }
    }

    @Test
    fun viewedNotificationIsMarkedAsRead() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()
            var readState = getContentAtPosition(0).read
            assertEquals(false, readState)
            notificationTableScreen.notificationRecylerView.clickAtPosition(0)
            Intents.intended(IntentMatchers.hasComponent(NotificationDetailActivity::class.java.name))
            pressBack()
            readState = getContentAtPosition(0).read
            assertEquals(true, readState)
        }
    }

    @Test
    fun swipeToDeleteNotificationDeletesNotification() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()
            val countBeforeSwipe = notificationTableScreen.notificationRecylerView.getItemCount()
            notificationTableScreen.notificationRecylerView.swipeRightToLeftAtPosition(0)
            val countAfterSwipe = notificationTableScreen.notificationRecylerView.getItemCount()
            assertEquals(countBeforeSwipe - 1, countAfterSwipe)
        }
    }

    @Test
    fun scrollPositionIsRetainedWhenNavigatingBack() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            for (counter in 1..5) {
                notificationTableScreen.addExampleNotificationsMenuItem.click()
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

    @Test
    fun clickDeleteAllNotificationsMenuItemAndNotAcceptingConfirmationDoesNotDeleteNotifications() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()

            val itemCount = notificationTableScreen.notificationRecylerView.getItemCount()

            Assert.assertTrue(itemCount > 0)

            notificationTableScreen.deleteAllMenuItem.click()
            Assert.assertTrue(notificationTableScreen.alertDialogDeleteAll.isDisplayed())

            notificationTableScreen.alertDialogDeleteAll.negativeButton.click()

            assertEquals(itemCount, notificationTableScreen.notificationRecylerView.getItemCount())
        }
    }

    @Test
    fun clickDeleteAllNotificationsMenuItemAndAcceptingConfirmationDeletesAllNotifications() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()

            Assert.assertTrue(notificationTableScreen.notificationRecylerView.getItemCount() > 0)

            notificationTableScreen.deleteAllMenuItem.click()
            Assert.assertTrue(notificationTableScreen.alertDialogDeleteAll.isDisplayed())

            notificationTableScreen.alertDialogDeleteAll.positiveButton.click()

            Assert.assertTrue(notificationTableScreen.notificationRecylerView.getItemCount() == 0)
        }
    }

    @Test
    fun clickMarkAsReadMenuItemMarksAllNotificationsAsRead() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()

            val count = notificationTableScreen.notificationRecylerView.getItemCount()
            for (position in 0 until count - 1) {
                val readState = getContentAtPosition(position).read
                assertEquals(false, readState)
            }

            notificationTableScreen.markAllAsReadMenuItem.click()

            for (position in 0 until count - 1) {
                val readState = getContentAtPosition(position).read
                assertEquals(true, readState)
            }
        }
    }

    @Test
    fun clickSettingsMenuItemLoadsSettingsActivity() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.settingsMenuItem.click()
            Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
        }
    }

    private fun getContentAtPosition(position: Int): NotificationAdapter.NotificationViewHolder {
        val viewHolder =
            notificationTableScreen.notificationRecylerView.getContentAtPosition(position)
        return viewHolder as NotificationAdapter.NotificationViewHolder
    }
}
