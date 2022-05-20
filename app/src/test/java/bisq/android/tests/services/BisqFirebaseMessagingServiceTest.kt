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

package bisq.android.tests.services

import bisq.android.mocks.Firebase
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.services.BisqFirebaseMessagingService
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
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
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
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)

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
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
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
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)

        assertEquals(1, invokeCount)
    }

}
