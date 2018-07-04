package com.joachimneumann.bisq

import android.util.Base64
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptoHelper(private val key: String // 32 character key - exchanged with phones that receive the message
) {

    private var ivspec: IvParameterSpec? = null
    private val keyspec: SecretKeySpec
    private var cipher: Cipher? = null

    init {

        keyspec = SecretKeySpec(key.toByteArray(), "AES")

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
        var v = valueToEncrypt
        while (v.length % 16 != 0) {
            v = "$v "
        }

        if (iv.length != 16) {
            throw Exception("iv not 16 characters")
        }
        ivspec = IvParameterSpec(iv.toByteArray())
        val encryptedBytes = encryptInternal(v, ivspec!!)
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
        if (text == null || text.length == 0) {
            throw Exception("Empty string")
        }

        if (key.length != 32) {
            throw Exception("key not 32 characters")
        }

        var encrypted: ByteArray?
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
        if (codeBase64 == null || codeBase64.length == 0) {
            throw Exception("Empty string")
        }

        if (key.length != 32) {
            throw Exception("key not 32 characters")
        }

        var decrypted: ByteArray?
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