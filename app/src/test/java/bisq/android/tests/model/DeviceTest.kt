package bisq.android.tests.model

import bisq.android.model.Device
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DeviceTest {

    private var token = generateToken()

    @Test
    fun testResetDevice() {
        Device.instance.reset()
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertFalse(Device.instance.confirmed)
    }

    @Test
    fun testSetNewToken() {
        Device.instance.newToken(token)
        assertEquals(token, Device.instance.token)
        MatcherAssert.assertThat(Device.instance.key, Matchers.matchesPattern("[0-9a-zA-Z/+]{32}"))
        assertFalse(Device.instance.confirmed)
    }

    @Test
    fun testGetPairingToken() {
        Device.instance.newToken(token)
        assertEquals(
            Device.DEVICE_MAGIC_ANDROID
                    + Device.DEVICE_SEPARATOR + Device.instance.descriptor +
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
        MatcherAssert.assertThat(Device.instance.key, Matchers.matchesPattern("[0-9a-zA-Z/+]{32}"))
        assertFalse(Device.instance.confirmed)
    }

    @Test
    fun testFromInvalidString() {
        Device.instance.newToken(token)
        assertFalse(Device.instance.fromString("android|Manufacturer Model|invalidKey|invalidToken"))
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertFalse(Device.instance.confirmed)
    }

    @Test
    fun testFromEmptyString() {
        Device.instance.newToken(token)
        assertFalse(Device.instance.fromString(""))
        assertNull(Device.instance.key)
        assertNull(Device.instance.token)
        assertFalse(Device.instance.confirmed)
    }

    private fun generateToken(): String {
        val uuid1 = UUID.randomUUID().toString()
        val uuid2 = UUID.randomUUID().toString()
        var uuid = uuid1 + uuid2
        uuid = uuid.replace("-", "")
        return uuid.substring(0, 32)
    }

}
