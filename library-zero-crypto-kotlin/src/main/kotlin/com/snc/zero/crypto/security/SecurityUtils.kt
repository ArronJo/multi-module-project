package com.snc.zero.crypto.security

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.cipher.rsa.RSASign
import com.snc.zero.crypto.hash.pbkdf2.PBKDF2
import com.snc.zero.crypto.token.SignedToken
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

object SecurityUtils {

    // ===== AES-GCM =====

    /**
     * AES-GCM 암호화
     * 반환: IV(앞 12바이트) + CIPHERTEXT+TAG
     */
    fun aesGcmEncrypt(
        plaintext: ByteArray,
        key: ByteArray,
        aad: ByteArray? = null,
        params: AESGCM.Params = AESGCM.Params()
    ): ByteArray {
        return AESGCM.encrypt(plaintext, key, aad, params)
    }

    /**
     * AES-GCM 복호화
     * 입력: IV(12바이트) + CIPHERTEXT+TAG
     */
    fun aesGcmDecrypt(
        blob: ByteArray,
        key: ByteArray,
        aad: ByteArray? = null,
        params: AESGCM.Params = AESGCM.Params()
    ): ByteArray {
        return AESGCM.decrypt(blob, key, aad, params)
    }

    // ===== Password Hash (PBKDF2-SHA256) =====

    /**
     * 저장 포맷: PBKDF2$<iterations>$<salt_b64url>$<dk_b64url>
     */
    fun pbkdf2Hash(password: CharArray, params: PBKDF2.Pbkdf2Params = PBKDF2.Pbkdf2Params()): String {
        return PBKDF2.pbkdf2Hash(password, params)
    }

    fun pbkdf2Verify(password: CharArray, stored: String): Boolean {
        return PBKDF2.pbkdf2Verify(password, stored)
    }

    // ===== RSA Sign/Verify =====

    fun generateRsaKeyPair(bits: Int = 2048): KeyPair {
        return RSASign.generateRsaKeyPair(bits)
    }

    fun rsaSign(privateKey: PrivateKey, data: ByteArray): ByteArray {
        return RSASign.rsaSign(privateKey, data)
    }

    fun rsaVerify(publicKey: PublicKey, data: ByteArray, signature: ByteArray): Boolean {
        return RSASign.rsaVerify(publicKey, data, signature)
    }

    // ===== HMAC-SHA256 & 간단 토큰 샘플 (미니 JWT) =====

    /**
     * 매우 단순한 JWT 형태의 샘플 (학습용)
     * header: {"alg":"HS256","typ":"JWT"}
     */
    fun createSignedToken(payloadJson: String, secretKey: ByteArray): String {
        return SignedToken.createSignedToken(payloadJson, secretKey)
    }

    fun verifySignedToken(token: String, secretKey: ByteArray): Boolean {
        return SignedToken.verifySignedToken(token, secretKey)
    }
}
