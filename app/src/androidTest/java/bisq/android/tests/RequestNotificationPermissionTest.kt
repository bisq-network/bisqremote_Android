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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.notification.NotificationTableActivity
import bisq.android.ui.pairing.RequestNotificationPermissionActivity
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestNotificationPermissionTest : BaseTest() {
    @Before
    override fun setup() {
        super.setup()
        pairDevice()
    }

    @Test
    fun clickEnableNotificationsButtonLoadsNotificationPermissionRequestPrompt() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        ActivityScenario.launch(RequestNotificationPermissionActivity::class.java).use {
            requestNotificationPermissionScreen.enableNotificationsButton.click()
            val expectedIntent = Matchers.allOf(
                IntentMatchers.hasAction("android.content.pm.action.REQUEST_PERMISSIONS"),
                IntentMatchers.hasExtra(
                    "android.content.pm.extra.REQUEST_PERMISSIONS_NAMES",
                    Matchers.hasItemInArray(Manifest.permission.POST_NOTIFICATIONS)
                )
            )
            intended(expectedIntent)
            assertThat(requestNotificationPermissionScreen.permissionPrompt.isDisplayed())
                .describedAs("Request notification permission prompt is displayed")
                .isTrue()
        }
    }

    @Test
    fun acceptingNotificationPermissionRequestLoadsNotificationTableScreen() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        ActivityScenario.launch(RequestNotificationPermissionActivity::class.java).use {
            requestNotificationPermissionScreen.enableNotificationsButton.click()
            assertThat(requestNotificationPermissionScreen.permissionPrompt.isDisplayed())
                .describedAs("Request notification permission prompt is displayed")
                .isTrue()
            requestNotificationPermissionScreen.permissionPrompt.grantPermission()
            intended(IntentMatchers.hasComponent(NotificationTableActivity::class.java.name))
            assertThat(
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
                .describedAs("Post notifications permission")
                .isEqualTo(PackageManager.PERMISSION_GRANTED)
        }
    }

    @Test
    fun clickSkipButtonLoadsNotificationTableScreen() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        ActivityScenario.launch(RequestNotificationPermissionActivity::class.java).use {
            requestNotificationPermissionScreen.skipPermissionButton.click()
            intended(IntentMatchers.hasComponent(NotificationTableActivity::class.java.name))

            assertThat(requestNotificationPermissionScreen.permissionPrompt.isDisplayed())
                .describedAs("Request notification permission prompt is not displayed")
                .isFalse()

            assertThat(
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
                .describedAs("Post notifications permission")
                .isEqualTo(PackageManager.PERMISSION_DENIED)
        }
    }
}
