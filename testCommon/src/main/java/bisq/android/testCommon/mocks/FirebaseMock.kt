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

package bisq.android.testCommon.mocks

import android.os.Build
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import org.junit.Assume.assumeTrue

object FirebaseMock {

    fun mockFirebaseTokenSuccessful() {
        val firebaseMessagingMock = mockFirebaseMessaging()
        val mockGetTokenTask = mockk<Task<String>>()
        val mockDeleteTokenTask = mockk<Task<Void>>()

        every { mockGetTokenTask.isSuccessful } returns true
        every { mockGetTokenTask.exception } returns null
        every { mockGetTokenTask.result } returns "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBg" +
            "KYWQfP4I1g21rukjFpyKsjGCvFqnQl8owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9" +
            "HS-MjZlfFJXc-LU_ycjwdHYE7XPFUQDD7UlnVB-giAI"

        every { mockDeleteTokenTask.isSuccessful } returns true
        every { mockDeleteTokenTask.exception } returns null

        mockFirebaseToken(firebaseMessagingMock, mockGetTokenTask, mockDeleteTokenTask)
    }

    fun mockFirebaseTokenUnsuccessful() {
        val firebaseMessagingMock = mockFirebaseMessaging()
        val mockGetTokenTask = mockk<Task<String>>()
        val mockDeleteTokenTask = mockk<Task<Void>>()

        every { mockGetTokenTask.isSuccessful } returns false
        every { mockGetTokenTask.exception } returns null
        every { mockGetTokenTask.result } returns null

        every { mockDeleteTokenTask.isSuccessful } returns true
        every { mockDeleteTokenTask.exception } returns null

        mockFirebaseToken(firebaseMessagingMock, mockGetTokenTask, mockDeleteTokenTask)
    }

    fun mockFirebaseNotInitialized() {
        checkStaticMockingSupport()

        mockkStatic(FirebaseMessaging::class)

        every { FirebaseMessaging.getInstance() } throws IllegalStateException("")
    }

    fun mockGooglePlayServicesAvailable() {
        checkStaticMockingSupport()

        mockkStatic(GoogleApiAvailabilityLight::class)

        val googleApiAvailabilityMock = mockk<GoogleApiAvailabilityLight>()
        every { GoogleApiAvailabilityLight.getInstance() } returns googleApiAvailabilityMock
        every { googleApiAvailabilityMock.isGooglePlayServicesAvailable(any()) } returns
            ConnectionResult.SUCCESS
    }

    fun mockGooglePlayServicesNotAvailable() {
        checkStaticMockingSupport()

        mockkStatic(GoogleApiAvailabilityLight::class)

        val googleApiAvailabilityMock = mockk<GoogleApiAvailabilityLight>()
        every { GoogleApiAvailabilityLight.getInstance() } returns googleApiAvailabilityMock
        every { googleApiAvailabilityMock.isGooglePlayServicesAvailable(any()) } returns
            ConnectionResult.SERVICE_MISSING
    }

    fun unmockFirebaseMessaging() {
        unmockkStatic(FirebaseMessaging::class, GoogleApiAvailabilityLight::class)
    }

    private fun mockFirebaseMessaging(): FirebaseMessaging {
        checkStaticMockingSupport()

        mockkStatic(FirebaseMessaging::class)

        val firebaseMessagingMock = mockk<FirebaseMessaging>()
        every { FirebaseMessaging.getInstance() } returns firebaseMessagingMock
        return firebaseMessagingMock
    }

    private fun mockFirebaseToken(
        firebaseMessagingMock: FirebaseMessaging,
        mockGetTokenTask: Task<String>,
        mockDeleteTokenTask: Task<Void>
    ) {
        checkStaticMockingSupport()

        mockkStatic(FirebaseMessaging::class)

        val slotGetTokenListener = slot<OnCompleteListener<String>>()
        val slotDeleteTokenListener = slot<OnCompleteListener<Void>>()

        every { FirebaseMessaging.getInstance() } returns firebaseMessagingMock
        every { firebaseMessagingMock.token } returns mockGetTokenTask
        every { firebaseMessagingMock.deleteToken() } returns mockDeleteTokenTask

        every { mockGetTokenTask.addOnCompleteListener(capture(slotGetTokenListener)) } answers {
            slotGetTokenListener.captured.onComplete(mockGetTokenTask)
            mockGetTokenTask
        }
        every { mockDeleteTokenTask.addOnCompleteListener(capture(slotDeleteTokenListener)) } answers {
            slotDeleteTokenListener.captured.onComplete(mockDeleteTokenTask)
            mockDeleteTokenTask
        }
    }

    private fun checkStaticMockingSupport() {
        assumeTrue(
            "API level 28 or newer is required for static mocking",
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        )
    }
}
