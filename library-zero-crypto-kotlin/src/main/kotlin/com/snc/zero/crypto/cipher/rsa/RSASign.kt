package com.snc.zero.crypto.cipher.rsa

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature

/**
 * RSA Sign/Verify
 */
object RSASign {
    private val secureRandom = SecureRandom()

    fun generateRsaKeyPair(bits: Int = 2048): KeyPair {
        val gen = KeyPairGenerator.getInstance("RSA")
        gen.initialize(bits, secureRandom)
        return gen.generateKeyPair()
    }

    fun rsaSign(privateKey: PrivateKey, data: ByteArray): ByteArray {
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initSign(privateKey, secureRandom)
        sig.update(data)
        return sig.sign()
    }

    fun rsaVerify(publicKey: PublicKey, data: ByteArray, signature: ByteArray): Boolean {
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initVerify(publicKey)
        sig.update(data)
        return sig.verify(signature)
    }
}
