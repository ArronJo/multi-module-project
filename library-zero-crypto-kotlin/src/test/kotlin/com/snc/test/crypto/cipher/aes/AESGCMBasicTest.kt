package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * 기본 암복호화 테스트
 *
 * - 기본 암복호화 동작
 * - 다양한 입력 데이터 테스트
 * - AAD 테스트
 */
@Suppress("NonAsciiCharacters")
@DisplayName("AES-GCM 기본 암복호화 테스트")
open class AESGCMBasicTest {

    @Nested
    @DisplayName("기본 암복호화 동작")
    inner class BasicEncryptDecrypt {

        private val key: ByteArray = AESGCM.generateKey()
        private val plaintext = "Hello, Kotlin AES-GCM!".toByteArray(Charsets.UTF_8)
        private lateinit var encrypted: ByteArray

        @BeforeEach
        fun init() {
            encrypted = AESGCM.encrypt(plaintext = plaintext, key = key)
        }

        @Test
        fun `암복호화 테스트 1`() {
            val encrypted = AESGCM.encrypt(plaintext = plaintext, key = key)
            println("-".repeat(20))
            println("plaintext=${String(plaintext)}")
            println("encrypted=${String(encrypted)}")

            val decrypted = AESGCM.decrypt(blob = encrypted, key = key)
            println("decrypted=${String(decrypted)}")

            assertArrayEquals(plaintext, decrypted)
        }

        @Test
        fun `암호화 시점과 복호화 시점이 다를 경우 테스트`() {
            Thread.sleep(100)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = key)
            assertArrayEquals(plaintext, decrypted)
        }

        @Test
        fun `timestamp가 올바르게 보존되는지 확인`() {
            // Given
            val beforeEncrypt = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(plaintext = plaintext, key = key)
            Thread.sleep(100)
            val beforeDecrypt = System.currentTimeMillis()

            // When
            val extractedMetadata = AESGCM.extractMetadata(encrypted)

            // Then
            assertTrue(extractedMetadata.timestamp >= beforeEncrypt)
            assertTrue(extractedMetadata.timestamp <= beforeDecrypt)
            println("암호화 시점: $beforeEncrypt")
            println("저장된 timestamp: ${extractedMetadata.timestamp}")
            println("복호화 시점: $beforeDecrypt")
        }

        @Test
        fun `기본 암복호화가 원본 평문을 반환해야 한다`() {
            val decrypted = AESGCM.decrypt(blob = encrypted, key = key)
            assertArrayEquals(plaintext, decrypted)
        }

        @Test
        fun `잘못된 키로 복호화 시 예외가 발생해야 한다`() {
            val wrongKey = AESGCM.generateKey(256)

            assertThrows(Exception::class.java) {
                AESGCM.decrypt(blob = encrypted, key = wrongKey)
            }
        }

        @Test
        fun `암호화된 데이터는 IV와 암호문을 포함해야 한다`() {
            assertTrue(encrypted.size > 12, "암호화된 데이터는 최소한 IV보다 커야 한다")
        }
    }

    @Nested
    @DisplayName("다양한 입력 데이터 테스트")
    inner class VariousInputData {

        private val testKey = AESGCM.generateKey(256)

        @Test
        fun `빈 평문도 올바르게 암복호화되어야 한다`() {
            // Given
            val emptyPlaintext = ByteArray(0)

            // When
            val encrypted = AESGCM.encrypt(plaintext = emptyPlaintext, key = testKey)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey)

            // Then
            assertArrayEquals(emptyPlaintext, decrypted)
        }

        @Test
        fun `한글이 포함된 텍스트를 올바르게 처리해야 한다`() {
            // Given
            val koreanText = "Hello, World! 안녕하세요!".toByteArray(Charsets.UTF_8)

            // When
            val encrypted = AESGCM.encrypt(plaintext = koreanText, key = testKey)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey)

            // Then
            assertArrayEquals(koreanText, decrypted)
        }

        @Test
        fun `큰 데이터도 올바르게 암복호화되어야 한다`() {
            // Given
            val largeData = ByteArray(10_000) { (it % 256).toByte() }

            // When
            val encrypted = AESGCM.encrypt(plaintext = largeData, key = testKey)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey)

            // Then
            assertArrayEquals(largeData, decrypted)
        }

        @Test
        fun `특수문자가 포함된 데이터를 처리해야 한다`() {
            // Given
            val specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?".toByteArray(Charsets.UTF_8)

            // When
            val encrypted = AESGCM.encrypt(plaintext = specialChars, key = testKey)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey)

            // Then
            assertArrayEquals(specialChars, decrypted)
        }
    }

    @Nested
    @DisplayName("AAD (Additional Authenticated Data) 테스트")
    inner class AADTest {

        private val testKey = AESGCM.generateKey(256)
        private val testPlaintext = "AAD 테스트 데이터".toByteArray(Charsets.UTF_8)

        @Test
        fun `AAD를 사용한 암복호화가 올바르게 동작해야 한다`() {
            // Given
            val aad = "additional authenticated data".toByteArray()

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, aad = aad)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey, aad = aad)

            // Then
            assertArrayEquals(testPlaintext, decrypted)
        }

        @Test
        fun `다른 AAD로 복호화 시 예외가 발생해야 한다`() {
            // Given
            val aad1 = "correct aad".toByteArray()
            val aad2 = "wrong aad".toByteArray()
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, aad = aad1)

            // When & Then
            assertThrows(Exception::class.java) {
                AESGCM.decrypt(blob = encrypted, key = testKey, aad = aad2)
            }
        }

        @Test
        fun `AAD 없이 암호화했는데 AAD로 복호화 시 예외가 발생해야 한다`() {
            // Given
            val aad = "unexpected aad".toByteArray()
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)

            // When & Then
            assertThrows(Exception::class.java) {
                AESGCM.decrypt(blob = encrypted, key = testKey, aad = aad)
            }
        }

        @Test
        fun `한글 AAD도 올바르게 처리되어야 한다`() {
            // Given
            val koreanAAD = "한글 추가 인증 데이터".toByteArray(Charsets.UTF_8)

            // When
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey, aad = koreanAAD)
            val decrypted = AESGCM.decrypt(blob = encrypted, key = testKey, aad = koreanAAD)

            // Then
            assertArrayEquals(testPlaintext, decrypted)
        }
    }

    @Nested
    @DisplayName("오류 상황 테스트")
    inner class ErrorCaseTest {

        private val testKey = AESGCM.generateKey(256)
        private val testPlaintext = "오류 테스트 데이터".toByteArray()

        @Test
        fun `잘못된 크기의 암호문에 대해 예외가 발생해야 한다`() {
            // Given
            val invalidData = ByteArray(10) // 너무 작은 데이터

            // When & Then
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.decrypt(blob = invalidData, key = testKey)
            }
        }

        @Test
        fun `null 키로 암호화 시 예외가 발생해야 한다`() {
            // When & Then
            assertThrows(Exception::class.java) {
                AESGCM.encrypt(plaintext = testPlaintext, key = ByteArray(0))
            }
        }

        @Test
        fun `잘못된 크기의 키로 암호화 시 예외가 발생해야 한다`() {
            // Given
            val wrongSizeKey = ByteArray(16) // 128비트 키를 256비트 파라미터와 사용
            val params = AESGCM.Params(keyBits = 256)

            // When & Then
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.encrypt(plaintext = testPlaintext, key = wrongSizeKey, params = params)
            }
        }

        @Test
        fun `잘못된 크기의 키로 복호화 시 예외가 발생해야 한다`() {
            // Given
            val encrypted = AESGCM.encrypt(plaintext = testPlaintext, key = testKey)
            val wrongSizeKey = ByteArray(16) // 128비트 키
            val params = AESGCM.Params(keyBits = 256)

            // When & Then
            assertThrows(IllegalArgumentException::class.java) {
                AESGCM.decrypt(blob = encrypted, key = wrongSizeKey, params = params)
            }
        }
    }
}
