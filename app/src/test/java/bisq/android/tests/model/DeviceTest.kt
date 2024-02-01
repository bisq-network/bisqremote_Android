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

package bisq.android.tests.model

import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.UUID

class DeviceTest {
    private var token = generateToken()

    @Test
    fun testResetDevice() {
        Device.instance.newToken(token)
        Device.instance.reset()
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .matches("[0-9a-zA-Z/+]{32}")
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(token)
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    fun testSetNewToken() {
        Device.instance.newToken(token)
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(token)
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .matches("[0-9a-zA-Z/+]{32}")
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    fun testGetPairingToken() {
        Device.instance.newToken(token)
        assertThat(Device.instance.pairingToken())
            .isEqualTo(
                Device.DEVICE_MAGIC_ANDROID +
                    Device.DEVICE_SEPARATOR + Device.instance.descriptor +
                    Device.DEVICE_SEPARATOR + Device.instance.key +
                    Device.DEVICE_SEPARATOR + Device.instance.token
            )
    }

    @Test
    fun testGetPairingTokenAfterReset() {
        Device.instance.reset()
        assertThat(Device.instance.pairingToken())
            .isEqualTo(
                Device.DEVICE_MAGIC_ANDROID +
                    Device.DEVICE_SEPARATOR + Device.instance.descriptor +
                    Device.DEVICE_SEPARATOR + Device.instance.key +
                    Device.DEVICE_SEPARATOR + Device.instance.token,
            )
    }

    @Test
    fun testGetDeviceName() {
        assertThat(Device.instance.getDeviceName())
            .isEqualTo("My Manufacturer My Model")
    }

    @Test
    fun testFromValidString() {
        Device.instance.newToken(token)
        var result = false
        Device.instance.pairingToken()?.let { result = Device.instance.fromString(it) }
        assertThat(result).isTrue()
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(token)
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .matches("[0-9a-zA-Z/+]{32}")
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.PAIRED)
    }

    @Test
    fun testFromInvalidString() {
        Device.instance.newToken(token)
        assertThat(Device.instance.fromString("android|Manufacturer Model|invalidKey|invalidToken"))
            .describedAs("Device fromString")
            .isFalse()
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .matches("[0-9a-zA-Z/+]{32}")
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(token)
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    @Test
    fun testFromEmptyString() {
        Device.instance.newToken(token)
        assertThat(Device.instance.fromString(""))
            .describedAs("Device fromString")
            .isFalse()
        assertThat(Device.instance.key)
            .describedAs("Device key")
            .matches("[0-9a-zA-Z/+]{32}")
        assertThat(Device.instance.token)
            .describedAs("Device token")
            .isEqualTo(token)
        assertThat(Device.instance.status)
            .describedAs("Device status")
            .isEqualTo(DeviceStatus.UNPAIRED)
    }

    private fun generateToken(): String {
        val uuid1 = UUID.randomUUID().toString()
        val uuid2 = UUID.randomUUID().toString()
        var uuid = uuid1 + uuid2
        uuid = uuid.replace("-", "")
        return uuid.substring(0, 32)
    }
}
