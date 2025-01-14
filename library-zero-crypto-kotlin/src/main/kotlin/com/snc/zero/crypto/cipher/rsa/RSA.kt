package com.snc.zero.crypto.cipher.rsa

import java.security.*
import java.util.Base64
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec

class RSA {
    private var publicKey: PublicKey
    private var privateKey: PrivateKey

    companion object {
        private const val ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        private const val KEY_SIZE = 2048

        /**
         * Base64로 인코딩된 공개키를 가져옵니다.
         */
        fun importPublicKey(encodedKey: String): PublicKey {
            val publicBytes = Base64.getDecoder().decode(encodedKey)
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePublic(X509EncodedKeySpec(publicBytes))
        }

        /**
         * Base64로 인코딩된 개인키를 가져옵니다.
         */
        fun importPrivateKey(encodedKey: String): PrivateKey {
            val privateBytes = Base64.getDecoder().decode(encodedKey)
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateBytes))
        }
    }

    /**
     * 새로운 키 쌍을 생성하여 RSAUtil 인스턴스를 생성합니다.
     */
    constructor() {
        val keyPair = generateKeyPair()
        this.publicKey = keyPair.public
        this.privateKey = keyPair.private
    }

    /**
     * 기존 키 쌍으로 RSAUtil 인스턴스를 생성합니다.
     */
    constructor(publicKey: PublicKey, privateKey: PrivateKey) {
        this.publicKey = publicKey
        this.privateKey = privateKey
    }

    /**
     * 새로운 RSA 키 쌍을 생성합니다.
     */
    private fun generateKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(KEY_SIZE)
        return generator.generateKeyPair()
    }

    /**
     * 문자열을 RSA로 암호화하고 Base64로 인코딩하여 반환합니다.
     */
    fun encrypt(plainText: String): String {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encrypted = cipher.doFinal(plainText.toByteArray())
            Base64.getEncoder().encodeToString(encrypted)
        } catch (e: Exception) {
            println("암호화 중 오류가 발생했습니다. $e")
            throw RSAException("암호화 중 오류가 발생했습니다", e)
        }
    }

    /**
     * Base64로 인코딩된 암호화 문자열을 복호화합니다.
     */
    fun decrypt(encryptedText: String): String {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
            String(decrypted)
        } catch (e: Exception) {
            println("복호화 중 오류가 발생했습니다. $e")
            throw RSAException("복호화 중 오류가 발생했습니다", e)
        }
    }

    /**
     * 공개키를 Base64 문자열로 내보냅니다.
     */
    fun exportPublicKey(): String = Base64.getEncoder().encodeToString(publicKey.encoded)

    /**
     * 개인키를 Base64 문자열로 내보냅니다.
     */
    fun exportPrivateKey(): String = Base64.getEncoder().encodeToString(privateKey.encoded)
}

/**
 * RSA 관련 예외를 처리하기 위한 커스텀 예외 클래스
 */
class RSAException(message: String, cause: Throwable? = null) : Exception(message, cause)
