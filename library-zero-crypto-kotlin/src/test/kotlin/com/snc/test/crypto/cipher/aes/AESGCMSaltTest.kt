package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.cipher.aes.EncryptedMetadata
import com.snc.zero.crypto.cipher.aes.HashGenerator
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("AESGCM Salt 기능 테스트")
open class AESGCMSaltTest {

    private val testKey = AESGCM.generateKey(256)
    private val testPlaintext = "테스트 평문 데이터입니다.".toByteArray()

    @Nested
    @DisplayName("Salt 생성 및 검증")
    inner class SaltGenerationAndValidation {

        @Test
        @DisplayName("기본 Salt 크기는 16바이트여야 한다")
        fun `should generate 16 bytes salt by default`() {
            // Given
            val params = AESGCM.Params()

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = params)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals(16, metadata.salt.size)
            assertEquals(params.saltBytes, metadata.salt.size)
        }

        @Test
        @DisplayName("커스텀 Salt 크기가 정확히 적용되어야 한다")
        fun `should apply custom salt size correctly`() {
            // Given
            val customSaltSize = 32
            val params = AESGCM.Params(saltBytes = customSaltSize)

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = params)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals(customSaltSize, metadata.salt.size)
        }

        @Test
        @DisplayName("여러 번 암호화할 때마다 다른 Salt가 생성되어야 한다")
        fun `should generate different salt for each encryption`() {
            // Given & When
            val encrypted1 = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val encrypted2 = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val encrypted3 = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)

            val salt1 = AESGCM.extractMetadata(encrypted1).salt
            val salt2 = AESGCM.extractMetadata(encrypted2).salt
            val salt3 = AESGCM.extractMetadata(encrypted3).salt

            // Then
            assertFalse(salt1.contentEquals(salt2))
            assertFalse(salt2.contentEquals(salt3))
            assertFalse(salt1.contentEquals(salt3))
        }

        @Test
        @DisplayName("Salt는 0이 아닌 값들을 포함해야 한다")
        fun `should generate non-zero salt values`() {
            // Given & When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val salt = AESGCM.extractMetadata(encrypted).salt

            // Then
            assertTrue(salt.any { it != 0.toByte() }, "Salt should contain non-zero values")
        }
    }

    @Nested
    @DisplayName("Salt 기반 해시 검증")
    inner class SaltBasedHashVerification {

        @Test
        @DisplayName("같은 Salt로 생성된 해시는 검증에 성공해야 한다")
        fun `should verify hash with same salt successfully`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)

            // When & Then
            assertDoesNotThrow {
                AESGCM.decrypt(blob = encrypted, key = testKey)
            }
        }

        @Test
        @DisplayName("다른 Salt로 해시를 조작하면 검증에 실패해야 한다")
        fun `should fail hash verification with different salt`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Salt 조작
            val manipulatedSalt = ByteArray(metadata.salt.size) { 0xFF.toByte() }
            val manipulatedMetadata = metadata.copy(salt = manipulatedSalt)
            val manipulatedEncrypted = replaceMetadata(encrypted, manipulatedMetadata)

            // When & Then
            val exception = assertThrows<SecurityException> {
                AESGCM.decrypt(blob = manipulatedEncrypted, key = testKey)
            }
            assertEquals("데이터 무결성 검증 실패 - 데이터가 변조되었을 수 있습니다", exception.message)
        }

        @Test
        @DisplayName("Salt 일부만 변경되어도 해시 검증에 실패해야 한다")
        fun `should fail hash verification with partially modified salt`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Salt의 첫 번째 바이트만 변경
            val manipulatedSalt = metadata.salt.copyOf()
            manipulatedSalt[0] = (manipulatedSalt[0].toInt() xor 0xFF).toByte()
            val manipulatedMetadata = metadata.copy(salt = manipulatedSalt)
            val manipulatedEncrypted = replaceMetadata(encrypted, manipulatedMetadata)

            // When & Then
            assertThrows<SecurityException> {
                AESGCM.decrypt(blob = manipulatedEncrypted, key = testKey)
            }
        }
    }

    @Nested
    @DisplayName("PBKDF2와 Salt 통합 테스트")
    inner class PBKDF2WithSaltIntegration {

        @Test
        @DisplayName("같은 입력과 Salt로 PBKDF2는 동일한 결과를 생성해야 한다")
        fun `should generate same PBKDF2 result with same input and salt`() {
            // Given
            val data = "test data".toByteArray()
            val salt = "test salt".toByteArray()
            val iterations = 1000

            // When
            val hash1 = HashGenerator.pbkdf2(data, salt, iterations, 32)
            val hash2 = HashGenerator.pbkdf2(data, salt, iterations, 32)

            // Then
            assertArrayEquals(hash1, hash2)
        }

        @Test
        @DisplayName("다른 Salt로 PBKDF2는 다른 결과를 생성해야 한다")
        fun `should generate different PBKDF2 result with different salt`() {
            // Given
            val data = "test data".toByteArray()
            val salt1 = "salt1".toByteArray()
            val salt2 = "salt2".toByteArray()
            val iterations = 1000

            // When
            val hash1 = HashGenerator.pbkdf2(data, salt1, iterations, 32)
            val hash2 = HashGenerator.pbkdf2(data, salt2, iterations, 32)

            // Then
            assertFalse(hash1.contentEquals(hash2))
        }

        @Test
        @DisplayName("PBKDF2 반복 횟수가 0 이하이면 예외가 발생해야 한다")
        fun `should throw exception when PBKDF2 iterations is zero or negative`() {
            // Given
            val data = "test".toByteArray()
            val salt = "salt".toByteArray()

            // When & Then
            assertThrows<IllegalArgumentException> {
                HashGenerator.pbkdf2(data, salt, 0, 32)
            }

            assertThrows<IllegalArgumentException> {
                HashGenerator.pbkdf2(data, salt, -1, 32)
            }
        }
    }

    @Nested
    @DisplayName("Salt 크기 변경 호환성 테스트")
    inner class SaltSizeCompatibility {

        @Test
        @DisplayName("다른 Salt 크기로 암호화된 데이터도 올바르게 복호화되어야 한다")
        fun `should decrypt data encrypted with different salt sizes`() {
            // Given
            val saltSizes = listOf(8, 16, 24, 32, 64)

            saltSizes.forEach { saltSize ->
                // When
                val params = AESGCM.Params(saltBytes = saltSize)
                val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = params)
                val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey, params = params)

                // Then
                assertArrayEquals(testPlaintext, decrypted, "Salt size: $saltSize")
                assertEquals(saltSize, AESGCM.extractMetadata(encrypted).salt.size)
            }
        }

        @Test
        @DisplayName("잘못된 Salt 크기 파라미터로 복호화하면 실패해야 한다")
        fun `should fail decryption with wrong salt size parameter`() {
            // Given
            val encryptParams = AESGCM.Params(saltBytes = 16)
            val decryptParams = AESGCM.Params(saltBytes = 32) // 다른 크기
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, params = encryptParams)

            // When & Then
            assertThrows<Exception> {
                AESGCM.decrypt(blob = encrypted, key = testKey, params = decryptParams)
            }
        }
    }

    @Nested
    @DisplayName("레인보우 테이블 공격 방어 테스트")
    inner class RainbowTableAttackDefense {

        @Test
        @DisplayName("같은 평문도 다른 Salt로 인해 다른 해시를 생성해야 한다")
        fun `should generate different hash for same plaintext with different salt`() {
            // Given
            val plaintext = "동일한 평문".toByteArray()
            val key = AESGCM.generateKey()

            // When
            val encrypted1 = AESGCM.encrypt(plaintext = plaintext, key = key)
            val encrypted2 = AESGCM.encrypt(plaintext = plaintext, key = key)
            val encrypted3 = AESGCM.encrypt(plaintext = plaintext, key = key)

            val hash1 = AESGCM.extractMetadata(encrypted1).hash
            val hash2 = AESGCM.extractMetadata(encrypted2).hash
            val hash3 = AESGCM.extractMetadata(encrypted3).hash

            // Then
            assertFalse(hash1.contentEquals(hash2))
            assertFalse(hash2.contentEquals(hash3))
            assertFalse(hash1.contentEquals(hash3))
        }

        @Test
        @DisplayName("Salt 엔트로피가 충분해야 한다")
        fun `should have sufficient salt entropy`() {
            // Given
            val sampleSize = 100
            val salts = mutableListOf<ByteArray>()

            // When
            repeat(sampleSize) {
                val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
                salts.add(AESGCM.extractMetadata(encrypted).salt)
            }

            // Then
            // 모든 Salt가 서로 달라야 함
            for (i in salts.indices) {
                for (j in i + 1 until salts.size) {
                    assertFalse(
                        salts[i].contentEquals(salts[j]),
                        "Salt $i and $j should be different"
                    )
                }
            }

            // 각 Salt의 엔트로피 검증 (0이 아닌 바이트가 충분해야 함)
            salts.forEach { salt ->
                val nonZeroBytes = salt.count { it != 0.toByte() }
                assertTrue(
                    nonZeroBytes > salt.size / 2,
                    "Salt should have sufficient entropy"
                )
            }
        }
    }

    // Helper function to replace metadata in encrypted blob
    private fun replaceMetadata(encrypted: ByteArray, newMetadata: EncryptedMetadata): ByteArray {
        val originalMetadata = AESGCM.extractMetadata(encrypted)
        val originalMetadataSize = EncryptedMetadata.calculateSize(originalMetadata.salt.size)
        val remainingData = encrypted.copyOfRange(originalMetadataSize, encrypted.size)

        return newMetadata.toByteArray() + remainingData
    }
}
