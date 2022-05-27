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

import bisq.android.mocks.FirebaseMock
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.services.BisqFirebaseMessagingService
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BisqFirebaseMessagingServiceTest {

    @After
    fun cleanup() {
        FirebaseMock.unmockFirebaseMessaging()
    }

    @Test
    fun testFetchFcmTokenUnsuccessfulWithoutOnComplete() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken()
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
    }

    @Test
    fun testFetchFcmTokenUnsuccessfulWithOnComplete() {
        var invokeCount = 0

        FirebaseMock.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken {
            invokeCount++
        }
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)

        assertEquals(1, invokeCount)
    }

    @Test
    fun testFetchFcmTokenSuccessfulWithoutOnComplete() {
        FirebaseMock.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken()
        assertEquals(
            "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqnQl8" +
                "owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHYE7XPFU" +
                "QDD7UlnVB-giAI",
            Device.instance.token
        )
        assertNotNull(Device.instance.key)
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
    }

    @Test
    fun testFetchFcmTokenSuccessfulWithOnComplete() {
        var invokeCount = 0

        FirebaseMock.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        BisqFirebaseMessagingService.fetchFcmToken {
            invokeCount++
        }
        assertEquals(
            "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqnQl8" +
                "owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHYE7XPFU" +
                "QDD7UlnVB-giAI",
            Device.instance.token
        )
        assertNotNull(Device.instance.key)
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)

        assertEquals(1, invokeCount)
    }
}
