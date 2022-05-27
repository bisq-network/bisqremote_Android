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

package bisq.android.util

import android.util.Base64
import bisq.android.ext.hexStringToByteArray
import java.nio.charset.Charset
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Cryptographic util for encrypting/decrypting messages.
 *
 * @param key A 32-character key that is exchanged with the device receiving the messages.
 */
class CryptoUtil(private val key: String) {

    private var ivSpec: IvParameterSpec? = null
    private val keySpec: SecretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
    private var cipher: Cipher? = null

    init {
        if (key.length != KEY_LENGTH) {
            throw IllegalArgumentException("Key is not $KEY_LENGTH characters")
        }
        cipher = Cipher.getInstance("AES/CBC/NOPadding")
    }

    companion object {
        const val KEY_LENGTH = 32
        const val IV_LENGTH = 16

        fun generateKey(): String {
            val uuid1 = UUID.randomUUID().toString().replace("-", "")
            val uuid2 = UUID.randomUUID().toString().replace("-", "")
            val uuid = uuid1 + uuid2
            val bytearray = (uuid).hexStringToByteArray()
            val keyLong = Base64.encodeToString(bytearray, Base64.NO_WRAP)
            return keyLong.substring(0, KEY_LENGTH)
        }

        class CryptoException(message: String) : Exception(message)
    }

    @Throws(IllegalArgumentException::class)
    fun encrypt(valueToEncrypt: String, iv: String): String {
        if (iv.length != IV_LENGTH) {
            throw IllegalArgumentException("Initialization vector is not $IV_LENGTH characters")
        }
        var paddedValueToEncrypt = valueToEncrypt
        while (paddedValueToEncrypt.length % IV_LENGTH != 0) {
            paddedValueToEncrypt = "$paddedValueToEncrypt "
        }
        ivSpec = IvParameterSpec(iv.toByteArray())
        val encryptedBytes = encryptInternal(paddedValueToEncrypt, ivSpec!!)
        val encryptedBase64 = Base64.encode(encryptedBytes, Base64.DEFAULT)
        return String(encryptedBase64, Charset.forName("UTF-8"))
    }

    @Throws(IllegalArgumentException::class)
    fun decrypt(valueToDecrypt: String, iv: String): String {
        if (iv.length != IV_LENGTH) {
            throw IllegalArgumentException("Initialization vector is not $IV_LENGTH characters")
        }
        ivSpec = IvParameterSpec(iv.toByteArray())
        val decryptedBytes = decryptInternal(valueToDecrypt, ivSpec!!)
        return String(decryptedBytes!!)
    }

    @Throws(IllegalArgumentException::class)
    private fun encryptInternal(text: String?, ivSpec: IvParameterSpec): ByteArray? {
        if (text == null || text.isEmpty()) {
            throw IllegalArgumentException("Empty string")
        }
        val encrypted: ByteArray?
        try {
            cipher!!.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            encrypted = cipher!!.doFinal(text.toByteArray())
        } catch (e: Exception) {
            throw CryptoException("[encrypt] " + e.message)
        }
        return encrypted
    }

    @Throws(IllegalArgumentException::class)
    private fun decryptInternal(codeBase64: String?, ivSpec: IvParameterSpec): ByteArray? {
        if (codeBase64 == null || codeBase64.isEmpty()) {
            throw IllegalArgumentException("Empty string")
        }
        val decrypted: ByteArray?
        try {
            cipher!!.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val code = Base64.decode(codeBase64, Base64.DEFAULT)
            decrypted = cipher!!.doFinal(code)
        } catch (e: Exception) {
            throw CryptoException("[decrypt] " + e.message)
        }
        return decrypted
    }
}
