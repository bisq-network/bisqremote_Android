package com.joachimneumann.bisq.util

import android.util.Base64
import com.joachimneumann.bisq.ext.hexStringToByteArray
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Cryptographic util for encrypting/decrypting messages.
 *
 * @param key A 32-character key that is exchanged with the device receiving the messages.
 */
class CryptoUtil(private val key: String) {

    private var ivspec: IvParameterSpec? = null
    private val keyspec: SecretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
    private var cipher: Cipher? = null

    init {
        try {
            cipher = Cipher.getInstance("AES/CBC/NOPadding")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun encrypt(valueToEncrypt: String, iv: String): String {
        var paddedValueToEncrypt = valueToEncrypt
        while (paddedValueToEncrypt.length % 16 != 0) {
            paddedValueToEncrypt = "$paddedValueToEncrypt "
        }
        if (iv.length != 16) {
            throw Exception("iv not 16 characters")
        }
        ivspec = IvParameterSpec(iv.toByteArray())
        val encryptedBytes = encryptInternal(paddedValueToEncrypt, ivspec!!)
        val encryptedBase64 = Base64.encode(encryptedBytes, Base64.DEFAULT)
        return String(encryptedBase64, Charset.forName("UTF-8"))
    }

    @Throws(Exception::class)
    fun decrypt(valueToDecrypt: String, iv: String): String {
        if (iv.length != 16) {
            throw Exception("iv not 16 characters")
        }
        ivspec = IvParameterSpec(iv.toByteArray())
        val decryptedBytes = decryptInternal(valueToDecrypt, ivspec!!)
        return String(decryptedBytes!!)
    }

    @Throws(Exception::class)
    private fun encryptInternal(text: String?, ivspec: IvParameterSpec): ByteArray? {
        if (text == null || text.isEmpty()) {
            throw Exception("Empty string")
        }
        if (key.length != 32) {
            throw Exception("key not 32 characters")
        }
        val encrypted: ByteArray?
        try {
            cipher!!.init(Cipher.ENCRYPT_MODE, keyspec, ivspec)
            encrypted = cipher!!.doFinal(text.toByteArray())
        } catch (e: Exception) {
            throw Exception("[encrypt] " + e.message)
        }
        return encrypted
    }

    @Throws(Exception::class)
    private fun decryptInternal(codeBase64: String?, ivspec: IvParameterSpec): ByteArray? {
        if (codeBase64 == null || codeBase64.isEmpty()) {
            throw Exception("Empty string")
        }
        if (key.length != 32) {
            throw Exception("key not 32 characters")
        }
        val decrypted: ByteArray?
        try {
            cipher!!.init(Cipher.DECRYPT_MODE, keyspec, ivspec)
            val code = Base64.decode(codeBase64, Base64.DEFAULT)
            decrypted = cipher!!.doFinal(code)
        } catch (e: Exception) {
            throw Exception("[decrypt] " + e.message)
        }
        return decrypted
    }
}

fun generateKey(): String {
    val uuid1 = UUID.randomUUID().toString().replace("-", "")
    val uuid2 = UUID.randomUUID().toString().replace("-", "")
    val uuid = uuid1 + uuid2
    val bytearray = (uuid).hexStringToByteArray()
    val keyLong = Base64.encodeToString(bytearray, Base64.NO_WRAP)
    return keyLong.substring(0, 32)
}
