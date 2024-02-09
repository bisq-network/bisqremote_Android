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

import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.notification.NotificationAdapter
import bisq.android.ui.notification.NotificationDetailActivity
import bisq.android.ui.settings.SettingsActivity
import org.assertj.core.api.Assertions.assertThat
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
        notificationTableActivityRule.launch()

        notificationTableScreen.addExampleNotificationsMenuItem.click()
        notificationTableScreen.notificationRecylerView.clickAtPosition(0)
        intended(hasComponent(NotificationDetailActivity::class.java.name))
    }

    @Test
    fun viewedNotificationIsMarkedAsRead() {
        notificationTableActivityRule.launch()

        notificationTableScreen.addExampleNotificationsMenuItem.click()
        var readState = getContentAtPosition(0).read
        assertThat(readState)
            .describedAs("Read state")
            .isFalse()

        notificationTableScreen.notificationRecylerView.clickAtPosition(0)
        intended(hasComponent(NotificationDetailActivity::class.java.name))
        pressBack()
        readState = getContentAtPosition(0).read
        assertThat(readState)
            .describedAs("Read state")
            .isTrue()
    }

    @Test
    fun swipeToDeleteNotificationDeletesNotification() {
        notificationTableActivityRule.launch()

        notificationTableScreen.addExampleNotificationsMenuItem.click()
        val countBeforeSwipe = notificationTableScreen.notificationRecylerView.getItemCount()
        notificationTableScreen.notificationRecylerView.swipeRightToLeftAtPosition(0)
        val countAfterSwipe = notificationTableScreen.notificationRecylerView.getItemCount()
        assertThat(countAfterSwipe)
            .describedAs("Notification count after swipe to delete")
            .isEqualTo(countBeforeSwipe - 1)
    }

    @Test
    fun scrollPositionIsRetainedWhenNavigatingBack() {
        notificationTableActivityRule.launch()

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
        intended(hasComponent(NotificationDetailActivity::class.java.name))
        pressBack()
        assertThat(notificationTableScreen.notificationRecylerView.getScrollPosition())
            .describedAs("Scroll position when navigating back")
            .isEqualTo(positionBeforeClick)
    }

    @Test
    fun clickDeleteAllNotificationsMenuItemAndNotAcceptingConfirmationDoesNotDeleteNotifications() {
        notificationTableActivityRule.launch()

        notificationTableScreen.addExampleNotificationsMenuItem.click()

        val itemCount = notificationTableScreen.notificationRecylerView.getItemCount()

        assertThat(itemCount).isGreaterThan(0)

        notificationTableScreen.deleteAllMenuItem.click()
        assertThat(notificationTableScreen.alertDialogDeleteAll.isDisplayed())
            .describedAs("Delete all alert dialog is displayed")
            .isTrue()

        notificationTableScreen.alertDialogDeleteAll.negativeButton.click()

        assertThat(notificationTableScreen.notificationRecylerView.getItemCount())
            .describedAs("Item count")
            .isEqualTo(itemCount)
    }

    @Test
    fun clickDeleteAllNotificationsMenuItemAndAcceptingConfirmationDeletesAllNotifications() {
        notificationTableActivityRule.launch()

        notificationTableScreen.addExampleNotificationsMenuItem.click()

        assertThat(notificationTableScreen.notificationRecylerView.getItemCount())
            .describedAs("Item count")
            .isGreaterThan(0)

        notificationTableScreen.deleteAllMenuItem.click()
        assertThat(notificationTableScreen.alertDialogDeleteAll.isDisplayed())
            .describedAs("Delete all alert dialog is displayed")
            .isTrue()

        notificationTableScreen.alertDialogDeleteAll.positiveButton.click()

        assertThat(notificationTableScreen.notificationRecylerView.getItemCount())
            .describedAs("Item count")
            .isZero()
    }

    @Test
    fun clickMarkAsReadMenuItemMarksAllNotificationsAsRead() {
        notificationTableActivityRule.launch()

        notificationTableScreen.addExampleNotificationsMenuItem.click()

        val count = notificationTableScreen.notificationRecylerView.getItemCount()
        for (position in 0 until count - 1) {
            val readState = getContentAtPosition(position).read
            assertThat(readState)
                .describedAs("Read state")
                .isFalse()
        }

        notificationTableScreen.markAllAsReadMenuItem.click()

        for (position in 0 until count - 1) {
            val readState = getContentAtPosition(position).read
            assertThat(readState)
                .describedAs("Read state")
                .isTrue()
        }
    }

    @Test
    fun clickSettingsMenuItemLoadsSettingsActivity() {
        notificationTableActivityRule.launch()

        notificationTableScreen.settingsMenuItem.click()
        intended(hasComponent(SettingsActivity::class.java.name))
    }

    private fun getContentAtPosition(position: Int): NotificationAdapter.NotificationViewHolder {
        val viewHolder =
            notificationTableScreen.notificationRecylerView.getContentAtPosition(position)
        return viewHolder as NotificationAdapter.NotificationViewHolder
    }
}
