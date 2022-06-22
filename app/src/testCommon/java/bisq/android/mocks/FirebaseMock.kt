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

package bisq.android.mocks

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic

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
        mockkStatic("com.google.firebase.messaging.FirebaseMessaging")

        every { FirebaseMessaging.getInstance() } throws IllegalStateException()
    }

    fun mockGooglePlayServicesAvailable() {
        mockkStatic("com.google.android.gms.common.GoogleApiAvailability")

        val googleApiAvailabilityMock = mockk<GoogleApiAvailability>()
        every { GoogleApiAvailability.getInstance() } returns googleApiAvailabilityMock
        every { googleApiAvailabilityMock.isGooglePlayServicesAvailable(any()) } returns
            ConnectionResult.SUCCESS
    }

    fun mockGooglePlayServicesNotAvailable() {
        mockkStatic("com.google.android.gms.common.GoogleApiAvailability")

        val googleApiAvailabilityMock = mockk<GoogleApiAvailability>()
        every { GoogleApiAvailability.getInstance() } returns googleApiAvailabilityMock
        every { googleApiAvailabilityMock.isGooglePlayServicesAvailable(any()) } returns
            ConnectionResult.SERVICE_MISSING
    }

    fun unmockFirebaseMessaging() {
        unmockkStatic("com.google.firebase.messaging.FirebaseMessaging")
        unmockkStatic("com.google.android.gms.common.GoogleApiAvailability")
    }

    private fun mockFirebaseMessaging(): FirebaseMessaging {
        mockkStatic("com.google.firebase.messaging.FirebaseMessaging")

        val firebaseMessagingMock = mockk<FirebaseMessaging>()
        every { FirebaseMessaging.getInstance() } returns firebaseMessagingMock
        return firebaseMessagingMock
    }

    private fun mockFirebaseToken(
        firebaseMessagingMock: FirebaseMessaging,
        mockGetTokenTask: Task<String>,
        mockDeleteTokenTask: Task<Void>
    ) {
        mockkStatic("com.google.firebase.messaging.FirebaseMessaging")

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
}
