package com.snc.zero.crypto.cipher.aes

import com.snc.zero.crypto.hash.Hash
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES {

    fun encrypt(data: ByteArray, key: String, iv: String, transform: String = "AES/CFB/PKCS5Padding"): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), "AES")
        val cipher = Cipher.getInstance(transform)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            IvParameterSpec(genIv(iv))
        )
        return cipher.doFinal(data)
    }

    fun decrypt(enc: ByteArray, key: String, iv: String, transform: String = "AES/CFB/PKCS5Padding"): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), "AES")
        val cipher = Cipher.getInstance(transform)
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