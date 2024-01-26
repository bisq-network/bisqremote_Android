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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.notification.NotificationTableActivity
import bisq.android.ui.pairing.PairingSuccessActivity
import bisq.android.ui.pairing.RequestNotificationPermissionActivity
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PairingSuccessTest : BaseTest() {
    @Before
    override fun setup() {
        super.setup()
        pairDevice()
    }

    @After
    fun removeMocks() {
        unmockkStatic("androidx.core.app.ActivityCompat")
        unmockkStatic("androidx.core.content.ContextCompat")
    }

    @Test
    fun clickPairingSuccessButtonLoadsNotificationTableScreenWithApi32AndOlder() {
        assumeMaxApiLevel(Build.VERSION_CODES.S_V2)

        ActivityScenario.launch(PairingSuccessActivity::class.java).use {
            pairingSuccessScreen.pairingCompleteButton.click()
            Intents.intended(IntentMatchers.hasComponent(NotificationTableActivity::class.java.name))

            assertFalse(pairingSuccessScreen.permissionPrompt.isDisplayed())
        }
    }

    @Test
    fun clickPairingSuccessButtonLoadsNotificationPermissionRequestPromptWithApi33AndNewer() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        ActivityScenario.launch(PairingSuccessActivity::class.java).use {
            pairingSuccessScreen.pairingCompleteButton.click()
            val expectedIntent = Matchers.allOf(
                IntentMatchers.hasAction("android.content.pm.action.REQUEST_PERMISSIONS"),
                IntentMatchers.hasExtra(
                    "android.content.pm.extra.REQUEST_PERMISSIONS_NAMES",
                    Matchers.hasItemInArray(Manifest.permission.POST_NOTIFICATIONS)
                ),
            )
            Intents.intended(expectedIntent)
            assertTrue(pairingSuccessScreen.permissionPrompt.isDisplayed())
        }
    }

    @Test
    fun acceptingNotificationPermissionRequestLoadsNotificationTableScreen() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        ActivityScenario.launch(PairingSuccessActivity::class.java).use {
            pairingSuccessScreen.pairingCompleteButton.click()
            assertTrue(pairingSuccessScreen.permissionPrompt.isDisplayed())
            pairingSuccessScreen.permissionPrompt.grantPermission()
            Intents.intended(IntentMatchers.hasComponent(NotificationTableActivity::class.java.name))
            assertEquals(
                PackageManager.PERMISSION_GRANTED,
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }
    }

    @Test
    fun denyingNotificationPermissionRequestLoadsNotificationTableScreen() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        ActivityScenario.launch(PairingSuccessActivity::class.java).use {
            pairingSuccessScreen.pairingCompleteButton.click()
            assertTrue(pairingSuccessScreen.permissionPrompt.isDisplayed())
            pairingSuccessScreen.permissionPrompt.denyPermission()
            Intents.intended(IntentMatchers.hasComponent(NotificationTableActivity::class.java.name))
            assertEquals(
                PackageManager.PERMISSION_DENIED,
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }
    }

    @Test
    fun clickPairingSuccessButtonWithPermissionsAlreadyGrantedLoadsNotificationTableScreen() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        // Attempting to grant the permission does not work,
        // i.e. GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS).
        // Therefore, just mock the permission check. Not ideal, but works for now.
        mockkStatic("androidx.core.content.ContextCompat")
        every {
            ContextCompat.checkSelfPermission(
                any(), Manifest.permission.POST_NOTIFICATIONS
            )
        } returns PackageManager.PERMISSION_GRANTED

        ActivityScenario.launch(PairingSuccessActivity::class.java).use {
            pairingSuccessScreen.pairingCompleteButton.click()
            Intents.intended(IntentMatchers.hasComponent(NotificationTableActivity::class.java.name))

            assertFalse(pairingSuccessScreen.permissionPrompt.isDisplayed())
        }
    }

    @Test
    fun clickPairingSuccessButtonWithPermissionsPreviouslyDeniedLoadsRequestPermissionsActivity() {
        assumeMinApiLevel(Build.VERSION_CODES.TIRAMISU)

        // I have no idea how to ensure shouldShowRequestPermissionRationale returns true.
        // Therefore, just mock it. Not ideal, but works for now.
        mockkStatic("androidx.core.app.ActivityCompat")
        every {
            ActivityCompat.shouldShowRequestPermissionRationale(
                any(), Manifest.permission.POST_NOTIFICATIONS
            )
        } returns true

        ActivityScenario.launch(PairingSuccessActivity::class.java).use {
            pairingSuccessScreen.pairingCompleteButton.click()
            Intents.intended(IntentMatchers.hasComponent(RequestNotificationPermissionActivity::class.java.name))

            assertFalse(pairingSuccessScreen.permissionPrompt.isDisplayed())
        }
    }
}
