package com.joachimneumann.bisq.tests.services

import com.joachimneumann.bisq.mocks.Firebase
import com.joachimneumann.bisq.model.Device
import com.joachimneumann.bisq.services.BisqFirebaseMessagingService
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class BisqFirebaseMessagingServiceTest {

    @Test
    fun testFetchFcmTokenUnsuccessfulWithoutOnComplete() {
        Firebase.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken()
        Assert.assertNull(Device.instance.key)
        Assert.assertNull(Device.instance.token)
        Assert.assertFalse(Device.instance.confirmed)
    }

    @Test
    fun testFetchFcmTokenUnsuccessfulWithOnComplete() {
        var invokeCount = 0

        Firebase.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken {
            invokeCount++
        }
        Assert.assertNull(Device.instance.key)
        Assert.assertNull(Device.instance.token)
        Assert.assertFalse(Device.instance.confirmed)

        assertEquals(1, invokeCount)
    }

    @Test
    fun testFetchFcmTokenSuccessfulWithoutOnComplete() {
        Firebase.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken()
        Assert.assertEquals(
            "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_",
            Device.instance.token
        )
        Assert.assertNotNull(Device.instance.key)
        Assert.assertFalse(Device.instance.confirmed)
    }

    @Test
    fun testFetchFcmTokenSuccessfulWithOnComplete() {
        var invokeCount = 0

        Firebase.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken {
            invokeCount++
        }
        Assert.assertEquals(
            "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_",
            Device.instance.token
        )
        Assert.assertNotNull(Device.instance.key)
        Assert.assertFalse(Device.instance.confirmed)

        assertEquals(1, invokeCount)
    }

}
