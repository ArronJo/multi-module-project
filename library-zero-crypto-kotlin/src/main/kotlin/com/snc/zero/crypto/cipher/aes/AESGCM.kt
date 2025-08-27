package com.snc.zero.crypto.cipher.aes

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES (Advanced Encryption Standard) 암호화
 *
 * 참고사항: class SecurityUtils 도 참고하면 좋아요
 */

object AESGCM {

    private val secureRandom = SecureRandom()

    private fun randomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }

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
     * -key: keyBits(256) 이므로 Hash256 함수를 이용하여 256비트로 만들면 된다.
     * -반환: EncryptedMetadata + IV + CIPHERTEXT+TAG
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
        val encryptedData = cipher.doFinal(plaintext)

        // 메타데이터 생성 (평문, IV, 키로 해시 생성)
        val hash = EncryptedMetadata.createHash(plaintext, iv, key)
        val metadata = EncryptedMetadata(hash = hash)

        return metadata.toByteArray() + iv + encryptedData
    }

    /**
     * AES-GCM 복호화
     * 입력: EncryptedMetadata + IV + CIPHERTEXT+TAG
     *
     * 예외 발생 순서:
     * - (IV나 암호문 데이터를 변조) GCM 태그 검증 → AEADBadTagException
     * - 해시 검증 → SecurityException
     */
    fun decrypt(
        blob: ByteArray,
        key: ByteArray,
        aad: ByteArray? = null,
        params: AesGcmParams = AesGcmParams()
    ): ByteArray {
        require(key.size * 8 == params.keyBits) { "AES key must be ${params.keyBits} bits" }
        require(blob.size > EncryptedMetadata.METADATA_SIZE + params.ivBytes) { "Invalid ciphertext" }

        // 메타데이터 추출
        val metadataBytes = blob.copyOfRange(0, EncryptedMetadata.METADATA_SIZE)
        val metadata = EncryptedMetadata.fromByteArray(metadataBytes)

        // IV와 암호문 추출
        val dataStart = EncryptedMetadata.METADATA_SIZE
        val iv = blob.copyOfRange(dataStart, dataStart + params.ivBytes)
        val ciphertext = blob.copyOfRange(dataStart + params.ivBytes, blob.size)

        // 복호화
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(params.tagBits, iv)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), spec)

        if (aad != null) cipher.updateAAD(aad)
        val plaintext = cipher.doFinal(ciphertext)

        // 해시 검증 (위변조 체크)
        if (!metadata.verifyHash(plaintext, iv, key)) {
            throw SecurityException("Hash verification failed - data may have been tampered with")
        }

        return plaintext
    }

    /**
     * 암호화된 데이터에서 메타데이터만 추출
     */
    fun extractMetadata(blob: ByteArray): EncryptedMetadata {
        require(blob.size >= EncryptedMetadata.METADATA_SIZE) { "Invalid blob size" }
        val metadataBytes = blob.copyOfRange(0, EncryptedMetadata.METADATA_SIZE)
        return EncryptedMetadata.fromByteArray(metadataBytes)
    }
}

/**
 * 암호화된 데이터의 메타데이터를 관리하는 클래스
 */
data class EncryptedMetadata(
    val version: String = "V01",
    val hash: ByteArray, // SHA-256 해시 (32바이트)
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        private const val DELIMITER = "::"
        private const val PREFIX = "ENCRYPTED$DELIMITER"
        private const val VERSION_SIZE = 3 + DELIMITER.length
        private const val HASH_SIZE = 32 // SHA-256
        private const val HASH_INDEX = PREFIX.length + VERSION_SIZE
        private const val TIMESTAMP_SIZE = 8 // Long
        private const val TIMESTAMP_INDEX = HASH_INDEX + HASH_SIZE
        const val METADATA_SIZE = PREFIX.length + 5 + HASH_SIZE + TIMESTAMP_SIZE // "V01::" = 5바이트

        /**
         * 평문, IV, 키 정보로 해시 생성
         */
        fun createHash(plaintext: ByteArray, iv: ByteArray, key: ByteArray): ByteArray {
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(plaintext)
            digest.update(iv)
            digest.update(key)
            return digest.digest()
        }

        /**
         * 바이트 배열에서 메타데이터 파싱
         */
        fun fromByteArray(data: ByteArray): EncryptedMetadata {
            require(data.size >= METADATA_SIZE) { "Invalid metadata size" }

            val prefixAndVersion = data.copyOfRange(0, PREFIX.length).toString(Charsets.UTF_8)
            require(prefixAndVersion == PREFIX) { "Invalid prefix" }

            val version = data.copyOfRange(PREFIX.length, PREFIX.length + VERSION_SIZE - DELIMITER.length)
            val hash = data.copyOfRange(HASH_INDEX, HASH_INDEX + HASH_SIZE)
            val timestampBytes = data.copyOfRange(TIMESTAMP_INDEX, TIMESTAMP_INDEX + TIMESTAMP_SIZE)
            val timestamp = timestampBytes.fold(0L) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF).toLong() }

            return EncryptedMetadata(String(version), hash, timestamp)
        }
    }

    /**
     * 메타데이터를 바이트 배열로 변환 (고정 길이)
     */
    fun toByteArray(): ByteArray {
        val prefix = "$PREFIX$version$DELIMITER".toByteArray()
        val timestampBytes = ByteArray(TIMESTAMP_SIZE)
        var temp = timestamp
        for (i in TIMESTAMP_SIZE - 1 downTo 0) {
            timestampBytes[i] = (temp and 0xFF).toByte()
            temp = temp shr 8
        }

        return prefix + hash + timestampBytes
    }

    /**
     * 해시 검증
     */
    fun verifyHash(plaintext: ByteArray, iv: ByteArray, key: ByteArray): Boolean {
        val expectedHash = createHash(plaintext, iv, key)
        return hash.contentEquals(expectedHash)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedMetadata

        if (version != other.version) return false
        if (!hash.contentEquals(other.hash)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + hash.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
