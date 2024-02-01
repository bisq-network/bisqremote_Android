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

package bisq.android.tests.util

import bisq.android.util.CryptoUtil
import bisq.android.util.CryptoUtil.Companion.generateKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.UUID

class CryptoUtilTest {
    private var key = generateKey()
    private var iv = generateValidIV()
    private var crypto = CryptoUtil(key)

    @Test
    fun testGenerateKeyIsValid() {
        val testKey = generateKey()
        assertThat(testKey).matches("[0-9a-zA-Z/+]{32}")
    }

    @Test
    fun testGenerateKeyIsUnique() {
        val generatedKeys: MutableList<String> = ArrayList()
        @Suppress("UnusedPrivateMember")
        for (i in 1..10000) {
            val testKey = generateKey()
            assertThat(testKey).isNotIn(generatedKeys)
            generatedKeys.add(testKey)
        }
    }

    @Test
    fun testEncryptDecryptRandomString() {
        val charPool: List<Char> = (' '..'~').toList()
        val valueToEncrypt =
            (1..kotlin.random.Random.nextInt(10, 256))
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
                .trim()
        var expectedDecryptedValue = valueToEncrypt
        while (expectedDecryptedValue.length % 16 != 0) {
            expectedDecryptedValue = "$expectedDecryptedValue "
        }

        val encrypted = crypto.encrypt(valueToEncrypt, iv)
        val decrypted = crypto.decrypt(encrypted, iv)

        assertThat(encrypted)
            .describedAs("Encrypted value")
            .isNotEqualTo(valueToEncrypt)
        assertThat(decrypted)
            .describedAs("Decrypted value")
            .isEqualTo(expectedDecryptedValue)
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

        assertThat(decrypted)
            .describedAs("Decrypted value")
            .isEqualTo(expectedDecryptedValue)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInitializationWithInvalidKey() {
        CryptoUtil(generateInvalidKey())
    }

    @Test
    fun testDecryptWithIncorrectKey() {
        val valueToEncrypt = "valueToEncrypt"
        val encryptedString = crypto.encrypt(valueToEncrypt, iv)
        val testCrypto = CryptoUtil(generateKey())
        testCrypto.decrypt(encryptedString, iv)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEncryptEmptyString() {
        crypto.encrypt("", iv)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDecryptEmptyString() {
        crypto.decrypt("", iv)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEncryptWithInvalidIv() {
        crypto.encrypt("valueToEncrypt", generateInvalidIV())
    }

    @Test(expected = IllegalArgumentException::class)
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
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
