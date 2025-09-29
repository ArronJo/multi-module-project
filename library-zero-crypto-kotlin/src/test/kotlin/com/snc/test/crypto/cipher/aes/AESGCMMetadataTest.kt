package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.cipher.aes.EncryptedMetadata
import com.snc.zero.crypto.cipher.aes.HashGenerator
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

/**
 * 메타데이터 테스트
 *
 * - EncryptedMetadata 클래스 기능
 * - 강화된 해시 생성 및 검증
 * - 가변 Salt 길이 메타데이터
 * - PBKDF2 테스트
 */
@Suppress("NonAsciiCharacters")
@DisplayName("AES-GCM 메타데이터 테스트 (보안 강화)")
open class AESGCMMetadataTest {

    @Nested
    @DisplayName("EncryptedMetadata 클래스 테스트")
    inner class EncryptedMetadataBasic {

        @Test
        @DisplayName("메타데이터가 올바른 AFJ02 프리픽스로 시작해야 한다")
        fun `should start with correct AFJ02 prefix`() {
            // Given
            val hash = ByteArray(32) { it.toByte() }
            val salt = ByteArray(16) { (it * 2).toByte() }
            val metadata = EncryptedMetadata(version = "AFJ02", hash = hash, salt = salt)

            // When
            val bytes = metadata.toByteArray()
            val prefix = bytes.copyOfRange(0, 16).toString(Charsets.UTF_8)

            // Then
            assertTrue(prefix.startsWith("ENW::AFJ02::"), "올바른 AFJ02 프리픽스로 시작해야 한다")
        }

        @Test
        @DisplayName("직렬화와 역직렬화가 올바르게 동작해야 한다 (AFJ02)")
        fun `should serialize and deserialize correctly AFJ02`() {
            // Given
            val originalHash = ByteArray(32) { (it * 3).toByte() }
            val originalSalt = ByteArray(16) { (it * 5).toByte() }
            val originalTimestamp = System.currentTimeMillis()
            val originalMetadata = EncryptedMetadata(
                version = "AFJ02",
                hash = originalHash,
                salt = originalSalt,
                timestamp = originalTimestamp
            )

            // When
            val serialized = originalMetadata.toByteArray()
            val deserialized = EncryptedMetadata.fromByteArray(serialized)

            // Then
            assertEquals(originalMetadata.version, deserialized.version)
            assertArrayEquals(originalMetadata.hash, deserialized.hash)
            assertArrayEquals(originalMetadata.salt, deserialized.salt)
            assertEquals(originalMetadata.timestamp, deserialized.timestamp)
            assertEquals(originalMetadata, deserialized)
        }

        @Test
        @DisplayName("메타데이터가 가변 길이로 직렬화되어야 한다 (AFJ02)")
        fun `should serialize to variable length AFJ02`() {
            // Given
            val hash = ByteArray(32) { it.toByte() }
            val salt16 = ByteArray(16) { (it * 2).toByte() }
            val salt32 = ByteArray(32) { (it * 3).toByte() }

            val metadata1 = EncryptedMetadata(version = "AFJ02", hash = hash, salt = salt16, timestamp = 1000L)
            val metadata2 = EncryptedMetadata(version = "AFJ02", hash = hash, salt = salt32, timestamp = System.currentTimeMillis())

            // When
            val bytes1 = metadata1.toByteArray()
            val bytes2 = metadata2.toByteArray()

            val expectedSize1 = EncryptedMetadata.calculateSize(16)
            val expectedSize2 = EncryptedMetadata.calculateSize(32)

            // Then
            assertEquals(expectedSize1, bytes1.size, "16바이트 Salt의 메타데이터 크기가 맞아야 한다")
            assertEquals(expectedSize2, bytes2.size, "32바이트 Salt의 메타데이터 크기가 맞아야 한다")
            assertTrue(bytes2.size > bytes1.size, "Salt가 큰 메타데이터가 더 커야 한다")
            assertEquals(16, bytes2.size - bytes1.size, "크기 차이는 Salt 차이와 같아야 한다")
        }

        @Test
        @DisplayName("잘못된 크기의 데이터로 역직렬화 시 예외가 발생해야 한다")
        fun `should throw exception for invalid size during deserialization`() {
            // Given
            val invalidData = ByteArray(10)

            // When & Then
            assertThrows(IllegalArgumentException::class.java) {
                EncryptedMetadata.fromByteArray(invalidData)
            }
        }

        @Test
        @DisplayName("잘못된 프리픽스로 역직렬화 시 예외가 발생해야 한다")
        fun `should throw exception for invalid prefix during deserialization`() {
            // Given
            val invalidData = "INVALID::AFJ02::".toByteArray() + ByteArray(32) + ByteArray(16) + ByteArray(8)

            // When & Then
            assertThrows(IllegalArgumentException::class.java) {
                EncryptedMetadata.fromByteArray(invalidData)
            }
        }
    }

    @Nested
    @DisplayName("강화된 해시 생성 테스트")
    inner class SecureHashGeneration {

        @Test
        @DisplayName("강화된 해시 생성이 올바르게 동작해야 한다")
        fun `should create secure hash correctly`() {
            // Given
            val plaintext = "test data".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt = ByteArray(16) { (it * 3).toByte() }
            val iterations = 1000
            val fixedTimestamp = 123456789L // 고정 값으로 검수

            // When
            val hash1 = HashGenerator.createSecureHash(plaintext, iv, key, salt, iterations, fixedTimestamp)
            val hash2 = HashGenerator.createSecureHash(plaintext, iv, key, salt, iterations, fixedTimestamp)

            // Then
            assertEquals(32, hash1.size, "HMAC-SHA256 해시는 32바이트여야 한다")
            assertArrayEquals(hash1, hash2, "동일한 입력에 대해 동일한 해시가 생성되어야 한다")
        }

        @Test
        @DisplayName("다른 입력에 대해 다른 해시가 생성되어야 한다")
        fun `should create different hash for different inputs`() {
            // Given
            val plaintext1 = "test data 1".toByteArray()
            val plaintext2 = "test data 2".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt = ByteArray(16) { (it * 3).toByte() }
            val iterations = 1000
            val fixedTimestamp = System.currentTimeMillis()

            // When
            val hash1 = HashGenerator.createSecureHash(plaintext1, iv, key, salt, iterations, fixedTimestamp)
            val hash2 = HashGenerator.createSecureHash(plaintext2, iv, key, salt, iterations, fixedTimestamp)

            // Then
            assertFalse(hash1.contentEquals(hash2), "다른 평문에 대해 다른 해시가 생성되어야 한다")
        }

        @Test
        @DisplayName("다른 Salt에 대해 다른 해시가 생성되어야 한다")
        fun `should create different hash for different salt`() {
            // Given
            val plaintext = "test data".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt1 = ByteArray(16) { (it * 3).toByte() }
            val salt2 = ByteArray(16) { (it * 4).toByte() }
            val iterations = 1000
            val fixedTimestamp = System.currentTimeMillis()

            // When
            val hash1 = HashGenerator.createSecureHash(plaintext, iv, key, salt1, iterations, fixedTimestamp)
            val hash2 = HashGenerator.createSecureHash(plaintext, iv, key, salt2, iterations, fixedTimestamp)

            // Then
            assertFalse(hash1.contentEquals(hash2), "다른 Salt에 대해 다른 해시가 생성되어야 한다")
        }

        @Test
        @DisplayName("PBKDF2 반복 횟수 차이로 다른 해시가 생성되어야 한다")
        fun `should create different hash for different iterations`() {
            // Given
            val plaintext = "test data".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt = ByteArray(16) { (it * 3).toByte() }
            val fixedTimestamp = System.currentTimeMillis()

            // When
            val hash1000 = HashGenerator.createSecureHash(plaintext, iv, key, salt, 1000, fixedTimestamp)
            val hash2000 = HashGenerator.createSecureHash(plaintext, iv, key, salt, 2000, fixedTimestamp)

            // Then
            assertFalse(hash1000.contentEquals(hash2000), "다른 반복 횟수에 대해 다른 해시가 생성되어야 한다")
        }

        @Test
        @DisplayName("타임스탬프 차이로 다른 해시가 생성되어야 한다")
        fun `should create different hash for different timestamp`() {
            // Given
            val plaintext = "timestamp test".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt = ByteArray(16) { (it * 3).toByte() }
            val iterations = 1000

            // When
            val timestamp1 = 1000000L
            val timestamp2 = 2000000L
            val hash1 = HashGenerator.createSecureHash(plaintext, iv, key, salt, iterations, timestamp1)
            val hash2 = HashGenerator.createSecureHash(plaintext, iv, key, salt, iterations, timestamp2)

            // Then
            assertFalse(hash1.contentEquals(hash2), "타임스탬프 차이로 인해 다른 해시가 생성되어야 한다")
        }
    }

    @Nested
    @DisplayName("해시 검증 테스트")
    inner class HashVerification {

        @Test
        @DisplayName("강화된 해시 검증이 올바르게 동작해야 한다")
        fun `should verify secure hash correctly`() {
            // Given
            val plaintext = "test data".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt = ByteArray(16) { (it * 3).toByte() }
            val iterations = 1000
            val fixedTimestamp = 123456789L
            val hash = HashGenerator.createSecureHash(plaintext, iv, key, salt, iterations, fixedTimestamp)
            val metadata = EncryptedMetadata(version = "M02", hash = hash, salt = salt, timestamp = fixedTimestamp)

            // When & Then
            assertTrue(
                metadata.verifyHash(plaintext, iv, key, iterations),
                "올바른 데이터에 대해 검증이 성공해야 한다"
            )

            val wrongPlaintext = "wrong data".toByteArray()
            assertFalse(
                metadata.verifyHash(wrongPlaintext, iv, key, iterations),
                "잘못된 평문에 대해 검증이 실패해야 한다"
            )

            val wrongIv = ByteArray(12) { (it + 1).toByte() }
            assertFalse(
                metadata.verifyHash(plaintext, wrongIv, key, iterations),
                "잘못된 IV에 대해 검증이 실패해야 한다"
            )

            val wrongKey = ByteArray(32) { (it * 3).toByte() }
            assertFalse(
                metadata.verifyHash(plaintext, iv, wrongKey, iterations),
                "잘못된 키에 대해 검증이 실패해야 한다"
            )
        }
    }

    @Nested
    @DisplayName("가변 Salt 길이 메타데이터 테스트")
    inner class VariableSaltMetadata {

        @Test
        @DisplayName("다양한 Salt 크기의 메타데이터를 올바르게 직렬화/역직렬화해야 한다")
        fun `should handle various salt sizes correctly`() {
            val saltSizes = listOf(8, 16, 24, 32, 64)
            val hash = ByteArray(32) { it.toByte() }
            val timestamp = System.currentTimeMillis()

            saltSizes.forEach { saltSize ->
                // Given
                val salt = ByteArray(saltSize) { (it % 256).toByte() }
                val originalMetadata = EncryptedMetadata(version = "AFJ02", hash = hash, salt = salt, timestamp = timestamp)

                // When
                val serialized = originalMetadata.toByteArray()
                val deserialized = EncryptedMetadata.fromByteArray(serialized)

                // Then
                assertEquals(
                    EncryptedMetadata.calculateSize(saltSize),
                    serialized.size,
                    "Salt 크기 ${saltSize}의 메타데이터 크기가 맞아야 한다"
                )
                assertEquals(
                    originalMetadata,
                    deserialized,
                    "Salt 크기 ${saltSize}의 메타데이터가 정확히 복원되어야 한다"
                )
            }
        }
    }

    @Nested
    @DisplayName("메타데이터 추출 테스트")
    inner class MetadataExtraction {

        private val testKey = AESGCM.generateKey(256)
        private val testPlaintext = "메타데이터 추출 테스트".toByteArray()

        @Test
        @DisplayName("메타데이터 추출이 올바르게 동작해야 한다 (AFJ02)")
        fun `should extract AFJ02 metadata correctly`() {
            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals("AFJ02", metadata.version, "올바른 AFJ02 버전이어야 한다")
            assertEquals(32, metadata.hash.size, "해시 크기가 32바이트여야 한다")
            assertEquals(16, metadata.salt.size, "Salt 크기가 16바이트여야 한다")
            assertTrue(metadata.timestamp > 0, "타임스탬프가 설정되어야 한다")
        }

        @Test
        @DisplayName("커스텀 Salt 크기가 올바르게 추출되어야 한다")
        fun `should extract custom salt size correctly`() {
            // Given
            val params = AESGCM.Params(saltBytes = 32)

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = params)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals(32, metadata.salt.size, "커스텀 Salt 크기가 추출되어야 한다")
        }

        @Test
        @DisplayName("타임스탬프가 올바른 범위에 있어야 한다")
        fun `should have timestamp in valid range`() {
            // Given
            val beforeEncrypt = System.currentTimeMillis()

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val metadata = AESGCM.extractMetadata(encrypted)
            val afterExtract = System.currentTimeMillis()

            // Then
            assertTrue(metadata.timestamp >= beforeEncrypt, "타임스탬프가 암호화 시점 이후여야 한다")
            assertTrue(metadata.timestamp <= afterExtract, "타임스탬프가 추출 시점 이전이어야 한다")
        }
    }

    @Nested
    @DisplayName("PBKDF2 테스트")
    inner class PBKDF2Test {

        @Test
        @DisplayName("PBKDF2 해시 함수가 올바르게 동작해야 한다")
        fun `should work pbkdf2 hash function correctly`() {
            // Given
            val plaintext = "test data".toByteArray()
            val salt = ByteArray(16) { (it * 3).toByte() }
            val iterations = 1000
            val keyLength = 16

            // When
            val result = HashGenerator.pbkdf2(plaintext, salt, iterations, keyLength)

            // Then
            assertEquals(keyLength, result.size, "요청한 키 길이와 결과 길이가 같아야 한다")
        }

        @Test
        @DisplayName("동일한 입력에 대해 동일한 PBKDF2 결과가 나와야 한다")
        fun `should produce same pbkdf2 result for same input`() {
            // Given
            val plaintext = "test data".toByteArray()
            val salt = ByteArray(16) { (it * 3).toByte() }
            val iterations = 1000
            val keyLength = 32

            // When
            val result1 = HashGenerator.pbkdf2(plaintext, salt, iterations, keyLength)
            val result2 = HashGenerator.pbkdf2(plaintext, salt, iterations, keyLength)

            // Then
            assertArrayEquals(result1, result2, "동일한 입력에 대해 동일한 결과가 나와야 한다")
        }

        @Test
        @DisplayName("다른 Salt로 다른 PBKDF2 결과가 나와야 한다")
        fun `should produce different pbkdf2 result for different salt`() {
            // Given
            val plaintext = "test data".toByteArray()
            val salt1 = ByteArray(16) { (it * 3).toByte() }
            val salt2 = ByteArray(16) { (it * 4).toByte() }
            val iterations = 1000
            val keyLength = 32

            // When
            val result1 = HashGenerator.pbkdf2(plaintext, salt1, iterations, keyLength)
            val result2 = HashGenerator.pbkdf2(plaintext, salt2, iterations, keyLength)

            // Then
            assertFalse(result1.contentEquals(result2), "다른 Salt에 대해 다른 결과가 나와야 한다")
        }
    }

    @Nested
    @DisplayName("equals() 메서드")
    inner class EqualsTest {

        @Test
        @DisplayName("동일한 모든 필드를 가진 객체들은 같아야 한다")
        fun `동일한 모든 필드를 가진 객체들은 같아야 한다`() {
            // Given
            val version = "AFJ02"
            val hash = byteArrayOf(1, 2, 3, 4, 5)
            val salt = byteArrayOf(10, 20, 30)
            val timestamp = 1234567890L

            val metadata1 = EncryptedMetadata(version, hash, salt, timestamp)
            val metadata2 = EncryptedMetadata(version, hash, salt, timestamp)

            // When & Then
            assertTrue(metadata1.equals(metadata2))
            assertTrue(metadata2.equals(metadata1))
        }

        @Test
        @DisplayName("같은 객체 참조는 자기 자신과 같아야 한다")
        fun `같은 객체 참조는 자기 자신과 같아야 한다`() {
            // Given
            val metadata = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = 1234567890L
            )

            // When & Then
            assertTrue(metadata.equals(metadata))
        }

        @Test
        @DisplayName("null과 비교시 false를 반환해야 한다")
        fun `null과 비교시 false를 반환해야 한다`() {
            // Given
            val metadata = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = 1234567890L
            )

            // When & Then
            assertFalse(metadata.equals(null))
        }

        @Test
        @DisplayName("다른 타입의 객체와 비교시 false를 반환해야 한다")
        fun `다른 타입의 객체와 비교시 false를 반환해야 한다`() {
            // Given
            val metadata = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = 1234567890L
            )
            val other = "Not an EncryptedMetadata"

            // When & Then
            assertFalse(metadata.equals(other))
        }

        @Test
        @DisplayName("version이 다른 경우 false를 반환해야 한다")
        fun `version이 다른 경우 false를 반환해야 한다`() {
            // Given
            val baseHash = byteArrayOf(1, 2, 3)
            val baseSalt = byteArrayOf(4, 5, 6)
            val baseTimestamp = 1234567890L

            val metadata1 = EncryptedMetadata("AFJ01", baseHash, baseSalt, baseTimestamp)
            val metadata2 = EncryptedMetadata("AFJ02", baseHash, baseSalt, baseTimestamp)

            // When & Then
            assertFalse(metadata1.equals(metadata2))
        }

        @Test
        @DisplayName("hash가 다른 경우 false를 반환해야 한다")
        fun `hash가 다른 경우 false를 반환해야 한다`() {
            // Given
            val version = "AFJ02"
            val salt = byteArrayOf(4, 5, 6)
            val timestamp = 1234567890L

            val metadata1 = EncryptedMetadata(version, byteArrayOf(1, 2, 3), salt, timestamp)
            val metadata2 = EncryptedMetadata(version, byteArrayOf(7, 8, 9), salt, timestamp)

            // When & Then
            assertFalse(metadata1.equals(metadata2))
        }

        @Test
        @DisplayName("hash 길이가 다른 경우 false를 반환해야 한다")
        fun `hash 길이가 다른 경우 false를 반환해야 한다`() {
            // Given
            val version = "AFJ02"
            val salt = byteArrayOf(4, 5, 6)
            val timestamp = 1234567890L

            val metadata1 = EncryptedMetadata(version, byteArrayOf(1, 2, 3), salt, timestamp)
            val metadata2 = EncryptedMetadata(version, byteArrayOf(1, 2, 3, 4), salt, timestamp)

            // When & Then
            assertFalse(metadata1.equals(metadata2))
        }

        @Test
        @DisplayName("salt가 다른 경우 false를 반환해야 한다")
        fun `salt가 다른 경우 false를 반환해야 한다`() {
            // Given
            val version = "AFJ02"
            val hash = byteArrayOf(1, 2, 3)
            val timestamp = 1234567890L

            val metadata1 = EncryptedMetadata(version, hash, byteArrayOf(4, 5, 6), timestamp)
            val metadata2 = EncryptedMetadata(version, hash, byteArrayOf(7, 8, 9), timestamp)

            // When & Then
            assertFalse(metadata1.equals(metadata2))
        }

        @Test
        @DisplayName("salt 길이가 다른 경우 false를 반환해야 한다")
        fun `salt 길이가 다른 경우 false를 반환해야 한다`() {
            // Given
            val version = "AFJ02"
            val hash = byteArrayOf(1, 2, 3)
            val timestamp = 1234567890L

            val metadata1 = EncryptedMetadata(version, hash, byteArrayOf(4, 5, 6), timestamp)
            val metadata2 = EncryptedMetadata(version, hash, byteArrayOf(4, 5, 6, 7), timestamp)

            // When & Then
            assertFalse(metadata1.equals(metadata2))
        }

        @Test
        @DisplayName("timestamp가 다른 경우 false를 반환해야 한다")
        fun `timestamp가 다른 경우 false를 반환해야 한다`() {
            // Given
            val version = "AFJ02"
            val hash = byteArrayOf(1, 2, 3)
            val salt = byteArrayOf(4, 5, 6)

            val metadata1 = EncryptedMetadata(version, hash, salt, 1234567890L)
            val metadata2 = EncryptedMetadata(version, hash, salt, 9876543210L)

            // When & Then
            assertFalse(metadata1.equals(metadata2))
        }

        @Test
        @DisplayName("빈 배열들도 올바르게 비교되어야 한다")
        fun `빈 배열들도 올바르게 비교되어야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(),
                salt = byteArrayOf(),
                timestamp = 1234567890L
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(),
                salt = byteArrayOf(),
                timestamp = 1234567890L
            )

            // When & Then
            assertTrue(metadata1.equals(metadata2))
        }
    }

    @Nested
    @DisplayName("hashCode() 메서드")
    inner class HashCodeTest {

        @Test
        @DisplayName("동일한 객체는 같은 hashCode를 가져야 한다")
        fun `동일한 객체는 같은 hashCode를 가져야 한다`() {
            // Given
            val version = "AFJ02"
            val hash = byteArrayOf(1, 2, 3, 4, 5)
            val salt = byteArrayOf(10, 20, 30)
            val timestamp = 1234567890L

            val metadata1 = EncryptedMetadata(version, hash, salt, timestamp)
            val metadata2 = EncryptedMetadata(version, hash, salt, timestamp)

            // When & Then
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }

        @Test
        @DisplayName("서로 다른 객체는 다른 hashCode를 가져야 한다")
        fun `서로 다른 객체는 다른 hashCode를 가져야 한다`() {
            // Given
            val baseVersion = "AFJ02"
            val baseHash = byteArrayOf(1, 2, 3)
            val baseSalt = byteArrayOf(4, 5, 6)
            val baseTimestamp = 1234567890L

            val metadata1 = EncryptedMetadata(baseVersion, baseHash, baseSalt, baseTimestamp)
            val metadata2 = EncryptedMetadata("AFJ01", baseHash, baseSalt, baseTimestamp) // version 다름
            val metadata3 = EncryptedMetadata(baseVersion, byteArrayOf(7, 8, 9), baseSalt, baseTimestamp) // hash 다름
            val metadata4 = EncryptedMetadata(baseVersion, baseHash, byteArrayOf(7, 8, 9), baseTimestamp) // salt 다름
            val metadata5 = EncryptedMetadata(baseVersion, baseHash, baseSalt, 9876543210L) // timestamp 다름

            // When & Then
            assertAll(
                { assertNotEquals(metadata1.hashCode(), metadata2.hashCode()) },
                { assertNotEquals(metadata1.hashCode(), metadata3.hashCode()) },
                { assertNotEquals(metadata1.hashCode(), metadata4.hashCode()) },
                { assertNotEquals(metadata1.hashCode(), metadata5.hashCode()) }
            )
        }

        @Test
        @DisplayName("hashCode 일관성 - 동일한 객체는 여러 번 호출해도 같은 값을 반환해야 한다")
        fun `hashCode 일관성을 유지해야 한다`() {
            // Given
            val metadata = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3, 4, 5),
                salt = byteArrayOf(10, 20, 30),
                timestamp = 1234567890L
            )

            // When
            val hashCode1 = metadata.hashCode()
            val hashCode2 = metadata.hashCode()
            val hashCode3 = metadata.hashCode()

            // Then
            assertAll(
                { assertEquals(hashCode1, hashCode2) },
                { assertEquals(hashCode2, hashCode3) },
                { assertEquals(hashCode1, hashCode3) }
            )
        }

        @Test
        @DisplayName("빈 배열을 가진 객체도 올바른 hashCode를 생성해야 한다")
        fun `빈 배열을 가진 객체도 올바른 hashCode를 생성해야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(),
                salt = byteArrayOf(),
                timestamp = 1234567890L
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(),
                salt = byteArrayOf(),
                timestamp = 1234567890L
            )

            // When & Then
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }

        @Test
        @DisplayName("각 필드의 변경이 hashCode에 영향을 주어야 한다")
        fun `각 필드의 변경이 hashCode에 영향을 주어야 한다`() {
            // Given
            val original = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = 1234567890L
            )

            // When & Then
            assertAll(
                {
                    val modified = EncryptedMetadata("V01", original.hash, original.salt, original.timestamp)
                    assertNotEquals(original.hashCode(), modified.hashCode(), "version 변경시 hashCode가 달라야 함")
                },
                {
                    val modified = EncryptedMetadata(original.version, byteArrayOf(7, 8, 9), original.salt, original.timestamp)
                    assertNotEquals(original.hashCode(), modified.hashCode(), "hash 변경시 hashCode가 달라야 함")
                },
                {
                    val modified = EncryptedMetadata(original.version, original.hash, byteArrayOf(7, 8, 9), original.timestamp)
                    assertNotEquals(original.hashCode(), modified.hashCode(), "salt 변경시 hashCode가 달라야 함")
                },
                {
                    val modified = EncryptedMetadata(original.version, original.hash, original.salt, 9876543210L)
                    assertNotEquals(original.hashCode(), modified.hashCode(), "timestamp 변경시 hashCode가 달라야 함")
                }
            )
        }
    }

    @Nested
    @DisplayName("equals()와 hashCode() 계약")
    inner class EqualsHashCodeContractTest {

        @Test
        @DisplayName("equals가 true이면 hashCode도 같아야 한다")
        fun `equals가 true이면 hashCode도 같아야 한다`() {
            // Given
            val hash = byteArrayOf(1, 2, 3, 4, 5)
            val salt = byteArrayOf(10, 20, 30, 40)
            val timestamp = 1234567890L

            val metadata1 = EncryptedMetadata("AFJ02", hash, salt, timestamp)
            val metadata2 = EncryptedMetadata("AFJ02", hash, salt, timestamp)

            // When & Then
            assertTrue(metadata1.equals(metadata2))
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }

        @Test
        @DisplayName("equals가 false인 경우 hashCode는 다를 가능성이 높아야 한다")
        fun `equals가 false인 경우 hashCode는 다를 가능성이 높아야 한다`() {
            // Given
            val baseMetadata = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = 1234567890L
            )

            val differentMetadataList = listOf(
                EncryptedMetadata("V01", baseMetadata.hash, baseMetadata.salt, baseMetadata.timestamp),
                EncryptedMetadata(baseMetadata.version, byteArrayOf(7, 8, 9), baseMetadata.salt, baseMetadata.timestamp),
                EncryptedMetadata(baseMetadata.version, baseMetadata.hash, byteArrayOf(7, 8, 9), baseMetadata.timestamp),
                EncryptedMetadata(baseMetadata.version, baseMetadata.hash, baseMetadata.salt, 9876543210L)
            )

            // When & Then
            differentMetadataList.forEach { differentMetadata ->
                assertFalse(baseMetadata.equals(differentMetadata))
                assertNotEquals(
                    baseMetadata.hashCode(),
                    differentMetadata.hashCode(),
                    "서로 다른 객체는 다른 hashCode를 가져야 합니다"
                )
            }
        }

        @Test
        @DisplayName("바이트 배열의 내용이 같으면 equals와 hashCode가 일치해야 한다")
        fun `바이트 배열의 내용이 같으면 equals와 hashCode가 일치해야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3, 4, 5),
                salt = byteArrayOf(10, 20, 30),
                timestamp = 1234567890L
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3, 4, 5), // 같은 내용의 새로운 배열
                salt = byteArrayOf(10, 20, 30), // 같은 내용의 새로운 배열
                timestamp = 1234567890L
            )

            // When & Then
            assertTrue(metadata1.equals(metadata2))
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }

        @Test
        @DisplayName("바이트 배열의 순서가 다르면 equals와 hashCode가 달라야 한다")
        fun `바이트 배열의 순서가 다르면 equals와 hashCode가 달라야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = 1234567890L
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(3, 2, 1), // 순서가 다름
                salt = byteArrayOf(6, 5, 4), // 순서가 다름
                timestamp = 1234567890L
            )

            // When & Then
            assertFalse(metadata1.equals(metadata2))
            assertNotEquals(metadata1.hashCode(), metadata2.hashCode())
        }
    }

    @Nested
    @DisplayName("극단적인 경우")
    inner class EdgeCaseTest {

        @Test
        @DisplayName("매우 큰 바이트 배열도 올바르게 처리되어야 한다")
        fun `매우 큰 바이트 배열도 올바르게 처리되어야 한다`() {
            // Given
            val largeHash = ByteArray(1024) { it.toByte() }
            val largeSalt = ByteArray(512) { (it * 2).toByte() }

            val metadata1 = EncryptedMetadata("Z02", largeHash, largeSalt, 1234567890L)
            val metadata2 = EncryptedMetadata("Z02", largeHash, largeSalt, 1234567890L)

            // When & Then
            assertTrue(metadata1.equals(metadata2))
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }

        @Test
        @DisplayName("최대값 timestamp도 올바르게 처리되어야 한다")
        fun `최대값 timestamp도 올바르게 처리되어야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = Long.MAX_VALUE
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = Long.MAX_VALUE
            )

            // When & Then
            assertTrue(metadata1.equals(metadata2))
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }

        @Test
        @DisplayName("최소값 timestamp도 올바르게 처리되어야 한다")
        fun `최소값 timestamp도 올바르게 처리되어야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = Long.MIN_VALUE
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(1, 2, 3),
                salt = byteArrayOf(4, 5, 6),
                timestamp = Long.MIN_VALUE
            )

            // When & Then
            assertTrue(metadata1.equals(metadata2))
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }

        @Test
        @DisplayName("단일 바이트 배열도 올바르게 처리되어야 한다")
        fun `단일 바이트 배열도 올바르게 처리되어야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(42),
                salt = byteArrayOf(-1),
                timestamp = 0L
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = byteArrayOf(42),
                salt = byteArrayOf(-1),
                timestamp = 0L
            )

            // When & Then
            assertTrue(metadata1.equals(metadata2))
            assertEquals(metadata1.hashCode(), metadata2.hashCode())
        }
    }

    @Nested
    @DisplayName("성능 테스트")
    inner class PerformanceTest {

        @Test
        @DisplayName("equals 연산이 효율적으로 수행되어야 한다")
        fun `equals 연산이 효율적으로 수행되어야 한다`() {
            // Given
            val metadata1 = EncryptedMetadata(
                version = "M02",
                hash = ByteArray(32) { it.toByte() },
                salt = ByteArray(16) { it.toByte() },
                timestamp = 1234567890L
            )
            val metadata2 = EncryptedMetadata(
                version = "M02",
                hash = ByteArray(32) { it.toByte() },
                salt = ByteArray(16) { it.toByte() },
                timestamp = 1234567890L
            )

            // When
            val startTime = System.nanoTime()
            repeat(1000) {
                metadata1.equals(metadata2)
            }
            val endTime = System.nanoTime()
            val duration = endTime - startTime

            // Then
            assertTrue(duration < 10_000_000) // 10ms 미만
            assertTrue(metadata1.equals(metadata2))
        }

        @Test
        @DisplayName("hashCode 연산이 효율적으로 수행되어야 한다")
        fun `hashCode 연산이 효율적으로 수행되어야 한다`() {
            // Given
            val metadata = EncryptedMetadata(
                version = "M02",
                hash = ByteArray(32) { it.toByte() },
                salt = ByteArray(16) { it.toByte() },
                timestamp = 1234567890L
            )

            // When
            val startTime = System.nanoTime()
            repeat(1000) {
                metadata.hashCode()
            }
            val endTime = System.nanoTime()
            val duration = endTime - startTime

            // Then
            assertTrue(duration < 10_000_000) // 10ms 미만
        }
    }
}
