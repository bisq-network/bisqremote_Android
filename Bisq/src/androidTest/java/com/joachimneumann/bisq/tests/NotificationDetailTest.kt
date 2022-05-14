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
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationDetailTest : BaseTest() {

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
