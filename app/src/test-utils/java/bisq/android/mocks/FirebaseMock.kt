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

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot

class FirebaseMock {

    companion object {
        fun mockFirebaseTokenSuccessful() {
            mockkStatic("com.google.firebase.messaging.FirebaseMessaging")

            val firebaseMessagingMock = mockk<FirebaseMessaging>()
            every { FirebaseMessaging.getInstance() } returns firebaseMessagingMock

            val mockGetTokenTask = mockk<Task<String>>()
            every { firebaseMessagingMock.token } returns mockGetTokenTask

            val slot = slot<OnCompleteListener<String>>()
            every { mockGetTokenTask.addOnCompleteListener(capture(slot)) } answers {
                slot.captured.onComplete(mockGetTokenTask)
                mockGetTokenTask
            }

            every { mockGetTokenTask.isSuccessful } returns true
            every { mockGetTokenTask.exception } returns null
            every { mockGetTokenTask.result } returns "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_"
        }

        fun mockFirebaseTokenUnsuccessful() {
            mockkStatic("com.google.firebase.messaging.FirebaseMessaging")

            val firebaseMessagingMock = mockk<FirebaseMessaging>()
            every { FirebaseMessaging.getInstance() } returns firebaseMessagingMock

            val mockGetTokenTask = mockk<Task<String>>()
            every { firebaseMessagingMock.token } returns mockGetTokenTask

            val slot = slot<OnCompleteListener<String>>()
            every { mockGetTokenTask.addOnCompleteListener(capture(slot)) } answers {
                slot.captured.onComplete(mockGetTokenTask)
                mockGetTokenTask
            }

            every { mockGetTokenTask.isSuccessful } returns false
            every { mockGetTokenTask.exception } returns null
            every { mockGetTokenTask.result } returns null
        }
    }
}
