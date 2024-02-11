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

import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.services.BisqFirebaseMessagingService.Companion.fetchFcmToken
import bisq.android.services.BisqFirebaseMessagingService.Companion.isFirebaseMessagingInitialized
import bisq.android.services.BisqFirebaseMessagingService.Companion.isGooglePlayServicesAvailable
import bisq.android.services.BisqFirebaseMessagingService.Companion.refreshFcmToken
import bisq.android.testCommon.mocks.FirebaseMock
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Ignore
import org.junit.Test

class BisqFirebaseMessagingServiceTest {
    // TODO add tests for notification vs data messages

    @After
    fun cleanup() {
        FirebaseMock.unmockFirebaseMessaging()
    }

    @Test
    fun testIsGooglePlayServicesAvailableReturnsFalseWhenNotAvailable() {
        FirebaseMock.mockGooglePlayServicesNotAvailable()
        assertThat(isGooglePlayServicesAvailable(mockk())).isFalse()
    }

    @Test
    fun testIsGooglePlayServicesAvailableReturnsTrueWhenAvailable() {
        FirebaseMock.mockGooglePlayServicesAvailable()
        assertThat(isGooglePlayServicesAvailable(mockk())).isTrue()
    }

    @Test
    fun testIsFirebaseMessagingInitializedReturnsFalseWhenNotInitialized() {
        FirebaseMock.mockFirebaseNotInitialized()
        assertThat(isFirebaseMessagingInitialized()).isFalse()
    }

    @Test
    fun testIsFirebaseMessagingInitializedReturnsTrueWhenInitialized() {
        FirebaseMock.mockFirebaseTokenSuccessful()
        assertThat(isFirebaseMessagingInitialized()).isTrue()
    }

    @Test
    fun testFetchFcmTokenUnsuccessfulWithoutOnComplete() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        fetchFcmToken()
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .matches("[0-9a-zA-Z/+]{32}")
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    fun testFetchFcmTokenUnsuccessfulWithOnComplete() {
        var invokeCount = 0

        FirebaseMock.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        fetchFcmToken {
            invokeCount++
        }
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .matches("[0-9a-zA-Z/+]{32}")
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)

        assertThat(invokeCount)
            .describedAs("Fetch FCM token invoke count")
            .isEqualTo(1)
    }

    @Test
    fun testFetchFcmTokenSuccessfulWithoutOnComplete() {
        FirebaseMock.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        fetchFcmToken()
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(
                "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqnQl8" +
                    "owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHYE7XPFU" +
                    "QDD7UlnVB-giAI"
            )
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNotNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    fun testFetchFcmTokenSuccessfulWithOnComplete() {
        var invokeCount = 0

        FirebaseMock.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        fetchFcmToken {
            invokeCount++
        }
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(
                "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqnQl8" +
                    "owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHYE7XPFU" +
                    "QDD7UlnVB-giAI"
            )
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNotNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)

        assertThat(invokeCount)
            .describedAs("Fetch FCM token invoke count")
            .isEqualTo(1)
    }

    @Test
    @Ignore("refreshFcmToken needs to be fixed")
    fun testRefreshFcmTokenUnsuccessfulWithoutOnComplete() {
        FirebaseMock.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        refreshFcmToken()
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNull()
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    @Ignore("refreshFcmToken needs to be fixed")
    fun testRefreshFcmTokenUnsuccessfulWithOnComplete() {
        var invokeCount = 0

        FirebaseMock.mockFirebaseTokenUnsuccessful()
        Device.instance.reset()
        refreshFcmToken {
            invokeCount++
        }
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNull()
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)

        assertThat(invokeCount)
            .describedAs("Fetch FCM token invoke count")
            .isEqualTo(1)
    }

    @Test
    @Ignore("refreshFcmToken needs to be fixed")
    fun testRefreshFcmTokenSuccessfulWithoutOnComplete() {
        FirebaseMock.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        refreshFcmToken()
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(
                "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqnQl8" +
                    "owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHYE7XPFU" +
                    "QDD7UlnVB-giAI"
            )
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNotNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    @Ignore("refreshFcmToken needs to be fixed")
    fun testRefreshFcmTokenSuccessfulWithOnComplete() {
        var invokeCount = 0

        FirebaseMock.mockFirebaseTokenSuccessful()
        Device.instance.reset()
        refreshFcmToken {
            invokeCount++
        }
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(
                "cutUn7ZaTra9q3ayZG5vCQ:APA91bGrc9pTJdqzBgKYWQfP4I1g21rukjFpyKsjGCvFqnQl8" +
                    "owMqD_7_HB7viqHYXW5XE5O8B82Vyu9kZbAZ7u-S1sP_qVU9HS-MjZlfFJXc-LU_ycjwdHYE7XPFU" +
                    "QDD7UlnVB-giAI"
            )
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .isNotNull()
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)

        assertThat(invokeCount)
            .describedAs("Fetch FCM token invoke count")
            .isEqualTo(1)
    }
}
