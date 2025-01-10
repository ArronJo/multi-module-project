package com.snc.zero.crypto.cipher.aes

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureAES {

    private val GCM_IV_LENGTH: Int = 12 // GCM 모드의 표준 IV 길이는 12바이트입니다
    private val GCM_TAG_LENGTH: Int = 128 // GCM 태그 길이 지정 (128비트)

    @Throws(Exception::class)
    fun encrypt(plaintext: ByteArray?, key: SecretKey?): ByteArray {
        // 랜덤 IV(nonce) 생성
        val iv = ByteArray(GCM_IV_LENGTH).apply {
            SecureRandom().nextBytes(this)
        }

        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)

        // GCM 모드로 cipher 초기화
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        // 메시지 암호화
        val ciphertext = cipher.doFinal(plaintext)

        // IV와 암호문 결합
        val encrypted = ByteArray(iv.size + ciphertext.size)
        System.arraycopy(iv, 0, encrypted, 0, iv.size)
        System.arraycopy(ciphertext, 0, encrypted, iv.size, ciphertext.size)

        return encrypted
    }

    @Throws(Exception::class)
    fun decrypt(encrypted: ByteArray, key: SecretKey?): ByteArray {
        // IV 추출
        val iv = ByteArray(GCM_IV_LENGTH)
        System.arraycopy(encrypted, 0, iv, 0, iv.size)

        // 암호문 추출
        val ciphertext = ByteArray(encrypted.size - GCM_IV_LENGTH)
        System.arraycopy(encrypted, GCM_IV_LENGTH, ciphertext, 0, ciphertext.size)

        // GCM 파라미터 생성
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)

        // GCM 모드로 cipher 초기화
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        // 복호화 수행
        return cipher.doFinal(ciphertext)
    }
}
