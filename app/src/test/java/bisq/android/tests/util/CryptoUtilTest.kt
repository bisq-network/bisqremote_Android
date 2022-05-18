package bisq.android.tests.util

import bisq.android.util.CryptoUtil
import bisq.android.util.generateKey
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.matchesPattern
import org.junit.Assert.*

import org.junit.Test
import java.util.*

class CryptoUtilTest {

    private var key = generateKey()
    private var iv = generateValidIV()
    private var crypto = CryptoUtil(key)

    @Test
    fun testGenerateKeyIsValid() {
        val testKey = generateKey()
        assertThat(testKey, matchesPattern("[0-9a-zA-Z/+]{32}"))
    }

    @Test
    fun testGenerateKeyIsUnique() {
        val generatedKeys: MutableList<String> = ArrayList()
        for (i in 1..10000) {
            val testKey = generateKey()
            assertTrue(testKey !in generatedKeys)
            generatedKeys.add(testKey)
        }
    }

    @Test
    fun testEncryptDecryptRandomString() {
        val charPool: List<Char> = (' '..'~').toList()
        val valueToEncrypt = (1..kotlin.random.Random.nextInt(10, 256))
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
            .trim()
        var expectedDecryptedValue = valueToEncrypt
        while (expectedDecryptedValue.length % 16 != 0) {
            expectedDecryptedValue = "$expectedDecryptedValue "
        }

        val encrypted = crypto.encrypt(valueToEncrypt, iv)
        val decrypted = crypto.decrypt(encrypted, iv)

        assertNotEquals(valueToEncrypt, encrypted)
        assertEquals(expectedDecryptedValue, decrypted)
    }

    @Test
    fun testDecryptingMessageWithTrailingSpaces() {
        val valueToEncrypt = "this is a test "
        var expectedDecryptedValue = valueToEncrypt
        while (expectedDecryptedValue.length % 16 != 0) {
            expectedDecryptedValue = "$expectedDecryptedValue "
        }

        val encrypted = crypto.encrypt(valueToEncrypt, iv)
        val decrypted = crypto.decrypt(encrypted, iv)

        assertEquals(expectedDecryptedValue, decrypted)
    }

    @Test(expected = Exception::class)
    fun testEncryptWithInvalidKey() {
        val testCrypto = CryptoUtil(generateInvalidKey())
        val valueToEncrypt = "valueToEncrypt"
        testCrypto.encrypt(valueToEncrypt, iv)
    }

    @Test(expected = Exception::class)
    fun testEncryptEmptyString() {
        crypto.encrypt("", iv)
    }

    @Test(expected = Exception::class)
    fun testDecryptEmptyString() {
        crypto.decrypt("", iv)
    }

    @Test(expected = Exception::class)
    fun testEncryptWithInvalidIv() {
        crypto.encrypt("valueToEncrypt", generateInvalidIV())
    }

    @Test(expected = Exception::class)
    fun testDecryptWithInvalidIv() {
        crypto.decrypt(crypto.encrypt("valueToEncrypt", iv), generateInvalidIV())
    }

    private fun generateValidIV(): String {
        var uuid = UUID.randomUUID().toString()
        uuid = uuid.replace("-", "")
        return uuid.substring(0, 16)
    }

    private fun generateInvalidIV(): String {
        var uuid = UUID.randomUUID().toString()
        uuid = uuid.replace("-", "")
        return uuid.substring(0, kotlin.random.Random.nextInt(0, 15))
    }

    private fun generateInvalidKey(): String {
        val charPool: List<Char> = ('0'..'9') + ('a'..'z') + ('A'..'Z') + '/' + '+'
        return (1..kotlin.random.Random.nextInt(0, 31))
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

}
