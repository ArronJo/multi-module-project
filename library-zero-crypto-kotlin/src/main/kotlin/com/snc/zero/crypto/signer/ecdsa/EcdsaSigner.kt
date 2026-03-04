package com.snc.zero.crypto.signer.ecdsa

import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec

/**
 * ECDSA - P-256 (secp256r1) 전자서명
 *
 * 용도:
 *   ✅ JWT ES256 서명 (OAuth2, OIDC)
 *   ✅ TLS 인증서 서명
 *   ✅ 코드 서명 (APK, JAR)
 *   ✅ 블록체인 트랜잭션 서명 (Bitcoin, Ethereum)
 *
 * 주의:
 *   ⚠️ 서명 시 내부적으로 랜덤값(k) 사용 → 같은 메시지라도 서명값 매번 다름
 *   ⚠️ k 값이 노출/재사용되면 개인키 역산 가능 (PS3 해킹 사례)
 *   → SecureRandom 이 반드시 보장되어야 함
 */
class EcdsaSigner {

    fun generateKeyPair(): java.security.KeyPair {
        val keyGen = KeyPairGenerator.getInstance("EC")
        keyGen.initialize(ECGenParameterSpec("secp256r1"))
        return keyGen.generateKeyPair()
    }

    fun sign(privateKey: PrivateKey, message: ByteArray): ByteArray {
        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initSign(privateKey)
        sig.update(message)
        return sig.sign()
    }

    fun verify(publicKey: PublicKey, message: ByteArray, signature: ByteArray): Boolean {
        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initVerify(publicKey)
        sig.update(message)
        return sig.verify(signature)
    }
}
