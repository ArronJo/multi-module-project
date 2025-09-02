package com.snc.zero.crypto.cipher.rsa

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PSSParameterSpec

/**
 * RSA Sign/Verify with PSS padding
 */
object RSASign {
    private val secureRandom = SecureRandom()

    private val pssParameterSpec = PSSParameterSpec(
        "SHA-256",
        "MGF1",
        MGF1ParameterSpec.SHA256,
        32,
        1
    )

    fun generateRsaKeyPair(bits: Int = 2048): KeyPair {
        val gen = KeyPairGenerator.getInstance("RSA")
        gen.initialize(bits, secureRandom)
        return gen.generateKeyPair()
    }

    /**
     * PSS(Probabilistic Signature Scheme) 패딩을 사용하지 않는 한, SHA256withRSA는 같은 입력에 대해 항상 같은 출력을 만듭니다.
     */
    fun rsaSign(privateKey: PrivateKey, data: ByteArray, pssPadding: Boolean = false): ByteArray {
        return if (pssPadding) {
            val sig = Signature.getInstance("RSASSA-PSS")
            sig.setParameter(pssParameterSpec)
            sig.initSign(privateKey, secureRandom)
            sig.update(data)
            sig.sign()
        } else {
            val sig = Signature.getInstance("SHA256withRSA")
            sig.initSign(privateKey, secureRandom)
            sig.update(data)
            sig.sign()
        }
    }

    fun rsaVerify(publicKey: PublicKey, data: ByteArray, signature: ByteArray, pssPadding: Boolean = false): Boolean {
        return if (pssPadding) {
            val sig = Signature.getInstance("RSASSA-PSS")
            sig.setParameter(pssParameterSpec)
            sig.initVerify(publicKey)
            sig.update(data)
            sig.verify(signature)
        } else {
            val sig = Signature.getInstance("SHA256withRSA")
            sig.initVerify(publicKey)
            sig.update(data)
            sig.verify(signature)
        }
    }
}
