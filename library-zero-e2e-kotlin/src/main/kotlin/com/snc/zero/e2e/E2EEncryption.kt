package com.snc.zero.e2e

import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

/**
 * E2E 암호화
 *
 * 이 구현은 다음과 같은 특징을 가지고 있습니다:
 *
 *   1. RSA와 AES를 결합한 하이브리드 암호화 시스템을 사용합니다:
 *     - RSA (2048비트): 키 교환용
 *     - AES (256비트): 실제 메시지 암호화용
 *   2. 주요 기능:
 *     - 키 쌍 생성 (RSA)
 *     - AES 키 생성
 *     - 메시지 암호화/복호화
 *     - 키 변환 (문자열 ↔ 키 객체)
 *   3. 보안 특징:
 *     - CBC 모드 사용으로 더 강력한 보안
 *     - IV(Initial Vector) 사용
 *     - 적절한 패딩 적용
 *
 * 사용시 주의사항:
 *   실제 프로덕션 환경에서는 키 저장소 사용을 고려해야 합니다
 *   네트워크 전송 시 암호화된 데이터와 IV를 안전하게 전송해야 합니다
 *   에러 처리를 추가하는 것이 좋습니다.
 */
class E2EEncryption {
    // RSA 키 쌍 생성 (RSA (2048비트): 키 교환용)
    fun generateKeyPair(): Pair<PublicKey, PrivateKey> {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        return Pair(keyPair.public, keyPair.private)
    }

    // AES 키 생성 (AES (256비트): 실제 메시지 암호화용)
    fun generateAESKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    // RSA로 AES 키 암호화
    fun encryptAESKey(aesKey: SecretKey, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(aesKey.encoded)
    }

    // RSA로 AES 키 복호화
    fun decryptAESKey(encryptedAESKey: ByteArray, privateKey: PrivateKey): SecretKey {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedKey = cipher.doFinal(encryptedAESKey)
        return SecretKeySpec(decryptedKey, "AES")
    }

    // AES로 메시지 암호화
    fun encryptMessage(message: String, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val gcmIvLength = 12
        val gcmTagLength = 128

        val iv = ByteArray(gcmIvLength).apply {
            SecureRandom().nextBytes(this)
        }

        val gcmSpec = GCMParameterSpec(gcmTagLength, iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val encryptedMessage = cipher.doFinal(message.toByteArray(UTF_8))

        return Pair(encryptedMessage, iv)
    }

    // AES로 메시지 복호화
    fun decryptMessage(
        encryptedMessage: ByteArray,
        secretKey: SecretKey,
        iv: ByteArray
    ): String {
        val gcmTagLength = 128
        val gcmSpec = GCMParameterSpec(gcmTagLength, iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        val decryptedMessage = cipher.doFinal(encryptedMessage)
        return String(decryptedMessage, UTF_8)
    }

    // 키를 Base64 문자열로 변환
    fun keyToString(key: Key): String {
        return Base64.getEncoder().encodeToString(key.encoded)
    }

    // Base64 문자열에서 공개키 복원
    fun stringToPublicKey(keyStr: String): PublicKey {
        val keyBytes = Base64.getDecoder().decode(keyStr)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    // Base64 문자열에서 개인키 복원
    fun stringToPrivateKey(keyStr: String): PrivateKey {
        val keyBytes = Base64.getDecoder().decode(keyStr)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
}
