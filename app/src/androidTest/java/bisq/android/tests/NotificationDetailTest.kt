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
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.notification.NotificationDetailActivity
import bisq.android.ui.notification.NotificationTableActivity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.matchesPattern
import org.junit.Assert.assertEquals
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
            notificationTableScreen.settingsButton.click()
            settingsScreen.addExampleNotificationsButton.click()
            notificationTableScreen.notificationRecylerView.clickAtPosition(2)
            Intents.intended(IntentMatchers.hasComponent(NotificationDetailActivity::class.java.name))
            assertEquals("(example) Dispute message", notificationDetailScreen.title.getText())
            assertEquals(
                "You received a dispute message for trade with ID 34059340",
                notificationDetailScreen.message.getText()
            )
            assertEquals("Please contact the arbitrator", notificationDetailScreen.action.getText())
            assertThat(
                notificationDetailScreen.eventTime.getText(),
                matchesPattern("Event occurred: 20\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d")
            )
            assertThat(
                notificationDetailScreen.receivedTime.getText(),
                matchesPattern("Event received: 20\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d")
            )
        }
    }
}
