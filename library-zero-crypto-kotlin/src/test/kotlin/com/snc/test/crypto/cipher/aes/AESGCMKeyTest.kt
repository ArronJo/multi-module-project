package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.cipher.aes.AESGCM.Params
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * 키 생성 테스트
 *
 * - 지원되는 키 길이 (128, 192, 256비트)
 * - 지원되지 않는 키 길이 예외 처리
 * - 키 무작위성 및 품질 테스트
 * - 키 호환성 테스트
 */
@DisplayName("AES-GCM 키 생성 테스트")
open class AESGCMKeyTest {

    @Nested
    @DisplayName("지원되는 키 길이 테스트")
    inner class SupportedKeyLength {

        @Test
        @DisplayName("256비트 키가 올바르게 생성되어야 한다")
        fun `should generate 256 bit key correctly`() {
            // When
            val key = AESGCM.generateKey(256)

            // Then
            assertEquals(32, key.size, "256비트는 32바이트여야 한다")
        }

        @Test
        @DisplayName("192비트 키가 올바르게 생성되어야 한다")
        fun `should generate 192 bit key correctly`() {
            // When
            val key = AESGCM.generateKey(192)

            // Then
            assertEquals(24, key.size, "192비트는 24바이트여야 한다")
        }

        @Test
        @DisplayName("128비트 키가 올바르게 생성되어야 한다")
        fun `should generate 128 bit key correctly`() {
            // When
            val key = AESGCM.generateKey(128)

            // Then
            assertEquals(16, key.size, "128비트는 16바이트여야 한다")
        }

        @Test
        @DisplayName("기본 키 생성 시 256비트여야 한다")
        fun `should generate 256 bit key by default`() {
            // When
            val key = AESGCM.generateKey()

            // Then
            assertEquals(32, key.size, "기본 키는 256비트(32바이트)여야 한다")
        }
    }

    @Nested
    @DisplayName("지원되지 않는 키 길이 테스트")
    inner class UnsupportedKeyLength {

        @Test
        @DisplayName("64비트 키 생성 시 예외가 발생해야 한다")
        fun `should throw exception for 64 bit key`() {
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.generateKey(64)
            }
        }

        @Test
        @DisplayName("512비트 키 생성 시 예외가 발생해야 한다")
        fun `should throw exception for 512 bit key`() {
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.generateKey(512)
            }
        }

        @Test
        @DisplayName("96비트 키 생성 시 예외가 발생해야 한다")
        fun `should throw exception for 96 bit key`() {
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.generateKey(96)
            }
        }

        @Test
        @DisplayName("160비트 키 생성 시 예외가 발생해야 한다")
        fun `should throw exception for 160 bit key`() {
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.generateKey(160)
            }
        }

        @Test
        @DisplayName("0비트 키 생성 시 예외가 발생해야 한다")
        fun `should throw exception for 0 bit key`() {
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.generateKey(0)
            }
        }

        @Test
        @DisplayName("음수 키 길이 시 예외가 발생해야 한다")
        fun `should throw exception for negative key length`() {
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.generateKey(-128)
            }
        }
    }

    @Nested
    @DisplayName("키 무작위성 테스트")
    inner class KeyRandomness {

        @Test
        @DisplayName("매번 다른 256비트 키가 생성되어야 한다")
        fun `should generate different 256 bit keys each time`() {
            // When
            val key1 = AESGCM.generateKey(256)
            val key2 = AESGCM.generateKey(256)

            // Then
            assertFalse(key1.contentEquals(key2), "매번 다른 256비트 키가 생성되어야 한다")
        }

        @Test
        @DisplayName("매번 다른 192비트 키가 생성되어야 한다")
        fun `should generate different 192 bit keys each time`() {
            // When
            val key1 = AESGCM.generateKey(192)
            val key2 = AESGCM.generateKey(192)

            // Then
            assertFalse(key1.contentEquals(key2), "매번 다른 192비트 키가 생성되어야 한다")
        }

        @Test
        @DisplayName("매번 다른 128비트 키가 생성되어야 한다")
        fun `should generate different 128 bit keys each time`() {
            // When
            val key1 = AESGCM.generateKey(128)
            val key2 = AESGCM.generateKey(128)

            // Then
            assertFalse(key1.contentEquals(key2), "매번 다른 128비트 키가 생성되어야 한다")
        }

        @Test
        @DisplayName("연속으로 생성된 키들이 모두 달라야 한다")
        fun `should generate different keys in sequence`() {
            // Given
            val keys = mutableListOf<ByteArray>()

            // When - 10개의 키 생성
            repeat(10) {
                keys.add(AESGCM.generateKey(256))
            }

            // Then - 모든 키가 서로 달라야 함
            for (i in keys.indices) {
                for (j in i + 1 until keys.size) {
                    assertFalse(
                        keys[i].contentEquals(keys[j]),
                        "키 $i 와 키 $j 가 동일하면 안 됩니다"
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("키 품질 테스트")
    inner class KeyQuality {

        @Test
        @DisplayName("생성된 키에 0이 아닌 바이트가 있어야 한다")
        fun `should contain non zero bytes in generated key`() {
            // When
            val key = AESGCM.generateKey(256)

            // Then
            val hasNonZero = key.any { it != 0.toByte() }
            assertTrue(hasNonZero, "생성된 키에 0이 아닌 바이트가 있어야 한다")
        }

        @Test
        @DisplayName("모든 바이트가 같지 않아야 한다")
        fun `should not have all same bytes`() {
            // When
            val key = AESGCM.generateKey(256)

            // Then
            val firstByte = key[0]
            val allSame = key.all { it == firstByte }
            assertFalse(allSame, "모든 바이트가 같으면 안 됩니다")
        }

        @Test
        @DisplayName("키의 엔트로피가 충분해야 한다")
        fun `should have sufficient entropy`() {
            // When
            val key = AESGCM.generateKey(256)
            val uniqueBytes = key.toSet()

            // Then - 32바이트 키에서 최소 절반 이상의 고유한 바이트값을 가져야 함
            assertTrue(
                uniqueBytes.size >= 16,
                "키의 엔트로피가 부족합니다. 고유한 바이트 수: ${uniqueBytes.size}"
            )
        }
    }

    @Nested
    @DisplayName("키 호환성 테스트")
    inner class KeyCompatibility {

        @Test
        @DisplayName("생성된 256비트 키로 암복호화가 가능해야 한다")
        fun `should be able to encrypt and decrypt with generated 256 bit key`() {
            // Given
            val key = AESGCM.generateKey(256)
            val plaintext = "키 호환성 테스트".toByteArray(Charsets.UTF_8)

            // When
            val encrypted = AESGCM.encrypt(plaintext = plaintext, key = key)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = key)

            // Then
            assertArrayEquals(plaintext, decrypted)
        }

        @Test
        @DisplayName("생성된 192비트 키로 암복호화가 가능해야 한다")
        fun `should be able to encrypt and decrypt with generated 192 bit key`() {
            // Given
            val key = AESGCM.generateKey(192)
            val plaintext = "192비트 키 테스트".toByteArray(Charsets.UTF_8)
            val params = Params(keyBits = 192)

            // When
            val encrypted = AESGCM.encrypt(plaintext = plaintext, key = key, params = params)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = key, params = params)

            // Then
            assertArrayEquals(plaintext, decrypted)
        }

        @Test
        @DisplayName("생성된 128비트 키로 암복호화가 가능해야 한다")
        fun `should be able to encrypt and decrypt with generated 128 bit key`() {
            // Given
            val key = AESGCM.generateKey(128)
            val plaintext = "128비트 키 테스트".toByteArray(Charsets.UTF_8)
            val params = Params(keyBits = 128)
            // When
            val encrypted = AESGCM.encrypt(plaintext = plaintext, key = key, params = params)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = key, params = params)

            // Then
            assertArrayEquals(plaintext, decrypted)
        }
    }
}
