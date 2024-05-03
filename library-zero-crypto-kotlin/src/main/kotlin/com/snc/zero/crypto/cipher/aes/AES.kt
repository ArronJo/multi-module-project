package com.snc.zero.crypto.cipher.aes

import com.snc.zero.crypto.hash.Hash
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES {

    private const val ALGORITHM = "AES"
    private const val MODE = "CFB"
    private const val PADDING = "PKCS5Padding"

    fun encrypt(key: String, iv: String, data: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), ALGORITHM)
        val cipher = Cipher.getInstance("$ALGORITHM/$MODE/$PADDING")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            IvParameterSpec(genIv(iv))
        )
        return cipher.doFinal(data)
    }

    fun decrypt(key: String, iv: String, enc: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), ALGORITHM)
        val cipher = Cipher.getInstance("$ALGORITHM/$MODE/$PADDING")
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            IvParameterSpec(genIv(iv))
        )
        return cipher.doFinal(enc)
    }

    private fun genKey(key: String): ByteArray {
        return Hash.with(Hash.Algo.SHA256).digest(key)
    }

    private fun genIv(iv: String): ByteArray {
        return Hash.with(Hash.Algo.SHAKE128).digest(iv)
    }
}