package com.snc.zero.crypto.cipher.rsa

import java.security.Key
import java.security.SecureRandom
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec

/**
 * RSA 암호화  -> ECCCipher 클래스로 대체 하는 것이 좋다.
 * : RSA 가 너무 느리 현대적으로 맞기 않기 때문이라고 한다.
 *
 * @author mcharima5@gmail.com
 * @since 2022
 *
 * ---
 * ✅지금 구조의 문제점 (중요)
 * ❗1️⃣ 데이터 크기 제한
 *   RSA-OAEP:
 *   - 최대 ≈ (키길이 - 66byte)
 *    → 2048bit 기준 → 약 190byte
 *    → 큰 데이터 못 씀
 *
 * ❗2️⃣ 성능 안 좋음
 *   RSA는 대칭키보다:
 *   👉 100배 이상 느림
 *   → 트래픽 늘면 병목됨
 *
 * ❗3️⃣ 장기적으로 폐기 대상
 *   TLS 1.3, 금융권, 공공기관:
 *   → 전부 RSA 암호화 방식 폐기 중
 */
object RSACipher {

    fun encrypt(data: ByteArray, key: Key, transform: String = RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transform)
        if (RSAKeyGen.TRANSFORM_RSA_ECB_OAEP == transform ||
            RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256 == transform
        ) {
            val oaepParams = OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            )
            cipher.init(Cipher.ENCRYPT_MODE, key, oaepParams)
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        }

        return cipher.doFinal(data)
    }

    fun decrypt(enc: ByteArray, key: Key, transform: String = RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transform)
        if (RSAKeyGen.TRANSFORM_RSA_ECB_OAEP == transform ||
            RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256 == transform
        ) {
            val oaepParams = OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            )
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams)
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key)
        }

        return cipher.doFinal(enc)
    }

    /**
     * RSA + AES 하이브리드 암호화 결과
     */
    class EncryptedData(
        encryptedAesKey: ByteArray, // RSA로 암호화된 AES 키
        iv: ByteArray, // AES-GCM IV
        cipherText: ByteArray // AES로 암호화된 데이터
    ) {
        val encryptedAesKey = encryptedAesKey.clone()
        val iv = iv.clone()
        val cipherText = cipherText.clone()

        override fun toString(): String {
            return "EncryptedData(publicKey=${encryptedAesKey.toHexString()}, iv=${iv.toHexString()}, cipherText=${cipherText.toHexString()})"
        }

        private fun ByteArray.toHexString(): String {
            return this.joinToString("") { "%02x".format(it) }
        }
    }

    /**
     * RSA + AES 하이브리드 암호화
     * 1. AES 키 생성
     * 2. 데이터를 AES-GCM으로 암호화
     * 3. AES 키를 RSA 공개키로 암호화
     */
    fun encryptWithAES(data: ByteArray, rsaPublicKey: Key): EncryptedData {
        // 1. AES 키 생성 (256bit)
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256, SecureRandom())
        val aesKey = keyGen.generateKey()

        // 2. AES-GCM으로 데이터 암호화
        val iv = SecureRandom().generateSeed(12)
        val aesCipher = Cipher.getInstance("AES/GCM/NoPadding")
        aesCipher.init(
            Cipher.ENCRYPT_MODE,
            aesKey,
            GCMParameterSpec(128, iv)
        )
        val encryptedData = aesCipher.doFinal(data)

        // 3. AES 키를 RSA로 암호화
        val encryptedAesKey = encrypt(aesKey.encoded, rsaPublicKey)

        return EncryptedData(encryptedAesKey, iv, encryptedData)
    }

    /**
     * RSA + AES 하이브리드 복호화
     * 1. RSA 개인키로 AES 키 복호화
     * 2. AES 키로 데이터 복호화
     */
    fun decryptWithAES(encryptedData: EncryptedData, rsaPrivateKey: Key): ByteArray {
        // 1. RSA로 AES 키 복호화
        val aesKeyBytes = decrypt(encryptedData.encryptedAesKey, rsaPrivateKey)
        val aesKey = SecretKeySpec(aesKeyBytes, "AES")

        // 2. AES-GCM으로 데이터 복호화
        val aesCipher = Cipher.getInstance("AES/GCM/NoPadding")
        aesCipher.init(
            Cipher.DECRYPT_MODE,
            aesKey,
            GCMParameterSpec(128, encryptedData.iv)
        )

        return aesCipher.doFinal(encryptedData.cipherText)
    }
}
