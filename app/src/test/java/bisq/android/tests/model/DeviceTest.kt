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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class DeviceTest {

    private var token = generateToken()

    @Test
    fun testResetDevice() {
        Device.instance.reset()
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
    }

    @Test
    fun testSetNewToken() {
        Device.instance.newToken(token)
        assertEquals(token, Device.instance.token)
        assertThat(Device.instance.key, Matchers.matchesPattern("[0-9a-zA-Z/+]{32}"))
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
    }

    @Test
    fun testGetPairingToken() {
        Device.instance.newToken(token)
        assertEquals(
            Device.DEVICE_MAGIC_ANDROID +
                Device.DEVICE_SEPARATOR + Device.instance.descriptor +
                Device.DEVICE_SEPARATOR + Device.instance.key +
                Device.DEVICE_SEPARATOR + Device.instance.token,
            Device.instance.pairingToken()
        )
    }

    @Test
    fun testGetEmptyPairingToken() {
        Device.instance.reset()
        assertNull(Device.instance.pairingToken())
    }

    @Test
    fun testGetDeviceName() {
        assertEquals("My Manufacturer My Model", Device.instance.getDeviceName())
    }

    @Test
    fun testFromValidString() {
        Device.instance.newToken(token)
        var result = false
        Device.instance.pairingToken()?.let { result = Device.instance.fromString(it) }
        assertTrue(result)
        assertEquals(token, Device.instance.token)
        assertThat(Device.instance.key, Matchers.matchesPattern("[0-9a-zA-Z/+]{32}"))
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
    }

    @Test
    fun testFromInvalidString() {
        Device.instance.newToken(token)
        assertFalse(Device.instance.fromString("android|Manufacturer Model|invalidKey|invalidToken"))
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
    }

    @Test
    fun testFromEmptyString() {
        Device.instance.newToken(token)
        assertFalse(Device.instance.fromString(""))
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertEquals(DeviceStatus.UNPAIRED, Device.instance.status)
    }

    private fun generateToken(): String {
        val uuid1 = UUID.randomUUID().toString()
        val uuid2 = UUID.randomUUID().toString()
        var uuid = uuid1 + uuid2
        uuid = uuid.replace("-", "")
        return uuid.substring(0, 32)
    }
}
