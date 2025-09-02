package com.snc.test.crypto.cipher.rsa

import com.snc.zero.crypto.cipher.rsa.RSASign
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SignatureException

@Suppress("NonAsciiCharacters")
@DisplayName("RSASign 테스트")
class RSASignTest {

    @Nested
    @DisplayName("키 페어 생성 테스트")
    inner class GenerateRsaKeyPairTest {

        @Test
        @DisplayName("기본 2048비트 키 페어 생성")
        fun `기본 2048비트 키 페어가 정상적으로 생성된다`() {
            // When
            val keyPair = RSASign.generateRsaKeyPair()

            // Then
            assertNotNull(keyPair)
            assertNotNull(keyPair.private)
            assertNotNull(keyPair.public)
            assertEquals("RSA", keyPair.private.algorithm)
            assertEquals("RSA", keyPair.public.algorithm)
        }

        @Test
        @DisplayName("커스텀 비트 키 페어 생성")
        fun `지정된 비트 수로 키 페어가 생성된다`() {
            // Given
            val bits = 1024

            // When
            val keyPair = RSASign.generateRsaKeyPair(bits)

            // Then
            assertNotNull(keyPair)
            assertNotNull(keyPair.private)
            assertNotNull(keyPair.public)
            assertEquals("RSA", keyPair.private.algorithm)
            assertEquals("RSA", keyPair.public.algorithm)
        }

        @Test
        @DisplayName("4096비트 키 페어 생성")
        fun `4096비트 키 페어가 정상적으로 생성된다`() {
            // Given
            val bits = 4096

            // When
            val keyPair = RSASign.generateRsaKeyPair(bits)

            // Then
            assertNotNull(keyPair)
            assertNotNull(keyPair.private)
            assertNotNull(keyPair.public)
        }

        @Test
        @DisplayName("매번 다른 키 페어 생성")
        fun `매번 호출할 때마다 다른 키 페어가 생성된다`() {
            // When
            val keyPair1 = RSASign.generateRsaKeyPair()
            val keyPair2 = RSASign.generateRsaKeyPair()

            // Then
            assertFalse(keyPair1.private.encoded.contentEquals(keyPair2.private.encoded))
            assertFalse(keyPair1.public.encoded.contentEquals(keyPair2.public.encoded))
        }
    }

    @Nested
    @DisplayName("RSA 서명 테스트")
    inner class RsaSignTest {

        private val testData = "Hello, World!".toByteArray()
        private val keyPair: KeyPair = RSASign.generateRsaKeyPair()

        @Test
        @DisplayName("정상적인 데이터 서명")
        fun `정상적인 데이터에 대해 서명이 생성된다`() {
            // When
            val signature = RSASign.rsaSign(keyPair.private, testData)

            // Then
            assertNotNull(signature)
            assertTrue(signature.isNotEmpty())
        }

        @Test
        @DisplayName("빈 데이터 서명")
        fun `빈 데이터에 대해서도 서명이 생성된다`() {
            // Given
            val emptyData = ByteArray(0)

            // When
            val signature = RSASign.rsaSign(keyPair.private, emptyData)

            // Then
            assertNotNull(signature)
            assertTrue(signature.isNotEmpty())
        }

        @Test
        @DisplayName("동일한 데이터의 서명은 일관성 있음")
        fun `동일한 데이터와 키로 서명하면 일관된 서명이 생성된다`() {
            // When
            val signature1 = RSASign.rsaSign(keyPair.private, testData)
            val signature2 = RSASign.rsaSign(keyPair.private, testData)

            // Then
            assertArrayEquals(signature1, signature2)
        }

        @Test
        @DisplayName("동일한 데이터의 서명은 매번 다름 (PSS 패딩)")
        fun `동일한 데이터라도 PSS 패딩으로 인해 매번 다른 서명이 생성된다`() {
            // When
            val signature1 = RSASign.rsaSign(keyPair.private, testData, true)
            val signature2 = RSASign.rsaSign(keyPair.private, testData, true)

            // Then
            assertFalse(signature1.contentEquals(signature2))
        }

        @Test
        @DisplayName("서로 다른 데이터의 서명은 다름")
        fun `서로 다른 데이터는 다른 서명을 생성한다`() {
            // Given
            val data1 = "Hello".toByteArray()
            val data2 = "World".toByteArray()

            // When
            val signature1 = RSASign.rsaSign(keyPair.private, data1)
            val signature2 = RSASign.rsaSign(keyPair.private, data2)

            // Then
            assertFalse(signature1.contentEquals(signature2))
        }

        @Test
        @DisplayName("큰 데이터 서명")
        fun `큰 데이터에 대해서도 서명이 생성된다`() {
            // Given
            val largeData = ByteArray(10000) { it.toByte() }

            // When
            val signature = RSASign.rsaSign(keyPair.private, largeData)

            // Then
            assertNotNull(signature)
            assertTrue(signature.isNotEmpty())
        }
    }

    @Nested
    @DisplayName("RSA 서명 검증 테스트")
    inner class RsaVerifyTest {

        private val testData = "Hello, World!".toByteArray()
        private val keyPair: KeyPair = RSASign.generateRsaKeyPair()

        @Test
        @DisplayName("정상적인 서명 검증 성공")
        fun `올바른 서명이 성공적으로 검증된다`() {
            // Given
            val signature = RSASign.rsaSign(keyPair.private, testData)

            // When
            val isValid = RSASign.rsaVerify(keyPair.public, testData, signature)

            // Then
            assertTrue(isValid)
        }

        @Test
        @DisplayName("잘못된 서명 검증 실패")
        fun `잘못된 서명은 검증에 실패한다`() {
            // Given
            val wrongSignature = ByteArray(256) { 0 } // 잘못된 서명

            // When
            val isValid = RSASign.rsaVerify(keyPair.public, testData, wrongSignature)

            // Then
            assertFalse(isValid)
        }

        @Test
        @DisplayName("다른 키로 생성된 서명 검증 실패")
        fun `다른 개인키로 생성된 서명은 검증에 실패한다`() {
            // Given
            val anotherKeyPair = RSASign.generateRsaKeyPair()
            val signature = RSASign.rsaSign(anotherKeyPair.private, testData)

            // When
            val isValid = RSASign.rsaVerify(keyPair.public, testData, signature)

            // Then
            assertFalse(isValid)
        }

        @Test
        @DisplayName("변조된 데이터 검증 실패")
        fun `변조된 데이터는 검증에 실패한다`() {
            // Given
            val signature = RSASign.rsaSign(keyPair.private, testData)
            val tamperedData = "Hello, World!!".toByteArray() // 변조된 데이터

            // When
            val isValid = RSASign.rsaVerify(keyPair.public, tamperedData, signature)

            // Then
            assertFalse(isValid)
        }

        @Test
        @DisplayName("빈 데이터 서명 검증")
        fun `빈 데이터의 서명이 올바르게 검증된다`() {
            // Given
            val emptyData = ByteArray(0)
            val signature = RSASign.rsaSign(keyPair.private, emptyData)

            // When
            val isValid = RSASign.rsaVerify(keyPair.public, emptyData, signature)

            // Then
            assertTrue(isValid)
        }

        @Test
        @DisplayName("큰 데이터 서명 검증")
        fun `큰 데이터의 서명이 올바르게 검증된다`() {
            // Given
            val largeData = ByteArray(10000) { it.toByte() }
            val signature = RSASign.rsaSign(keyPair.private, largeData)

            // When
            val isValid = RSASign.rsaVerify(keyPair.public, largeData, signature)

            // Then
            assertTrue(isValid)
        }

        @Test
        @DisplayName("서명 길이가 다른 경우 예외 발생")
        fun `서명 길이가 다르면 예외가 발생한다`() {
            // Given
            val shortSignature = ByteArray(10) { 0 }

            // When & Then
            assertThrows<SignatureException> {
                RSASign.rsaVerify(keyPair.public, testData, shortSignature)
            }
        }
    }

    @Nested
    @DisplayName("통합 서명 및 검증 테스트")
    inner class IntegrationTest {

        @Test
        @DisplayName("전체 워크플로우 테스트")
        fun `키 생성부터 서명 검증까지 전체 과정이 정상 동작한다`() {
            // Given
            val data = "Integration test data".toByteArray()

            // When
            val keyPair = RSASign.generateRsaKeyPair()
            val signature = RSASign.rsaSign(keyPair.private, data)
            val isValid = RSASign.rsaVerify(keyPair.public, data, signature)

            // Then
            assertTrue(isValid)
        }

        @Test
        @DisplayName("여러 키 페어로 독립적인 서명 검증")
        fun `여러 키 페어가 독립적으로 동작한다`() {
            // Given
            val data = "Multi keypair test".toByteArray()
            val keyPair1 = RSASign.generateRsaKeyPair()
            val keyPair2 = RSASign.generateRsaKeyPair()

            // When
            val signature1 = RSASign.rsaSign(keyPair1.private, data)
            val signature2 = RSASign.rsaSign(keyPair2.private, data)

            // Then
            assertTrue(RSASign.rsaVerify(keyPair1.public, data, signature1))
            assertTrue(RSASign.rsaVerify(keyPair2.public, data, signature2))
            assertFalse(RSASign.rsaVerify(keyPair1.public, data, signature2))
            assertFalse(RSASign.rsaVerify(keyPair2.public, data, signature1))
        }

        @Test
        @DisplayName("다양한 데이터 타입 서명 검증")
        fun `다양한 데이터 타입에 대해 서명과 검증이 정상 동작한다`() {
            // Given
            val keyPair = RSASign.generateRsaKeyPair()
            val testCases = listOf(
                "ASCII text".toByteArray(),
                "한글 텍스트".toByteArray(Charsets.UTF_8),
                "Special chars: !@#$%^&*()".toByteArray(),
                ByteArray(100) { (it % 256).toByte() }, // 바이너리 데이터
                "".toByteArray() // 빈 데이터
            )

            testCases.forEach { data ->
                // When
                val signature = RSASign.rsaSign(keyPair.private, data)
                val isValid = RSASign.rsaVerify(keyPair.public, data, signature)

                // Then
                assertTrue(isValid, "데이터 타입별 서명 검증 실패: ${data.contentToString()}")
            }
        }
    }

    @Nested
    @DisplayName("키 사이즈별 테스트")
    inner class KeySizeTest {

        @Test
        @DisplayName("1024비트 키 테스트")
        fun `1024비트 키로 서명과 검증이 정상 동작한다`() {
            // Given
            val keyPair = RSASign.generateRsaKeyPair(1024)
            val data = "1024 bit key test".toByteArray()

            // When
            val signature = RSASign.rsaSign(keyPair.private, data)
            val isValid = RSASign.rsaVerify(keyPair.public, data, signature)

            // Then
            assertTrue(isValid)
        }

        @Test
        @DisplayName("2048비트 키 테스트")
        fun `2048비트 키로 서명과 검증이 정상 동작한다`() {
            // Given
            val keyPair = RSASign.generateRsaKeyPair(2048)
            val data = "2048 bit key test".toByteArray()

            // When
            val signature = RSASign.rsaSign(keyPair.private, data)
            val isValid = RSASign.rsaVerify(keyPair.public, data, signature)

            // Then
            assertTrue(isValid)
        }

        @Test
        @DisplayName("4096비트 키 테스트")
        fun `4096비트 키로 서명과 검증이 정상 동작한다`() {
            // Given
            val keyPair = RSASign.generateRsaKeyPair(4096)
            val data = "4096 bit key test".toByteArray()

            // When
            val signature = RSASign.rsaSign(keyPair.private, data)
            val isValid = RSASign.rsaVerify(keyPair.public, data, signature)

            // Then
            assertTrue(isValid)
        }
    }

    @Nested
    @DisplayName("예외 상황 테스트")
    inner class ExceptionTest {

        private val keyPair: KeyPair = RSASign.generateRsaKeyPair()
        private val testData = "Exception test data".toByteArray()

        @Test
        @DisplayName("잘못된 키 타입으로 서명 시도")
        fun `RSA가 아닌 키로 서명하면 예외가 발생한다`() {
            // Given
            val anotherKeyPair = java.security.KeyPairGenerator.getInstance("DSA").apply {
                initialize(1024)
            }.generateKeyPair()

            // When & Then
            assertThrows<Exception> {
                RSASign.rsaSign(anotherKeyPair.private, testData)
            }
        }

        @Test
        @DisplayName("잘못된 키 타입으로 검증 시도")
        fun `RSA가 아닌 키로 검증하면 예외가 발생한다`() {
            // Given
            val signature = RSASign.rsaSign(keyPair.private, testData)
            val anotherKeyPair = java.security.KeyPairGenerator.getInstance("DSA").apply {
                initialize(1024)
            }.generateKeyPair()

            // When & Then
            assertThrows<Exception> {
                RSASign.rsaVerify(anotherKeyPair.public, testData, signature)
            }
        }

        @Test
        @DisplayName("null 데이터로 서명 시도")
        fun `null 데이터로 서명하면 예외가 발생한다`() {
            // When & Then
            assertThrows<Exception> {
                RSASign.rsaSign(keyPair.private, null as ByteArray)
            }
        }

        @Test
        @DisplayName("null 데이터로 검증 시도")
        fun `null 데이터로 검증하면 예외가 발생한다`() {
            // Given
            val signature = RSASign.rsaSign(keyPair.private, testData)

            // When & Then
            assertThrows<Exception> {
                RSASign.rsaVerify(keyPair.public, null as ByteArray, signature)
            }
        }

        @Test
        @DisplayName("null 서명으로 검증 시도")
        fun `null 서명으로 검증하면 예외가 발생한다`() {
            // When & Then
            assertThrows<Exception> {
                RSASign.rsaVerify(keyPair.public, testData, null as ByteArray)
            }
        }
    }

    @Nested
    @DisplayName("보안 특성 테스트")
    inner class SecurityTest {

        @Test
        @DisplayName("개인키 없이는 서명 생성 불가")
        fun `공개키로는 서명을 생성할 수 없다`() {
            // Given
            val keyPair = RSASign.generateRsaKeyPair()
            val data = "Security test".toByteArray()

            // When & Then
            assertThrows<ClassCastException> {
                RSASign.rsaSign(keyPair.public as PrivateKey, data)
            }
        }

        @Test
        @DisplayName("공개키 없이는 검증 불가")
        fun `개인키로는 서명을 검증할 수 없다`() {
            // Given
            val keyPair = RSASign.generateRsaKeyPair()
            val data = "Security test".toByteArray()
            val signature = RSASign.rsaSign(keyPair.private, data)

            // When & Then
            assertThrows<ClassCastException> {
                RSASign.rsaVerify(keyPair.private as PublicKey, data, signature)
            }
        }

        @Test
        @DisplayName("서명 변조 감지")
        fun `서명이 변조되면 검증에 실패한다`() {
            // Given
            val data = "Tamper test".toByteArray()
            val signature = RSASign.rsaSign(keyPair.private, data)
            val tamperedSignature = signature.clone().apply {
                this[0] = (this[0].toInt() xor 1).toByte() // 첫 번째 바이트 변조
            }

            // When
            val isValid = RSASign.rsaVerify(keyPair.public, data, tamperedSignature)

            // Then
            assertFalse(isValid)
        }
    }

    private val keyPair: KeyPair = RSASign.generateRsaKeyPair()
}
