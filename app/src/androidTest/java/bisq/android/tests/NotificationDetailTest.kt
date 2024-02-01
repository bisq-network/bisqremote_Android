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
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.notification.NotificationDetailActivity
import bisq.android.ui.notification.NotificationTableActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationDetailTest : BaseTest() {
    @Before
    override fun setup() {
        super.setup()
        pairDevice()
    }

    @Test
    fun notificationDetailsArePopulatedCorrectly() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()
            notificationTableScreen.notificationRecylerView.clickAtPosition(2)
            intended(hasComponent(NotificationDetailActivity::class.java.name))
            assertThat(notificationDetailScreen.title.getText())
                .describedAs("Notification title")
                .isEqualTo("Dispute message")
            assertThat(notificationDetailScreen.message.getText())
                .describedAs("Notification message")
                .isEqualTo("You received a dispute message for trade with ID 34059340")
            assertThat(notificationDetailScreen.action.getText())
                .describedAs("Notification action")
                .isEqualTo("Please contact the arbitrator")
            assertThat(notificationDetailScreen.eventTime.getText())
                .describedAs("Notification event time")
                .matches("Event occurred: 20\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d")
            assertThat(notificationDetailScreen.receivedTime.getText())
                .describedAs("Notification received time")
                .matches("Event received: 20\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d")
        }
    }

    @Test
    fun clickDeleteButtonDeletesNotification() {
        ActivityScenario.launch(NotificationTableActivity::class.java).use {
            notificationTableScreen.addExampleNotificationsMenuItem.click()
            val countBeforeSwipe = notificationTableScreen.notificationRecylerView.getItemCount()

            notificationTableScreen.notificationRecylerView.clickAtPosition(0)
            intended(hasComponent(NotificationDetailActivity::class.java.name))

            notificationDetailScreen.deleteButton.click()
            val countAfterDelete = notificationTableScreen.notificationRecylerView.getItemCount()
            assertThat(countAfterDelete)
                .describedAs("Notification count after delete")
                .isEqualTo(countBeforeSwipe - 1)
        }
    }
}
