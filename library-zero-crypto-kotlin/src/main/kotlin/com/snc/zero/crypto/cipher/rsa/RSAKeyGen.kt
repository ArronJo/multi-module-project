package com.snc.zero.crypto.cipher.rsa

import com.snc.zero.extension.text.toBytes
import com.snc.zero.extension.text.toHexString
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * RSA
 *
 * -SumJCE Supported
 * : "Provider.id info" -> "SunJCE Provider (implements RSA, DES, Triple DES, AES, Blowfish, ARCFOUR, RC2, PBE, Diffie-Hellman, HMAC, ChaCha20)"
 * : "Cipher.RSA SupportedModes" -> "ECB"
 * : "Cipher.RSA SupportedPaddings" -> "NOPADDING|PKCS1PADDING|OAEPPADDING|OAEPWITHMD5ANDMGF1PADDING|OAEPWITHSHA1ANDMGF1PADDING|OAEPWITHSHA-1ANDMGF1PADDING|OAEPWITHSHA-224ANDMGF1PADDING|OAEPWITHSHA-256ANDMGF1PADDING|OAEPWITHSHA-384ANDMGF1PADDING|OAEPWITHSHA-512ANDMGF1PADDING|OAEPWITHSHA-512/224ANDMGF1PADDING|OAEPWITHSHA-512/256ANDMGF1PADDING"
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object RSAKeyGen {

    // 다른 옵션: const val TRANSFORM_RSA_ECB_PKCS1 = "RSA/ECB/PKCS1Padding"
    const val TRANSFORM_RSA_ECB_OAEP = "RSA/ECB/OAEPPadding"
    const val TRANSFORM_RSA_ECB_OAEP_SHA256 = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"

    fun genKeyPair(keySize: Int = 2048, random: SecureRandom? = null): KeyPair {
        var ran = random
        if (null == ran) {
            ran = SecureRandom(randomString().toByteArray())
        }
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(keySize, ran)
        return kpg.generateKeyPair()
    }

    fun toHexString(key: Key): String {
        return key.encoded.toHexString()
    }

    fun toPublicKey(hexStr: String): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = X509EncodedKeySpec(hexStr.toBytes())
        return keyFactory.generatePublic(keySpec)
    }

    fun toPrivateKey(hexStr: String): PrivateKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(hexStr.toBytes())
        return keyFactory.generatePrivate(keySpec)
    }

    private fun randomString(): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..16)
            .map { charset.random() }
            .joinToString("")
    }
}
