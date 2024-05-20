package com.snc.zero.crypto.cipher.aes

import com.snc.zero.crypto.hash.Hash
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES {

    fun encrypt(data: ByteArray, key: String, iv: String, transform: String = "AES/GCM/NoPadding"): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), "AES")
        val cipher = Cipher.getInstance(transform)
        val algoParameterSpec = parameterSpec(iv, transform)

        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            algoParameterSpec
        )
        return cipher.doFinal(data)
    }

    fun decrypt(enc: ByteArray, key: String, iv: String, transform: String = "AES/GCM/NoPadding"): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), "AES")
        val cipher = Cipher.getInstance(transform)
        val algoParameterSpec = parameterSpec(iv, transform)

        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            algoParameterSpec
        )
        return cipher.doFinal(enc)
    }

    private fun parameterSpec(iv: String, transform: String): AlgorithmParameterSpec {
        var algoParameterSpec: AlgorithmParameterSpec = IvParameterSpec(genIv(iv))
        if (transform.split("/")[1] == "GCM") {
            algoParameterSpec = GCMParameterSpec(128, iv.toByteArray())
        }
        return algoParameterSpec
    }

    private fun genKey(key: String): ByteArray {
        return Hash.with(Hash.Algo.SHA256).digest(key)
    }

    private fun genIv(iv: String): ByteArray {
        return Hash.with(Hash.Algo.SHAKE128).digest(iv)
    }
}