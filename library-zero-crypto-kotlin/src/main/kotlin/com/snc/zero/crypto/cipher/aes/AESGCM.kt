package com.snc.zero.crypto.cipher.aes

import com.snc.zero.crypto.share.randomBytes
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 참고사항: class SecurityUtils 도 참고하면 좋아요
 *
 * AES (Advanced Encryption Standard) 암호화
 *
 * (참고) SecretKey 생성 방법
 * fun generateAesSecretKey(): SecretKey {
 *   val keyGen = KeyGenerator.getInstance("AES")
 *   keyGen.init(256) // 256비트 키 생성
 *   return keyGen.generateKey()
 * }
 */
object AESGCM {

    private val header = "ENCRYPTED::V01::".toByteArray()

    fun generateAesKey(bits: Int = 256): ByteArray {
        require(bits in setOf(128, 192, 256))
        return randomBytes(bits / 8)
    }

    data class AesGcmParams(
        val keyBits: Int = 256,
        val ivBytes: Int = 12, // GCM 모드의 표준 IV 길이는 12바이트입니다
        val tagBits: Int = 128 // GCM 태그 길이 지정 (128비트)
    )

    /**
     * AES-GCM 암호화
     * 반환: Header + IV(앞 12바이트) + CIPHERTEXT+TAG
     */
    fun encrypt(
        plaintext: ByteArray,
        key: ByteArray,
        aad: ByteArray? = null,
        params: AesGcmParams = AesGcmParams()
    ): ByteArray {
        require(key.size * 8 == params.keyBits) { "AES key must be ${params.keyBits} bits" }
        val iv = randomBytes(params.ivBytes)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(params.tagBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), spec)
        if (aad != null) cipher.updateAAD(aad)
        val enc = cipher.doFinal(plaintext)
        return header + iv + enc
    }

    /**
     * AES-GCM 복호화
     * 입력: Header + IV(12바이트) + CIPHERTEXT+TAG
     */
    fun decrypt(
        blob: ByteArray,
        key: ByteArray,
        aad: ByteArray? = null,
        params: AesGcmParams = AesGcmParams()
    ): ByteArray {
        require(key.size * 8 == params.keyBits) { "AES key must be ${params.keyBits} bits" }
        require(blob.size > header.size + params.ivBytes) { "Invalid ciphertext" }
        val data = blob.copyOfRange(header.size, blob.size)
        val iv = data.copyOfRange(0, params.ivBytes)
        val ct = data.copyOfRange(params.ivBytes, data.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(params.tagBits, iv)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), spec)
        if (aad != null) cipher.updateAAD(aad)
        return cipher.doFinal(ct)
    }
}
