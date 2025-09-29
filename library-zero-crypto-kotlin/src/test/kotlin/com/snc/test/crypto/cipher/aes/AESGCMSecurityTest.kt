package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.cipher.aes.EncryptedMetadata
import com.snc.zero.crypto.cipher.aes.HashGenerator
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import javax.crypto.AEADBadTagException

/**
 * 보안 테스트
 *
 * - 암호문 무작위성
 * - 데이터 무결성 검증
 * - PBKDF2 보안 강화
 * - 레인보우 테이블 공격 저항성
 * - 타임스탬프 보안
 */
@DisplayName("AES-GCM 보안 테스트 (강화된 보안)")
open class AESGCMSecurityTest {

    private val testKey = AESGCM.generateKey(256)
    private val testPlaintext = "보안 테스트 데이터".toByteArray()

    @Nested
    @DisplayName("암호문 무작위성 테스트")
    inner class CiphertextRandomness {

        @Test
        @DisplayName("동일한 평문을 여러 번 암호화해도 다른 결과가 나와야 한다")
        fun `should produce different ciphertext for same plaintext`() {
            // When
            val encrypted1 = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val encrypted2 = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)

            // Then
            assertFalse(encrypted1.contentEquals(encrypted2), "동일한 평문이라도 다른 암호문이 생성되어야 한다")

            // 하지만 복호화 결과는 동일해야 함
            val decrypted1 = AESGCM.decrypt(blob = encrypted1, key = testKey)
            val decrypted2 = AESGCM.decrypt(blob = encrypted2, key = testKey)
            assertArrayEquals(decrypted1, decrypted2, "복호화 결과는 동일해야 한다")
        }

        @Test
        @DisplayName("메타데이터 해시가 암호화마다 다르게 생성되어야 한다 (Salt 포함)")
        fun `should generate different metadata hash for each encryption with salt`() {
            // When
            val encrypted1 = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val encrypted2 = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)

            val metadata1 = AESGCM.extractMetadata(encrypted1)
            val metadata2 = AESGCM.extractMetadata(encrypted2)

            // Then
            assertFalse(
                metadata1.hash.contentEquals(metadata2.hash),
                "각 암호화마다 다른 해시가 생성되어야 한다 (IV와 Salt가 다르므로)"
            )
            assertFalse(
                metadata1.salt.contentEquals(metadata2.salt),
                "각 암호화마다 다른 Salt가 생성되어야 한다"
            )
        }

        @Test
        @DisplayName("Salt 없이는 동일한 해시를 생성할 수 없어야 한다")
        fun `should not generate same hash without salt randomness`() {
            // Given - 동일한 입력 조건
            val plaintext = "consistent test data".toByteArray()
            val fixedIv = ByteArray(12) { 0x42.toByte() }
            val fixedKey = ByteArray(32) { 0x24.toByte() }

            // 다른 Salt 사용
            val salt1 = ByteArray(16) { 0x11.toByte() }
            val salt2 = ByteArray(16) { 0x22.toByte() }
            val iterations = 1000
            val fixedTimestamp = System.currentTimeMillis()

            // When
            val hash1 = HashGenerator.createSecureHash(plaintext, fixedIv, fixedKey, salt1, iterations, fixedTimestamp)
            val hash2 = HashGenerator.createSecureHash(plaintext, fixedIv, fixedKey, salt2, iterations, fixedTimestamp)

            // Then
            assertFalse(hash1.contentEquals(hash2), "다른 Salt로 인해 다른 해시가 생성되어야 한다")
        }
    }

    @Nested
    @DisplayName("데이터 무결성 테스트")
    inner class DataIntegrity {

        @Test
        @DisplayName("데이터 위변조 시 복호화에서 예외가 발생해야 한다")
        fun `should throw exception when data is tampered`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val tamperedData = encrypted.copyOf()

            // 메타데이터 크기 계산 (기본 16바이트 Salt)
            val metadataSize = EncryptedMetadata.calculateSize(16)
            val tamperPosition = metadataSize + 5 // IV 부분 변조

            tamperedData[tamperPosition] = (tamperedData[tamperPosition] + 1).toByte()

            // When & Then - GCM 태그 검증에서 AEADBadTagException 발생
            assertThrows(AEADBadTagException::class.java) {
                AESGCM.decrypt(blob = tamperedData, key = testKey)
            }
        }

        @Test
        @DisplayName("메타데이터 위변조 시 해시 검증에서 SecurityException이 발생해야 한다")
        fun `should throw SecurityException when metadata is tampered`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val tamperedData = encrypted.copyOf()

            // 메타데이터 내의 해시 부분 변조
            val hashStartPos = "ENW::AFJ02::".toByteArray().size
            tamperedData[hashStartPos + 10] = (tamperedData[hashStartPos + 10] + 1).toByte()

            // When & Then - 해시 검증에서 SecurityException 발생
            val exception = assertThrows(SecurityException::class.java) {
                AESGCM.decrypt(blob = tamperedData, key = testKey)
            }

            assertTrue(
                exception.message?.contains("무결성 검증 실패") == true,
                "적절한 오류 메시지가 포함되어야 한다"
            )
        }

        @Test
        @DisplayName("해시 위변조 시 SecurityException이 발생해야 한다")
        fun `should throw SecurityException when hash is tampered`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val tamperedData = encrypted.copyOf()

            // 메타데이터 내 해시 부분 변조 (GCM은 통과하지만 해시 검증에서 실패)
            val hashPosition = "ENW::AFJ02::".length
            tamperedData[hashPosition] = (tamperedData[hashPosition] + 1).toByte()

            // When & Then - 해시 검증에서 SecurityException 발생
            assertThrows(SecurityException::class.java) {
                AESGCM.decrypt(blob = tamperedData, key = testKey)
            }
        }

        @Test
        @DisplayName("Salt 위변조 시에도 해시 검증이 실패해야 한다")
        fun `should fail hash verification when salt is tampered`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = AESGCM.Params(saltBytes = 16))
            val tamperedData = encrypted.copyOf()

            // 메타데이터 파싱 (정확한 offset 계산용)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Salt 시작 위치 구하기
            val saltStart = "ENW::".toByteArray().size + metadata.version.toByteArray().size +
                "::".toByteArray().size + 32 + 2 // prefix+version+delimiter+hash+saltLength(2바이트)

            // Salt 바이트 중 하나 변조
            tamperedData[saltStart] = (tamperedData[saltStart] + 1).toByte()

            // When & Then - 해시 검증에서 SecurityException 발생
            assertThrows(SecurityException::class.java) {
                AESGCM.decrypt(blob = tamperedData, key = testKey)
            }
        }
    }

    @Nested
    @DisplayName("PBKDF2 보안 강화 테스트")
    inner class PBKDF2Security {

        @Test
        @DisplayName("PBKDF2 반복 횟수 증가로 보안이 강화되어야 한다")
        fun `should enhance security with increased pbkdf2 iterations`() {
            // Given
            val plaintext = "sensitive data".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt = ByteArray(16) { (it * 3).toByte() }
            val fixedTimestamp = System.currentTimeMillis()

            // When - 다른 반복 횟수로 해시 생성
            val hashLowIter = HashGenerator.createSecureHash(plaintext, iv, key, salt, 100, fixedTimestamp)
            val hashHighIter = HashGenerator.createSecureHash(plaintext, iv, key, salt, 10000, fixedTimestamp)

            // Then
            assertFalse(hashLowIter.contentEquals(hashHighIter), "반복 횟수가 다르면 다른 해시가 생성되어야 한다")
            assertEquals(32, hashLowIter.size, "해시 크기는 동일해야 한다")
            assertEquals(32, hashHighIter.size, "해시 크기는 동일해야 한다")
        }

        @Test
        @DisplayName("잘못된 반복 횟수로 검증 시 실패해야 한다")
        fun `should fail verification with wrong iteration count`() {
            // Given
            val params1 = AESGCM.Params(hashIterations = 1000)
            val params2 = AESGCM.Params(hashIterations = 2000)

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = params1)

            // Then - 다른 반복 횟수로 복호화 시도 시 해시 검증 실패
            assertThrows(SecurityException::class.java) {
                AESGCM.decrypt(blob = encrypted, key = testKey, params = params2)
            }
        }
    }

    @Nested
    @DisplayName("레인보우 테이블 공격 저항성 테스트")
    inner class RainbowTableResistance {

        @Test
        @DisplayName("레인보우 테이블 공격 저항성 테스트")
        fun `should resist rainbow table attacks`() {
            // Given - 동일한 평문에 대해 여러 번 암호화
            val commonPlaintext = "password123".toByteArray()
            val encryptionResults = mutableListOf<ByteArray>()

            // When - 10번 암호화
            repeat(10) {
                val encrypted = AESGCM.encrypt(plaintext = commonPlaintext, key = testKey)
                encryptionResults.add(encrypted)
            }

            // Then - 모든 결과가 달라야 함 (Salt로 인해)
            for (i in 0 until encryptionResults.size - 1) {
                for (j in i + 1 until encryptionResults.size) {
                    assertFalse(
                        encryptionResults[i].contentEquals(encryptionResults[j]),
                        "동일한 평문이라도 모든 암호화 결과가 달라야 한다"
                    )
                }
            }

            // 모든 결과가 정상적으로 복호화되어야 함
            encryptionResults.forEach { encrypted ->
                val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey)
                assertArrayEquals(commonPlaintext, decrypted, "모든 암호화 결과가 정상 복호화되어야 한다")
            }
        }

        @Test
        @DisplayName("동일 평문의 다른 암호화 결과가 서로 다른 Salt를 가져야 한다")
        fun `should have different salt for same plaintext encryptions`() {
            // Given
            val plaintext = "repeated plaintext".toByteArray()

            // When
            val encrypted1 = AESGCM.encrypt(plaintext = plaintext, key = testKey)
            val encrypted2 = AESGCM.encrypt(plaintext = plaintext, key = testKey)

            val metadata1 = AESGCM.extractMetadata(encrypted1)
            val metadata2 = AESGCM.extractMetadata(encrypted2)

            // Then
            assertFalse(
                metadata1.salt.contentEquals(metadata2.salt),
                "동일 평문의 다른 암호화는 서로 다른 Salt를 가져야 한다"
            )
        }
    }

    @Nested
    @DisplayName("타임스탬프 보안 테스트")
    inner class TimestampSecurity {

        @Test
        @DisplayName("타임스탬프 기반 해시 검증이 동작해야 한다")
        fun `should verify hash with timestamp consideration`() {
            // Given
            val plaintext = "timestamp test".toByteArray()
            val iv = ByteArray(12) { it.toByte() }
            val key = ByteArray(32) { (it * 2).toByte() }
            val salt = ByteArray(16) { (it * 3).toByte() }
            val iterations = 1000

            // When - createSecureHash는 내부적으로 현재 타임스탬프를 사용
            val timestamp = System.currentTimeMillis()
            val hash1 = HashGenerator.createSecureHash(plaintext, iv, key, salt, iterations, timestamp)

            Thread.sleep(10) // 시간 차이 생성

            val timestamp2 = System.currentTimeMillis()
            val hash2 = HashGenerator.createSecureHash(plaintext, iv, key, salt, iterations, timestamp2)

            // Then - 타임스탬프 차이로 인해 다른 해시 생성
            assertFalse(hash1.contentEquals(hash2), "타임스탬프 차이로 인해 다른 해시가 생성되어야 한다")
        }

        @Test
        @DisplayName("타임스탬프가 올바른 시간 범위 내에 있어야 한다")
        fun `should have timestamp within reasonable time range`() {
            // Given
            val currentTime = System.currentTimeMillis()

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            val timeDiff = Math.abs(metadata.timestamp - currentTime)
            assertTrue(timeDiff < 5000, "타임스탬프는 현재 시간과 5초 이내 차이여야 한다")
        }
    }

    @Nested
    @DisplayName("메타데이터 버전 호환성 테스트")
    inner class VersionCompatibility {

        @Test
        @DisplayName("메타데이터 버전 호환성 테스트")
        fun `should handle metadata version compatibility`() {
            // Given - AFJ02 버전으로 암호화
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals("AFJ02", metadata.version, "현재 버전은 AFJ02여야 한다")
            assertTrue(metadata.salt.isNotEmpty(), "AFJ02는 Salt를 포함해야 한다")

            // 정상 복호화 확인
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey)
            assertArrayEquals(testPlaintext, decrypted, "AFJ02 버전으로 정상 복호화되어야 한다")
        }

        @Test
        @DisplayName("AFJ02 메타데이터가 필수 구성 요소를 포함해야 한다")
        fun `should include required components in AFJ02 metadata`() {
            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals("AFJ02", metadata.version, "AFJ02 버전이어야 한다")
            assertEquals(32, metadata.hash.size, "32바이트 해시를 포함해야 한다")
            assertTrue(metadata.salt.isNotEmpty(), "Salt를 포함해야 한다")
            assertTrue(metadata.timestamp > 0, "유효한 타임스탬프를 포함해야 한다")
        }
    }

    @Nested
    @DisplayName("고급 보안 시나리오 테스트")
    inner class AdvancedSecurityScenarios {

        @Test
        @DisplayName("여러 키로 암호화된 데이터는 서로 복호화할 수 없어야 한다")
        fun `should not decrypt data encrypted with different keys`() {
            // Given
            val key1 = AESGCM.generateKey(256)
            val key2 = AESGCM.generateKey(256)
            val key3 = AESGCM.generateKey(256)

            // When
            val encrypted1 = AESGCM.encrypt(plaintext = testPlaintext, key = key1)
            val encrypted2 = AESGCM.encrypt(plaintext = testPlaintext, key = key2)
            val encrypted3 = AESGCM.encrypt(plaintext = testPlaintext, key = key3)

            // Then - 각 키로만 해당 암호문을 복호화할 수 있어야 함
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encrypted1, key = key1))
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encrypted2, key = key2))
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encrypted3, key = key3))

            // 다른 키로는 복호화할 수 없어야 함
            assertThrows(Exception::class.java) { AESGCM.decrypt(blob = encrypted1, key = key2) }
            assertThrows(Exception::class.java) { AESGCM.decrypt(blob = encrypted1, key = key3) }
            assertThrows(Exception::class.java) { AESGCM.decrypt(blob = encrypted2, key = key1) }
            assertThrows(Exception::class.java) { AESGCM.decrypt(blob = encrypted2, key = key3) }
            assertThrows(Exception::class.java) { AESGCM.decrypt(blob = encrypted3, key = key1) }
            assertThrows(Exception::class.java) { AESGCM.decrypt(blob = encrypted3, key = key2) }
        }

        @Test
        @DisplayName("키 길이별 보안 수준 테스트")
        fun `should maintain security across different key lengths`() {
            // Given
            val key128 = AESGCM.generateKey(128)
            val key192 = AESGCM.generateKey(192)
            val key256 = AESGCM.generateKey(256)

            val param128 = AESGCM.Params(keyBits = 128)
            val param192 = AESGCM.Params(keyBits = 192)
            val param256 = AESGCM.Params(keyBits = 256)

            // When - 각 키 길이로 암호화
            val encrypted128 = AESGCM.encrypt(plaintext = testPlaintext, key = key128, params = param128)
            val encrypted192 = AESGCM.encrypt(plaintext = testPlaintext, key = key192, params = param192)
            val encrypted256 = AESGCM.encrypt(plaintext = testPlaintext, key = key256, params = param256)

            // Then - 모든 키 길이에서 정상적으로 동작해야 함
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encrypted128, key = key128, params = param128))
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encrypted192, key = key192, params = param192))
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encrypted256, key = key256, params = param256))

            // 메타데이터도 올바르게 생성되어야 함
            val metadata128 = AESGCM.extractMetadata(encrypted128)
            val metadata192 = AESGCM.extractMetadata(encrypted192)
            val metadata256 = AESGCM.extractMetadata(encrypted256)

            assertEquals("AFJ02", metadata128.version)
            assertEquals("AFJ02", metadata192.version)
            assertEquals("AFJ02", metadata256.version)
        }

        @Test
        @DisplayName("극단적인 Salt 크기에서의 보안성 테스트")
        fun `should maintain security with extreme salt sizes`() {
            // Given
            val smallSaltParams = AESGCM.Params(saltBytes = 8) // 최소 크기
            val largeSaltParams = AESGCM.Params(saltBytes = 64) // 큰 크기

            // When
            val encryptedSmall = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = smallSaltParams)
            val encryptedLarge = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = largeSaltParams)

            // Then
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encryptedSmall, key = testKey, params = smallSaltParams))
            assertArrayEquals(testPlaintext, AESGCM.decrypt(blob = encryptedLarge, key = testKey, params = largeSaltParams))

            // 메타데이터에서 Salt 크기 확인
            val metadataSmall = AESGCM.extractMetadata(encryptedSmall)
            val metadataLarge = AESGCM.extractMetadata(encryptedLarge)

            assertEquals(8, metadataSmall.salt.size)
            assertEquals(64, metadataLarge.salt.size)
        }

        @Test
        @DisplayName("높은 반복 횟수에서의 보안성 테스트")
        fun `should maintain security with high iteration counts`() {
            // Given
            val highIterParams = AESGCM.Params(hashIterations = 100_000)

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = highIterParams)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey, params = highIterParams)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "높은 반복 횟수에서도 정상 동작해야 한다")

            // 메타데이터 검증
            val metadata = AESGCM.extractMetadata(encrypted)
            assertEquals("AFJ02", metadata.version)
            assertTrue(metadata.hash.isNotEmpty())
            assertTrue(metadata.salt.isNotEmpty())
        }
    }
}
