package com.joachimneumann.bisq.mocks

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot

class Firebase {

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
