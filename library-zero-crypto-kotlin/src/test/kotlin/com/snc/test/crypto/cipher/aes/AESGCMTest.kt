package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.cipher.aes.EncryptedMetadata
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("AES-GCM 암호화 테스트")
class AESGCMTest : BaseJUnit5Test() {

    @Nested
    inner class EncryptDecrypt() {

        @Test
        fun `encrypt and decrypt should return original plaintext`() {
            val key = AESGCM.generateAesKey()
            val plaintext = "Hello, Kotlin AES-GCM!".toByteArray(Charsets.UTF_8)

            val encrypted = AESGCM.encrypt(plaintext, key)
            val decrypted = AESGCM.decrypt(encrypted, key)

            assertArrayEquals(plaintext, decrypted)
        }

        @Test
        fun `decrypt with wrong key should throw exception`() {
            val key1 = AESGCM.generateAesKey(256)
            val key2 = AESGCM.generateAesKey(256)
            val plaintext = "Sensitive data".toByteArray(Charsets.UTF_8)

            val encrypted = AESGCM.encrypt(plaintext, key1)

            assertThrows(Exception::class.java) {
                AESGCM.decrypt(encrypted, key2)
            }
        }

        @Test
        fun `encrypted data should contain iv and ciphertext`() {
            val key = AESGCM.generateAesKey()
            val plaintext = "Short text".toByteArray(Charsets.UTF_8)

            val encrypted = AESGCM.encrypt(plaintext, key)

            // IV는 12바이트여야 하며 암호문은 최소 몇 바이트 이상
            assertTrue(encrypted.size > 12)
        }
    }

    @Nested
    inner class NewTestCase() {

        private val testKey = AESGCM.generateAesKey(256)
        private val testPlaintext = "Hello, World! 안녕하세요!".toByteArray()

        @Nested
        @DisplayName("EncryptedMetadata 클래스 테스트")
        inner class EncryptedMetadataTest {

            @Test
            @DisplayName("해시 생성이 올바르게 동작해야 한다")
            fun `should create hash correctly`() {
                // Given
                val plaintext = "test data".toByteArray()
                val iv = ByteArray(12) { it.toByte() }
                val key = ByteArray(32) { (it * 2).toByte() }

                // When
                val hash1 = EncryptedMetadata.createHash(plaintext, iv, key)
                val hash2 = EncryptedMetadata.createHash(plaintext, iv, key)

                // Then
                assertEquals(32, hash1.size, "SHA-256 해시는 32바이트여야 한다")
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

                // When
                val hash1 = EncryptedMetadata.createHash(plaintext1, iv, key)
                val hash2 = EncryptedMetadata.createHash(plaintext2, iv, key)

                // Then
                assertFalse(hash1.contentEquals(hash2), "다른 평문에 대해 다른 해시가 생성되어야 한다")
            }

            @Test
            @DisplayName("메타데이터가 고정 길이로 직렬화되어야 한다")
            fun `should serialize to fixed length`() {
                // Given
                val hash = ByteArray(32) { it.toByte() }
                val metadata1 = EncryptedMetadata(hash = hash, timestamp = 1000L)
                val metadata2 = EncryptedMetadata(hash = hash, timestamp = System.currentTimeMillis())

                // When
                val bytes1 = metadata1.toByteArray()
                val bytes2 = metadata2.toByteArray()

                // Then
                assertEquals(EncryptedMetadata.METADATA_SIZE, bytes1.size, "고정 길이여야 한다")
                assertEquals(EncryptedMetadata.METADATA_SIZE, bytes2.size, "고정 길이여야 한다")
                assertEquals(bytes1.size, bytes2.size, "모든 메타데이터는 동일한 길이여야 한다")
            }

            @Test
            @DisplayName("메타데이터가 올바른 프리픽스로 시작해야 한다")
            fun `should start with correct prefix`() {
                // Given
                val hash = ByteArray(32) { it.toByte() }
                val metadata = EncryptedMetadata(hash = hash)

                // When
                val bytes = metadata.toByteArray()
                val prefix = bytes.copyOfRange(0, 16).toString(Charsets.UTF_8)

                // Then
                assertTrue(prefix.startsWith("ENCRYPTED::V01::"), "올바른 프리픽스로 시작해야 한다")
            }

            @Test
            @DisplayName("직렬화와 역직렬화가 올바르게 동작해야 한다")
            fun `should serialize and deserialize correctly`() {
                // Given
                val originalHash = ByteArray(32) { (it * 3).toByte() }
                val originalTimestamp = System.currentTimeMillis()
                val originalMetadata = EncryptedMetadata(
                    version = "V01",
                    hash = originalHash,
                    timestamp = originalTimestamp
                )

                // When
                val serialized = originalMetadata.toByteArray()
                val deserialized = EncryptedMetadata.fromByteArray(serialized)

                // Then
                assertEquals(originalMetadata.version, deserialized.version)
                assertArrayEquals(originalMetadata.hash, deserialized.hash)
                assertEquals(originalMetadata.timestamp, deserialized.timestamp)
                assertEquals(originalMetadata, deserialized)
            }

            @Test
            @DisplayName("해시 검증이 올바르게 동작해야 한다")
            fun `should verify hash correctly`() {
                // Given
                val plaintext = "test data".toByteArray()
                val iv = ByteArray(12) { it.toByte() }
                val key = ByteArray(32) { (it * 2).toByte() }
                val hash = EncryptedMetadata.createHash(plaintext, iv, key)
                val metadata = EncryptedMetadata(hash = hash)

                // When & Then
                assertTrue(metadata.verifyHash(plaintext, iv, key), "올바른 데이터에 대해 검증이 성공해야 한다")

                val wrongPlaintext = "wrong data".toByteArray()
                assertFalse(metadata.verifyHash(wrongPlaintext, iv, key), "잘못된 평문에 대해 검증이 실패해야 한다")

                val wrongIv = ByteArray(12) { (it + 1).toByte() }
                assertFalse(metadata.verifyHash(plaintext, wrongIv, key), "잘못된 IV에 대해 검증이 실패해야 한다")

                val wrongKey = ByteArray(32) { (it * 3).toByte() }
                assertFalse(metadata.verifyHash(plaintext, iv, wrongKey), "잘못된 키에 대해 검증이 실패해야 한다")
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
                val invalidData = "INVALID::V01::".toByteArray() + ByteArray(32) + ByteArray(8)

                // When & Then
                assertThrows(IllegalArgumentException::class.java) {
                    EncryptedMetadata.fromByteArray(invalidData)
                }
            }
        }

        @Nested
        @DisplayName("AESGCM 키 생성 테스트")
        inner class KeyGenerationTest {

            @Test
            @DisplayName("256비트 키가 올바르게 생성되어야 한다")
            fun `should generate 256 bit key correctly`() {
                // When
                val key = AESGCM.generateAesKey(256)

                // Then
                assertEquals(32, key.size, "256비트는 32바이트여야 한다")
            }

            @Test
            @DisplayName("192비트 키가 올바르게 생성되어야 한다")
            fun `should generate 192 bit key correctly`() {
                // When
                val key = AESGCM.generateAesKey(192)

                // Then
                assertEquals(24, key.size, "192비트는 24바이트여야 한다")
            }

            @Test
            @DisplayName("128비트 키가 올바르게 생성되어야 한다")
            fun `should generate 128 bit key correctly`() {
                // When
                val key = AESGCM.generateAesKey(128)

                // Then
                assertEquals(16, key.size, "128비트는 16바이트여야 한다")
            }

            @Test
            @DisplayName("지원하지 않는 키 길이에 대해 예외가 발생해야 한다")
            fun `should throw exception for unsupported key length`() {
                // When & Then
                assertThrows(IllegalArgumentException::class.java) {
                    AESGCM.generateAesKey(64)
                }

                assertThrows(IllegalArgumentException::class.java) {
                    AESGCM.generateAesKey(512)
                }
            }

            @Test
            @DisplayName("매번 다른 키가 생성되어야 한다")
            fun `should generate different keys each time`() {
                // When
                val key1 = AESGCM.generateAesKey(256)
                val key2 = AESGCM.generateAesKey(256)

                // Then
                assertFalse(key1.contentEquals(key2), "매번 다른 키가 생성되어야 한다")
            }
        }

        @Nested
        @DisplayName("AESGCM 암복호화 테스트")
        inner class EncryptDecryptTest {

            @Test
            @DisplayName("기본 암복호화가 올바르게 동작해야 한다")
            fun `should encrypt and decrypt correctly`() {
                // When
                val encrypted = AESGCM.encrypt(testPlaintext, testKey)
                val decrypted = AESGCM.decrypt(encrypted, testKey)

                // Then
                assertArrayEquals(testPlaintext, decrypted, "복호화된 데이터가 원본과 일치해야 한다")
            }

            @Test
            @DisplayName("AAD를 사용한 암복호화가 올바르게 동작해야 한다")
            fun `should encrypt and decrypt with AAD correctly`() {
                // Given
                val aad = "additional authenticated data".toByteArray()

                // When
                val encrypted = AESGCM.encrypt(testPlaintext, testKey, aad)
                val decrypted = AESGCM.decrypt(encrypted, testKey, aad)

                // Then
                assertArrayEquals(testPlaintext, decrypted, "AAD 사용 시에도 올바르게 복호화되어야 한다")
            }

            @Test
            @DisplayName("다른 AAD로 복호화 시 예외가 발생해야 한다")
            fun `should throw exception when decrypting with wrong AAD`() {
                // Given
                val aad1 = "correct aad".toByteArray()
                val aad2 = "wrong aad".toByteArray()
                val encrypted = AESGCM.encrypt(testPlaintext, testKey, aad1)

                // When & Then
                assertThrows(Exception::class.java) {
                    AESGCM.decrypt(encrypted, testKey, aad2)
                }
            }

            @Test
            @DisplayName("잘못된 키로 복호화 시 예외가 발생해야 한다")
            fun `should throw exception when decrypting with wrong key`() {
                // Given
                val wrongKey = AESGCM.generateAesKey(256)
                val encrypted = AESGCM.encrypt(testPlaintext, testKey)

                // When & Then
                assertThrows(Exception::class.java) {
                    AESGCM.decrypt(encrypted, wrongKey)
                }
            }

            @Test
            @DisplayName("암호화된 데이터에 메타데이터가 포함되어야 한다")
            fun `should include metadata in encrypted data`() {
                // When
                val encrypted = AESGCM.encrypt(testPlaintext, testKey)

                // Then
                assertTrue(encrypted.size > EncryptedMetadata.METADATA_SIZE, "메타데이터가 포함된 크기여야 한다")

                val metadataBytes = encrypted.copyOfRange(0, EncryptedMetadata.METADATA_SIZE)
                val prefix = metadataBytes.copyOfRange(0, 16).toString(Charsets.UTF_8)
                assertTrue(prefix.startsWith("ENCRYPTED::V01::"), "올바른 메타데이터 프리픽스가 있어야 한다")
            }

            @Test
            @DisplayName("메타데이터 추출이 올바르게 동작해야 한다")
            fun `should extract metadata correctly`() {
                // When
                val encrypted = AESGCM.encrypt(testPlaintext, testKey)
                val metadata = AESGCM.extractMetadata(encrypted)

                // Then
                assertEquals("V01", metadata.version, "올바른 버전이어야 한다")
                assertEquals(32, metadata.hash.size, "해시 크기가 32바이트여야 한다")
                assertTrue(metadata.timestamp > 0, "타임스탬프가 설정되어야 한다")
            }

            @Test
            @DisplayName("데이터 위변조 시 복호화에서 예외가 발생해야 한다")
            fun `should throw exception when data is tampered`() {
                // Given
                val encrypted = AESGCM.encrypt(testPlaintext, testKey)
                val tamperedData = encrypted.copyOf()

                // 메타데이터 이후 데이터 변조 (IV 부분)
                val tamperPosition = EncryptedMetadata.METADATA_SIZE + 5
                tamperedData[tamperPosition] = (tamperedData[tamperPosition] + 1).toByte()

                // When & Then - GCM 태그 검증에서 AEADBadTagException 발생
                assertThrows(javax.crypto.AEADBadTagException::class.java) {
                    AESGCM.decrypt(tamperedData, testKey)
                }
            }

            @Test
            @DisplayName("해시 위변조 시 SecurityException이 발생해야 한다")
            fun `should throw SecurityException when hash is tampered`() {
                // Given
                val encrypted = AESGCM.encrypt(testPlaintext, testKey)
                val tamperedData = encrypted.copyOf()

                // 메타데이터 내 해시 부분 변조 (GCM은 통과하지만 해시 검증에서 실패)
                val hashPosition = "ENCRYPTED::V01::".length
                tamperedData[hashPosition] = (tamperedData[hashPosition] + 1).toByte()

                // When & Then - 해시 검증에서 SecurityException 발생
                assertThrows(SecurityException::class.java) {
                    AESGCM.decrypt(tamperedData, testKey)
                }
            }

            @Test
            @DisplayName("잘못된 크기의 키로 암호화 시 예외가 발생해야 한다")
            fun `should throw exception for wrong key size during encryption`() {
                // Given
                val wrongSizeKey = ByteArray(16) // 128비트 키를 256비트 파라미터와 사용
                val params = AESGCM.AesGcmParams(keyBits = 256)

                // When & Then
                assertThrows(IllegalArgumentException::class.java) {
                    AESGCM.encrypt(testPlaintext, wrongSizeKey, params = params)
                }
            }

            @Test
            @DisplayName("잘못된 크기의 키로 복호화 시 예외가 발생해야 한다")
            fun `should throw exception for wrong key size during decryption`() {
                // Given
                val encrypted = AESGCM.encrypt(testPlaintext, testKey)
                val wrongSizeKey = ByteArray(16) // 128비트 키
                val params = AESGCM.AesGcmParams(keyBits = 256)

                // When & Then
                assertThrows(IllegalArgumentException::class.java) {
                    AESGCM.decrypt(encrypted, wrongSizeKey, params = params)
                }
            }

            @Test
            @DisplayName("빈 평문도 올바르게 암복호화되어야 한다")
            fun `should handle empty plaintext correctly`() {
                // Given
                val emptyPlaintext = ByteArray(0)

                // When
                val encrypted = AESGCM.encrypt(emptyPlaintext, testKey)
                val decrypted = AESGCM.decrypt(encrypted, testKey)

                // Then
                assertArrayEquals(emptyPlaintext, decrypted, "빈 평문도 올바르게 처리되어야 한다")
            }

            @Test
            @DisplayName("큰 데이터도 올바르게 암복호화되어야 한다")
            fun `should handle large data correctly`() {
                // Given
                val largeData = ByteArray(10_000) { (it % 256).toByte() }

                // When
                val encrypted = AESGCM.encrypt(largeData, testKey)
                val decrypted = AESGCM.decrypt(encrypted, testKey)

                // Then
                assertArrayEquals(largeData, decrypted, "큰 데이터도 올바르게 처리되어야 한다")
            }
        }

        @Nested
        @DisplayName("매개변수 테스트")
        inner class ParameterTest {

            @Test
            @DisplayName("커스텀 매개변수로 암복호화가 동작해야 한다")
            fun `should work with custom parameters`() {
                // Given
                val key128 = AESGCM.generateAesKey(128)
                val params = AESGCM.AesGcmParams(keyBits = 128, ivBytes = 16, tagBits = 96)

                // When
                val encrypted = AESGCM.encrypt(testPlaintext, key128, params = params)
                val decrypted = AESGCM.decrypt(encrypted, key128, params = params)

                // Then
                assertArrayEquals(testPlaintext, decrypted, "커스텀 매개변수로도 올바르게 동작해야 한다")
            }

            @Test
            @DisplayName("잘못된 크기의 암호문에 대해 예외가 발생해야 한다")
            fun `should throw exception for invalid ciphertext size`() {
                // Given
                val invalidData = ByteArray(10) // 너무 작은 데이터

                // When & Then
                assertThrows(IllegalArgumentException::class.java) {
                    AESGCM.decrypt(invalidData, testKey)
                }
            }
        }

        @Nested
        @DisplayName("보안 테스트")
        inner class SecurityTest {

            @Test
            @DisplayName("동일한 평문을 여러 번 암호화해도 다른 결과가 나와야 한다")
            fun `should produce different ciphertext for same plaintext`() {
                // When
                val encrypted1 = AESGCM.encrypt(testPlaintext, testKey)
                val encrypted2 = AESGCM.encrypt(testPlaintext, testKey)

                // Then
                assertFalse(encrypted1.contentEquals(encrypted2), "동일한 평문이라도 다른 암호문이 생성되어야 한다")

                // 하지만 복호화 결과는 동일해야 함
                val decrypted1 = AESGCM.decrypt(encrypted1, testKey)
                val decrypted2 = AESGCM.decrypt(encrypted2, testKey)
                assertArrayEquals(decrypted1, decrypted2, "복호화 결과는 동일해야 한다")
            }

            @Test
            @DisplayName("메타데이터 해시가 암호화마다 다르게 생성되어야 한다")
            fun `should generate different metadata hash for each encryption`() {
                // When
                val encrypted1 = AESGCM.encrypt(testPlaintext, testKey)
                val encrypted2 = AESGCM.encrypt(testPlaintext, testKey)

                val metadata1 = AESGCM.extractMetadata(encrypted1)
                val metadata2 = AESGCM.extractMetadata(encrypted2)

                // Then
                assertFalse(
                    metadata1.hash.contentEquals(metadata2.hash),
                    "각 암호화마다 다른 해시가 생성되어야 한다 (IV가 다르므로)"
                )
            }
        }
    }
}
