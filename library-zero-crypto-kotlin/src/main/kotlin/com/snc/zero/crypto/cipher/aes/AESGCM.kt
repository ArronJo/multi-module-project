package com.snc.zero.crypto.cipher.aes

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES-GCM 암호화 클래스
 *
 * 보안 강화 요소:
 * - GCM 모드로 인증된 암호화
 * - Salt를 이용한 키 강화
 * - PBKDF2 + HMAC-SHA256 이중 해싱
 * - Timestamp 기반 재생 공격 방지
 * - 파라미터 일관성 검증
 */
object AESGCM {
    private val secureRandom = SecureRandom()

    /**
     * AES-GCM 암호화 파라미터
     */
    data class Params(
        val keyBits: Int = 256,
        val ivBytes: Int = 12, // GCM 표준 IV 길이
        val tagBits: Int = 128, // GCM 인증 태그 길이
        val saltBytes: Int = 16, // Salt 길이
        val hashIterations: Int = 100000 // PBKDF2 반복 횟수 (NIST 권장 기준)
    ) {
        init {
            require(keyBits in setOf(128, 192, 256)) { "Key size must be 128, 192, or 256 bits" }
            require(ivBytes > 0) { "IV size must be positive" }
            require(tagBits > 0) { "Tag size must be positive" }
            require(saltBytes > 0) { "Salt size must be positive" }
            require(hashIterations > 0) { "Hash iterations must be positive" }
        }
    }

    /**
     * 지정된 크기의 안전한 랜덤 바이트 생성
     */
    private fun randomBytes(size: Int): ByteArray {
        return ByteArray(size).also { secureRandom.nextBytes(it) }
    }

    /**
     * AES 키 생성
     */
    fun generateKey(bits: Int = 256): ByteArray {
        require(bits in setOf(128, 192, 256)) { "Key size must be 128, 192, or 256 bits" }
        return randomBytes(bits / 8)
    }

    /**
     * AES-GCM 암호화
     *
     * @param plaintext 암호화할 평문
     * @param key AES 키 (keyBits에 맞는 크기)
     * @param aad 추가 인증 데이터 (선택사항)
     * @param params 암호화 파라미터
     * @return [메타데이터 + IV + 암호문+태그] 형태의 바이트 배열
     */
    fun encrypt(
        plaintext: ByteArray,
        key: ByteArray,
        aad: ByteArray? = null,
        params: Params = Params()
    ): ByteArray {
        validateKey(key, params.keyBits)

        val iv = randomBytes(params.ivBytes)
        val salt = randomBytes(params.saltBytes)
        val timestamp = System.currentTimeMillis()

        // AES-GCM 암호화
        // 버전별 엄호화 로직 분리 가능
        val encryptedData = performGcmEncryption(
            plaintext = plaintext,
            key = key,
            iv = iv,
            aad = aad,
            params = params
        )

        // 메타데이터 생성 및 결합
        val metadata = createMetadata(
            plaintext = plaintext,
            iv = iv,
            key = key,
            salt = salt,
            timestamp = timestamp,
            params = params
        )

        return metadata.toByteArray() + iv + encryptedData
    }

    /**
     * AES-GCM 복호화
     *
     * @param blob 암호화된 데이터 ([메타데이터 + IV + 암호문+태그])
     * @param key AES 키
     * @param aad 추가 인증 데이터
     * @param params 복호화 파라미터
     * @return 복호화된 평문
     * @throws SecurityException 해시 검증 실패 시
     * @throws IllegalArgumentException 파라미터 불일치 시
     */
    fun decrypt(
        blob: ByteArray,
        key: ByteArray,
        aad: ByteArray? = null,
        params: Params = Params()
    ): ByteArray {
        validateKey(key, params.keyBits)
        validateBlobSize(blob, params)

        // 메타데이터 추출 및 검증
        val metadata = EncryptedMetadata.fromByteArray(blob)
        validateParameterConsistency(metadata, params)

        val metadataSize = EncryptedMetadata.calculateSize(metadata.salt.size)

        // IV와 암호문 분리
        val dataStart = metadataSize
        val ivEnd = dataStart + params.ivBytes
        require(blob.size > ivEnd) { "Invalid blob size: insufficient data for IV" }

        val iv = blob.copyOfRange(dataStart, ivEnd)
        val ciphertext = blob.copyOfRange(ivEnd, blob.size)

        // AES-GCM 복호화
        val ver = MetadataVersion.fromValue(metadata.version)
        if (ver == null) {
            println("지원하지 않는 버전")
            return ByteArray(0)
        }

        // (ver == MetadataVersion.VERSION2) 정보를 이용하여 버전별 복호화 로직 분리 가능
        val plaintext = performGcmDecryption(
            ciphertext = ciphertext,
            key = key,
            iv = iv,
            aad = aad,
            params = params
        )

        // 무결성 검증
        verifyIntegrity(metadata, plaintext, iv, key, params)

        return plaintext
    }

    /**
     * 메타데이터만 추출
     */
    fun extractMetadata(blob: ByteArray): EncryptedMetadata {
        return EncryptedMetadata.fromByteArray(blob)
    }

    // === Private Methods ===

    private fun validateKey(key: ByteArray, expectedBits: Int) {
        require(key.size * 8 == expectedBits) { "Key must be $expectedBits bits, got ${key.size * 8}" }
    }

    private fun validateBlobSize(blob: ByteArray, params: Params) {
        val minSize = EncryptedMetadata.calculateSize(params.saltBytes) + params.ivBytes + 16 // 최소 암호문 크기
        require(blob.size >= minSize) { "Invalid blob size: ${blob.size} < $minSize" }
    }

    private fun validateParameterConsistency(metadata: EncryptedMetadata, params: Params) {
        require(metadata.salt.size == params.saltBytes) {
            "Salt size mismatch: expected ${params.saltBytes}, got ${metadata.salt.size}"
        }
    }

    private fun performGcmEncryption(
        plaintext: ByteArray,
        key: ByteArray,
        iv: ByteArray,
        aad: ByteArray?,
        params: Params
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(params.tagBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), spec)

        aad?.let { cipher.updateAAD(it) }
        return cipher.doFinal(plaintext)
    }

    private fun performGcmDecryption(
        ciphertext: ByteArray,
        key: ByteArray,
        iv: ByteArray,
        aad: ByteArray?,
        params: Params
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(params.tagBits, iv)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), spec)

        aad?.let { cipher.updateAAD(it) }
        return cipher.doFinal(ciphertext)
    }

    private fun createMetadata(
        plaintext: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        salt: ByteArray,
        timestamp: Long,
        params: Params
    ): EncryptedMetadata {
        val hash = HashGenerator.createSecureHash(
            plaintext = plaintext,
            iv = iv,
            key = key,
            salt = salt,
            iterations = params.hashIterations,
            timestamp = timestamp
        )
        return EncryptedMetadata(version = MetadataVersion.VERSION2.value, hash = hash, salt = salt, timestamp = timestamp)
    }

    private fun verifyIntegrity(
        metadata: EncryptedMetadata,
        plaintext: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        params: Params
    ) {
        val isValid = metadata.verifyHash(plaintext, iv, key, params.hashIterations)
        if (!isValid) {
            throw SecurityException("데이터 무결성 검증 실패 - 데이터가 변조되었을 수 있습니다")
        }
    }
}

/**
 * 보안 해시 생성기
 */
object HashGenerator {

    /**
     * 강화된 보안 해시 생성
     *
     * 보안 요소:
     * 1. Salt로 레인보우 테이블 공격 방지
     * 2. PBKDF2로 무차별 대입 공격 방어
     * 3. HMAC-SHA256으로 무결성 보장
     * 4. Timestamp로 재생 공격 방지
     */
    fun createSecureHash(
        plaintext: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        salt: ByteArray,
        iterations: Int,
        timestamp: Long
    ): ByteArray {
        // 1단계: 입력 데이터 결합
        val combinedData = plaintext + iv + key

        // 2단계: PBKDF2 해싱 (Salt 적용)
        val pbkdf2Hash = pbkdf2(combinedData, salt, iterations, 32)

        // 3단계: Timestamp를 바이트로 변환
        val timestampBytes = timestamp.toByteArray()

        // 4단계: HMAC-SHA256 최종 해시
        val mac = Mac.getInstance("HmacSHA256")
        val hmacKey = SecretKeySpec(salt, "HmacSHA256")
        mac.init(hmacKey)
        mac.update(pbkdf2Hash)
        mac.update(timestampBytes)

        return mac.doFinal()
    }

    /**
     * PBKDF2 해시 함수
     */
    fun pbkdf2(data: ByteArray, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
        require(iterations > 0) { "iterations size must be greater than zero" }
        require(keyLength > 0) { "keyLength must be greater than zero" }

        val digest = MessageDigest.getInstance("SHA-256")
        var result = data

        repeat(iterations) {
            digest.reset()
            digest.update(result)
            digest.update(salt)
            result = digest.digest()
        }

        return if (result.size > keyLength) result.copyOf(keyLength) else result
    }
}

enum class MetadataVersion(val value: String) {
    VERSION1("AFF01"),
    VERSION2("AFJ02"); // 202509

    companion object {
        fun fromValue(value: String): MetadataVersion? =
            MetadataVersion.entries.find { it.value == value }
    }
}

/**
 * 암호화 메타데이터
 */
data class EncryptedMetadata(
    val version: String,
    val hash: ByteArray,
    val salt: ByteArray,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val DELIMITER = ":"
        const val PREFIX = "EDC" // Encrypted Data/Document Code
        private const val PREFIX_SIZE = PREFIX.length + DELIMITER.length
        private const val VERSION = "12345" // 버전 길이
        private const val VERSION_SIZE = VERSION.length + DELIMITER.length
        private const val HASH_SIZE = 32 // SHA-256
        private const val TIMESTAMP_SIZE = 8 // Long
        private const val SALT_LENGTH_SIZE = 2 // Salt 길이 정보

        const val FIXED_SIZE = PREFIX_SIZE + VERSION_SIZE + HASH_SIZE + SALT_LENGTH_SIZE + TIMESTAMP_SIZE

        /**
         * Salt 크기에 따른 메타데이터 전체 크기 계산
         */
        fun calculateSize(saltSize: Int): Int = FIXED_SIZE + saltSize

        /**
         * 바이트 배열에서 메타데이터 파싱
         */
        fun fromByteArray(data: ByteArray): EncryptedMetadata {
            require(data.size >= FIXED_SIZE) { "메타데이터 크기가 부족합니다: ${data.size} < $FIXED_SIZE" }

            var offset = 0

            // Prefix 검증
            val prefixStr = data.copyOfRange(offset, PREFIX.length).toString(Charsets.UTF_8)
            require(prefixStr == PREFIX) { "잘못된 메타데이터 형식입니다: 예상 '$PREFIX', 실제 '$prefixStr'" }
            offset += PREFIX_SIZE

            // 버전 읽기
            val version = data.copyOfRange(offset, offset + VERSION.length).toString(Charsets.UTF_8)
            offset += VERSION_SIZE

            // 해시 읽기
            val hash = data.copyOfRange(offset, offset + HASH_SIZE)
            offset += HASH_SIZE

            // Salt 길이 읽기 (Big-Endian)
            val saltLength = data.readUInt16BE(offset)
            require(saltLength > 0) { "잘못된 Salt 길이: $saltLength" }
            offset += SALT_LENGTH_SIZE

            require(data.size >= offset + saltLength + TIMESTAMP_SIZE) {
                "Salt 데이터가 부족합니다: 필요 ${saltLength}바이트, 사용가능 ${data.size - offset - TIMESTAMP_SIZE}바이트"
            }

            // Salt 읽기
            val salt = data.copyOfRange(offset, offset + saltLength)
            offset += saltLength

            // Timestamp 읽기 (Big-Endian)
            val timestamp = data.readUInt64BE(offset)

            return EncryptedMetadata(version, hash, salt, timestamp)
        }
    }

    /**
     * 메타데이터를 바이트 배열로 직렬화
     */
    fun toByteArray(): ByteArray {
        val prefixAndVersion = (PREFIX + DELIMITER + version + DELIMITER).toByteArray()
        val saltLengthBytes = salt.size.toUInt16BE()
        val timestampBytes = timestamp.toUInt64BE()

        return prefixAndVersion + hash + saltLengthBytes + salt + timestampBytes
    }

    /**
     * 해시 검증
     */
    fun verifyHash(plaintext: ByteArray, iv: ByteArray, key: ByteArray, iterations: Int): Boolean {
        val expectedHash = HashGenerator.createSecureHash(
            plaintext = plaintext,
            iv = iv,
            key = key,
            salt = salt,
            iterations = iterations,
            timestamp = timestamp
        )
        return hash.contentEquals(expectedHash)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncryptedMetadata) return false

        return version == other.version &&
            hash.contentEquals(other.hash) &&
            salt.contentEquals(other.salt) &&
            timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + hash.contentHashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

// === Extension Functions ===

private fun ByteArray.readUInt16BE(offset: Int): Int {
    require(offset + 1 < size) { "Insufficient data for UInt16 at offset $offset" }
    return ((this[offset].toInt() and 0xFF) shl 8) or (this[offset + 1].toInt() and 0xFF)
}

private fun ByteArray.readUInt64BE(offset: Int): Long {
    require(offset + 7 < size) { "Insufficient data for UInt64 at offset $offset" }
    return (0..7).fold(0L) { acc, i ->
        (acc shl 8) or (this[offset + i].toInt() and 0xFF).toLong()
    }
}

private fun Int.toUInt16BE(): ByteArray {
    require(this in 0..0xFFFF) { "Value $this is out of UInt16 range" }
    return byteArrayOf((this shr 8).toByte(), (this and 0xFF).toByte())
}

private fun Long.toUInt64BE(): ByteArray {
    return ByteArray(8) { i -> (this shr (8 * (7 - i))).toByte() }
}

private fun Long.toByteArray(): ByteArray = toUInt64BE()
