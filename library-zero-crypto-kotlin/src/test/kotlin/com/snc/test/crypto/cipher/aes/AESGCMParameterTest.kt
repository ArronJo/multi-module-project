package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * 매개변수 테스트
 *
 * - 커스텀 매개변수 조합
 * - IV, 태그 크기, Salt 크기 테스트
 * - 해시 반복 횟수 테스트
 * - 매개변수 유효성 검증
 */
@DisplayName("AES-GCM 매개변수 테스트 (보안 강화)")
open class AESGCMParameterTest : BaseJUnit5Test() {

    private val testPlaintext = "매개변수 테스트 데이터".toByteArray()

    @Nested
    @DisplayName("커스텀 매개변수 테스트")
    inner class CustomParameterTest {

        @Test
        @DisplayName("커스텀 128비트 키 매개변수로 암복호화가 동작해야 한다")
        fun `should work with custom 128bit key parameters`() {
            // Given
            val key128 = AESGCM.generateKey(128)
            val params = AESGCM.Params(
                keyBits = 128,
                ivBytes = 12,
                tagBits = 128,
                saltBytes = 16,
                hashIterations = 1000
            )

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, key128, params = params)
            val decrypted = AESGCM.decrypt(encrypted, key128, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "128비트 키 매개변수로 올바르게 동작해야 한다")
        }

        @Test
        @DisplayName("커스텀 192비트 키 매개변수로 암복호화가 동작해야 한다")
        fun `should work with custom 192bit key parameters`() {
            // Given
            val key192 = AESGCM.generateKey(192)
            val params = AESGCM.Params(
                keyBits = 192,
                ivBytes = 12,
                tagBits = 128,
                saltBytes = 20,
                hashIterations = 2000
            )

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, key192, params = params)
            val decrypted = AESGCM.decrypt(encrypted, key192, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "192비트 키 매개변수로 올바르게 동작해야 한다")
        }

        @Test
        @DisplayName("커스텀 256비트 키 매개변수로 암복호화가 동작해야 한다")
        fun `should work with custom 256bit key parameters`() {
            // Given
            val key256 = AESGCM.generateKey(256)
            val params = AESGCM.Params(
                keyBits = 256,
                ivBytes = 16,
                tagBits = 96,
                saltBytes = 32,
                hashIterations = 5000
            )

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, key256, params = params)
            val decrypted = AESGCM.decrypt(encrypted, key256, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "256비트 키 매개변수로 올바르게 동작해야 한다")
        }
    }

    @Nested
    @DisplayName("IV (Initialization Vector) 매개변수 테스트")
    inner class IVParameterTest {

        private val testKey = AESGCM.generateKey(256)

        @Test
        @DisplayName("기본 12바이트 IV가 올바르게 동작해야 한다")
        fun `should work with default 12byte IV`() {
            // Given
            val params = AESGCM.Params(ivBytes = 12)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "12바이트 IV로 올바르게 동작해야 한다")
        }

        @Test
        @DisplayName("16바이트 IV가 올바르게 동작해야 한다")
        fun `should work with 16byte IV`() {
            // Given
            val params = AESGCM.Params(ivBytes = 16)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "16바이트 IV로 올바르게 동작해야 한다")
        }

        @Test
        @DisplayName("다른 IV 크기로 복호화 시 실패해야 한다")
        fun `should fail when decrypting with different IV size`() {
            // Given
            val params12 = AESGCM.Params(ivBytes = 12)
            val params16 = AESGCM.Params(ivBytes = 16)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params12)

            // Then
            assertThrows(Exception::class.java) {
                AESGCM.decrypt(encrypted, testKey, params = params16)
            }
        }
    }

    @Nested
    @DisplayName("태그 크기 매개변수 테스트")
    inner class TagSizeParameterTest {

        private val testKey = AESGCM.generateKey(256)

        @Test
        @DisplayName("96비트 태그가 올바르게 동작해야 한다")
        fun `should work with 96bit tag`() {
            // Given
            val params = AESGCM.Params(tagBits = 96)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "96비트 태그로 올바르게 동작해야 한다")
        }

        @Test
        @DisplayName("128비트 태그가 올바르게 동작해야 한다")
        fun `should work with 128bit tag`() {
            // Given
            val params = AESGCM.Params(tagBits = 128)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "128비트 태그로 올바르게 동작해야 한다")
        }

        @Test
        @DisplayName("다른 태그 크기로 복호화 시 실패해야 한다")
        fun `should fail when decrypting with different tag size`() {
            // Given
            val params96 = AESGCM.Params(tagBits = 96)
            val params128 = AESGCM.Params(tagBits = 128)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params96)

            // Then
            assertThrows(Exception::class.java) {
                AESGCM.decrypt(encrypted, testKey, params = params128)
            }
        }
    }

    @Nested
    @DisplayName("Salt 크기 매개변수 테스트")
    inner class SaltSizeParameterTest {

        private val testKey = AESGCM.generateKey(256)

        @Test
        @DisplayName("커스텀 16바이트 Salt가 올바르게 적용되어야 한다")
        fun `should apply custom 16byte salt correctly`() {
            // Given
            val params = AESGCM.Params(saltBytes = 16)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals(16, metadata.salt.size, "16바이트 Salt가 적용되어야 한다")

            // 복호화도 정상적으로 동작해야 함
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)
            assertArrayEquals(testPlaintext, decrypted)
        }

        @Test
        @DisplayName("커스텀 32바이트 Salt가 올바르게 적용되어야 한다")
        fun `should apply custom 32byte salt correctly`() {
            // Given
            val params = AESGCM.Params(saltBytes = 32)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val metadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertEquals(32, metadata.salt.size, "32바이트 Salt가 적용되어야 한다")

            // 복호화도 정상적으로 동작해야 함
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)
            assertArrayEquals(testPlaintext, decrypted)
        }

        @Test
        @DisplayName("작은 8바이트 Salt도 동작해야 한다")
        fun `should work with small 8byte salt`() {
            // Given
            val params = AESGCM.Params(saltBytes = 8)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val metadata = AESGCM.extractMetadata(encrypted)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertEquals(8, metadata.salt.size, "8바이트 Salt가 적용되어야 한다")
            assertArrayEquals(testPlaintext, decrypted)
        }

        @Test
        @DisplayName("큰 64바이트 Salt도 동작해야 한다")
        fun `should work with large 64byte salt`() {
            // Given
            val params = AESGCM.Params(saltBytes = 64)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val metadata = AESGCM.extractMetadata(encrypted)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertEquals(64, metadata.salt.size, "64바이트 Salt가 적용되어야 한다")
            assertArrayEquals(testPlaintext, decrypted)
        }
    }

    @Nested
    @DisplayName("해시 반복 횟수 매개변수 테스트")
    inner class HashIterationParameterTest {

        private val testKey = AESGCM.generateKey(256)

        @Test
        @DisplayName("커스텀 1000회 해시 반복이 올바르게 적용되어야 한다")
        fun `should apply custom 1000 hash iterations correctly`() {
            // Given
            val params = AESGCM.Params(hashIterations = 1000)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "1000회 반복으로 암복호화가 성공해야 한다")
        }

        @Test
        @DisplayName("커스텀 5000회 해시 반복이 올바르게 적용되어야 한다")
        fun `should apply custom 5000 hash iterations correctly`() {
            // Given
            val params = AESGCM.Params(hashIterations = 5000)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "5000회 반복으로 암복호화가 성공해야 한다")
        }

        @Test
        @DisplayName("다른 반복 횟수로 복호화 시 실패해야 한다")
        fun `should fail when decrypting with different iteration count`() {
            // Given
            val params1000 = AESGCM.Params(hashIterations = 1000)
            val params2000 = AESGCM.Params(hashIterations = 2000)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params1000)

            // Then
            assertThrows(SecurityException::class.java) {
                AESGCM.decrypt(encrypted, testKey, params = params2000)
            }
        }

        @Test
        @DisplayName("높은 반복 횟수도 정상 동작해야 한다")
        fun `should work with high iteration count`() {
            // Given
            val params = AESGCM.Params(hashIterations = 50_000)

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "높은 반복 횟수에서도 정상 동작해야 한다")
        }
    }

    @Nested
    @DisplayName("매개변수 조합 테스트")
    inner class ParameterCombinationTest {

        @Test
        @DisplayName("모든 매개변수가 동일해야 암복호화가 성공한다")
        fun `should succeed only when all parameters match`() {
            // Given
            val key128 = AESGCM.generateKey(128)
            val params = AESGCM.Params(
                keyBits = 128,
                ivBytes = 16,
                tagBits = 96,
                saltBytes = 24,
                hashIterations = 3000
            )

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, key128, params = params)
            val decrypted = AESGCM.decrypt(encrypted, key128, params = params)

            // Then
            assertArrayEquals(testPlaintext, decrypted, "모든 매개변수가 일치할 때 성공해야 한다")
        }

        @Test
        @DisplayName("매개변수 중 하나라도 다르면 실패해야 한다")
        fun `should fail if any parameter is different`() {
            // Given
            val key = AESGCM.generateKey(256)
            val paramsOriginal = AESGCM.Params(
                keyBits = 256,
                ivBytes = 12,
                tagBits = 128,
                saltBytes = 16,
                hashIterations = 1000
            )

            val paramsWithDifferentIV = AESGCM.Params(
                keyBits = 256,
                ivBytes = 16, // 다름
                tagBits = 128,
                saltBytes = 16,
                hashIterations = 1000
            )

            val paramsWithDifferentTag = AESGCM.Params(
                keyBits = 256,
                ivBytes = 12,
                tagBits = 96, // 다름
                saltBytes = 16,
                hashIterations = 1000
            )

            val paramsWithDifferentIterations = AESGCM.Params(
                keyBits = 256,
                ivBytes = 12,
                tagBits = 128,
                saltBytes = 16,
                hashIterations = 2000 // 다름
            )

            // When
            val encrypted = AESGCM.encrypt(testPlaintext, key, params = paramsOriginal)

            // Then
            assertThrows(Exception::class.java) {
                AESGCM.decrypt(encrypted, key, params = paramsWithDifferentIV)
            }

            assertThrows(Exception::class.java) {
                AESGCM.decrypt(encrypted, key, params = paramsWithDifferentTag)
            }

            assertThrows(Exception::class.java) {
                AESGCM.decrypt(encrypted, key, params = paramsWithDifferentIterations)
            }
        }
    }

    @Nested
    @DisplayName("매개변수 유효성 검증 테스트")
    inner class ParameterValidationTest {

        private val testKey = AESGCM.generateKey(256)

        @Test
        @DisplayName("잘못된 키 크기 매개변수로 예외가 발생해야 한다")
        fun `should throw exception for invalid key size parameter`() {
            // Given
            val wrongParams = AESGCM.Params(keyBits = 64) // 지원되지 않는 키 크기

            // When & Then
            assertThrows(Exception::class.java) {
                AESGCM.encrypt(testPlaintext, testKey, params = wrongParams)
            }
        }

        @Test
        @DisplayName("너무 작은 Salt 크기로 예외가 발생해야 한다")
        fun `should throw exception for too small salt size`() {
            // Given
            val params = AESGCM.Params(saltBytes = 0) // 0바이트 Salt

            // When & Then
            assertThrows(Exception::class.java) {
                AESGCM.encrypt(testPlaintext, testKey, params = params)
            }
        }

        @Test
        @DisplayName("너무 작은 반복 횟수로 예외가 발생해야 한다")
        fun `should throw exception for too small iteration count`() {
            // Given
            val params = AESGCM.Params(hashIterations = 0) // 0회 반복

            // When & Then
            assertThrows(Exception::class.java) {
                AESGCM.encrypt(testPlaintext, testKey, params = params)
            }
        }
    }
}
