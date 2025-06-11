package com.snc.zero.crypto.cipher.rsa

import java.security.Key
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

/**
 * RSA
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object RSA {

    fun encrypt(data: ByteArray, key: Key, transform: String): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transform)
        if (RSAKeyGen.TRANSFORM_RSA_ECB_OAEP == transform ||
            RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256 == transform
        ) {
            val oaepParams = OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            )
            cipher.init(Cipher.ENCRYPT_MODE, key, oaepParams)
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        }

        return cipher.doFinal(data)
    }

    fun decrypt(enc: ByteArray, key: Key, transform: String): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transform)
        if (RSAKeyGen.TRANSFORM_RSA_ECB_OAEP == transform ||
            RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256 == transform
        ) {
            val oaepParams = OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            )
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams)
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key)
        }

        return cipher.doFinal(enc)
    }
}
