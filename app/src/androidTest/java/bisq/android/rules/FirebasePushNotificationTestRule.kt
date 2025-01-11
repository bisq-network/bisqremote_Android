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

package bisq.android.rules

import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * This rule interacts with the lifecycle of FirebaseMessagingService,
 * effectively simulating the receipt of push notifications.
 */
class FirebasePushNotificationTestRule(private val pushService: FirebaseMessagingService) : TestWatcher() {

    companion object {
        private const val FIREBASE_PUSH_TOKEN = "mocked_token_value"
    }

    override fun starting(description: Description) {
        super.starting(description)
        pushService.attachBaseContext()
        pushService.onCreate()
        pushService.onNewToken(FIREBASE_PUSH_TOKEN)
    }

    override fun finished(description: Description) {
        pushService.onDestroy()
        super.finished(description)
    }

    fun onNewToken(token: String) = pushService.onNewToken(token)

    fun sendPush(remoteMessage: RemoteMessage) = pushService.onMessageReceived(remoteMessage)
}

internal fun Service.attachBaseContext() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    val attachBaseContextMethod = ContextWrapper::class.java.getDeclaredMethod("attachBaseContext", Context::class.java)
    attachBaseContextMethod.isAccessible = true

    attachBaseContextMethod.invoke(this, context)
}
