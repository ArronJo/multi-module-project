package com.snc.zero.crypto.ecdh

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.KeyAgreement

/**
 * ECC (Elliptic Curve Cryptography): 타원 곡선 암호
 * - ECDH (Elliptic Curve Diffie-Hellman) 키 교환 (Key Exchange)
 *
 * 섫명
 *  ECC(타원곡선암호)는 타원곡선 수학을 기반으로 하는 암호 기술의 거대한 상위 개념(틀)이며,
 *  ECDH(타원곡선 디피-헬만)는 이 ECC 기술을 사용하여 공유 비밀키를 교환하는 알고리즘(응용 기술)입니다.
 *
 * 용도:
 *   ✅ 두 당사자가 안전하게 공유 비밀키(세션키) 생성
 *   ✅ TLS 핸드셰이크 기반
 *   ✅ E2E 암호화 (Signal, WhatsApp 등)
 *
 * 흐름:
 *   Alice 공개키 → Bob 에게 전달
 *   Bob 공개키   → Alice 에게 전달
 *   각자 상대방 공개키 + 자신의 개인키 → 동일한 공유비밀 도출
 */
class EcdhKeyExchange {

    companion object {

        fun handleKeyExchange(clientPublicKeyBase64: String): String {
            val exchange = EcdhKeyExchange()
            val serverKeyPair = exchange.generateKeyPair()

            val sharedSecret = exchange.deriveSharedSecretFromBase64(
                serverKeyPair.privateKey,
                clientPublicKeyBase64
            )
            // aesKey → 세션 저장 후 이후 요청 복호화에 활용
            val aesKey = exchange.deriveAesKey(sharedSecret)
            println("AES-256 세션키: ${aesKey.toHexString()}")

            return Base64.getEncoder().encodeToString(serverKeyPair.publicKeyBytes) // 브라우저로 반환
        }
    }

    data class KeyPairHolder(
        val publicKey: PublicKey,
        val privateKey: PrivateKey,
        val publicKeyBytes: ByteArray // 전송용 (X.509 인코딩)
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is KeyPairHolder) return false
            return publicKey == other.publicKey &&
                privateKey == other.privateKey &&
                publicKeyBytes.contentEquals(other.publicKeyBytes)
        }

        override fun hashCode(): Int {
            var result = publicKey.hashCode()
            result = 31 * result + privateKey.hashCode()
            result = 31 * result + publicKeyBytes.contentHashCode()
            return result
        }
    }

    fun generateKeyPair(): KeyPairHolder {
        val keyGen = KeyPairGenerator.getInstance("EC")
        keyGen.initialize(ECGenParameterSpec("secp256r1"))
        val keyPair = keyGen.generateKeyPair()
        return KeyPairHolder(
            publicKey = keyPair.public,
            privateKey = keyPair.private,
            publicKeyBytes = keyPair.public.encoded
        )
    }

    /**
     * 상대방 공개키 바이트 → 공유 비밀키 도출
     */
    fun deriveSharedSecret(myPrivateKey: PrivateKey, peerPublicKeyBytes: ByteArray): ByteArray {
        val peerPublicKey = KeyFactory.getInstance("EC")
            .generatePublic(X509EncodedKeySpec(peerPublicKeyBytes))

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(myPrivateKey)
        keyAgreement.doPhase(peerPublicKey, true)

        return keyAgreement.generateSecret()
    }

    /**
     * 브라우저에서 받은 Base64 공개키로 공유 비밀키 도출
     */
    fun deriveSharedSecretFromBase64(myPrivateKey: PrivateKey, peerPublicKeyBase64: String): ByteArray {
        val peerPublicKeyBytes = Base64.getDecoder().decode(peerPublicKeyBase64)
        return deriveSharedSecret(myPrivateKey, peerPublicKeyBytes)
    }

    /**
     * 공유 비밀키 → AES-256 세션키 도출 (SHA-256 해시)
     */
    fun deriveAesKey(sharedSecret: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(sharedSecret)
    }
}
