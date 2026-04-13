package com.snc.test.crypto.cipher.fpe

import com.snc.zero.crypto.cipher.fpe.AesFf1Crypto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Suppress("NonAsciiCharacters")
@DisplayName("AES FF1 FPE 암호화 테스트")
class AesFf1CryptoTest {

    private val secretKeyString = "01234567890123456789012345678901" // 256-bit key
    private var tweak = AesFf1Crypto.generateTweak() //"static-tweak-value"
    private var aesFf1 = AesFf1Crypto(secretKeyString, tweak)

    // 특수문자 지원을 위한 Radix 95 알파벳
    private val asciiAlphabet = StringBuilder().apply {
        for (c in 32..126) {
            append(c.toChar())
        }
    }.toString()
    private val aesFf1Ascii = AesFf1Crypto(secretKeyString, tweak, asciiAlphabet)

    @BeforeEach
    fun setup() {
        tweak = AesFf1Crypto.generateTweak()
        println("[DEBUG_LOG] Running test with secret key: $secretKeyString, tweak: $tweak")

        aesFf1 = AesFf1Crypto(secretKeyString, tweak)
    }

    @Nested
    @DisplayName("정상 동작 테스트 (기본 Radix 62)")
    inner class SuccessTests {

        @Test
        @DisplayName("기본 암복호화 성공")
        fun `기본 암복호화 성공`() {
            val plainText = "HelloWorld123"

            val encrypted = aesFf1.encrypt(plainText)
            println("[DEBUG_LOG] Encrypted: $encrypted")

            val decrypted = aesFf1.decrypt(encrypted)

            assertEquals(plainText, decrypted)
            assertEquals(plainText.length, encrypted.length)
        }

        @Test
        @DisplayName("결정론적 암호화 확인 (동일 키, 동일 평문 -> 동일 암호문)")
        fun `결정론적 암호화 확인`() {
            val plainText = "DeterministicTest123"
            val encrypted1 = aesFf1.encrypt(plainText)
            val encrypted2 = aesFf1.encrypt(plainText)

            println("[DEBUG_LOG] Encrypted: $encrypted1")
            assertEquals(encrypted1, encrypted2)
        }

        @Test
        @DisplayName("고유성 확인 (서로 다른 평문 -> 서로 다른 암호문)")
        fun `고유성 확인`() {
            val plainTexts = listOf("aaaa", "aaab", "aaac", "abaa", "baaa")
            val cipherTexts = plainTexts.map { aesFf1.encrypt(it) }

            assertEquals(plainTexts.size, cipherTexts.distinct().size)
        }

        @Test
        @DisplayName("포맷 유지 확인 (알파뉴메릭 및 길이 유지)")
        fun `포맷 유지 확인`() {
            val alphanumericRegex = Regex("^[a-zA-Z0-9]*$")
            val inputs = listOf("abcd", "12345", "a1B2c3D4", "HelloWorld", "0000")

            for (input in inputs) {
                val encrypted = aesFf1.encrypt(input)
                assertEquals(input.length, encrypted.length)
                assertTrue(alphanumericRegex.matches(encrypted), "Encrypted string '$encrypted' should only contain alphanumeric characters")
            }
        }

        @Test
        @DisplayName("짧은 문자열 전수 순열 검증 (4자리 숫자)")
        fun `짧은 문자열 전수 순열 검증`() {
            val inputs = (0..999).map { it.toString().padStart(4, '0') }
            val cipherTexts = inputs.map { aesFf1.encrypt(it) }

            assertEquals(inputs.size, cipherTexts.distinct().size)
            cipherTexts.forEach { assertEquals(4, it.length) }
        }
    }

    @Nested
    @DisplayName("중복 생성 검사 테스트 (Radix 62)")
    inner class DuplicateCheckTests {

        @Test
        @DisplayName("4자리 숫자 전수 중복 검사 (0000-9999)")
        fun `4자리 숫자 전수 중복 검사`() {
            // 10,000개의 모든 조합에 대해 중복이 없는지 확인
            val inputs = (0..9999).map { it.toString().padStart(4, '0') }
            val cipherTexts = inputs.map { aesFf1.encrypt(it) }

            //println("[DEBUG_LOG] Unique cipher texts: ${cipherTexts.toList()}")
            assertEquals(inputs.size, cipherTexts.distinct().size, "10,000개의 숫자 조합에 대해 암호문은 모두 고유해야 합니다.")
        }

        @Test
        @DisplayName("랜덤 6자리 Radix 62 문자열 10,000개 중복 검사")
        fun `랜덤 6자리 Radix 62 문자열 중복 검사`() {
            val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            val random = java.util.Random(42) // 재현성을 위한 시드 고정

            val inputs = mutableSetOf<String>()
            while (inputs.size < 10000) {
                val sb = StringBuilder()
                repeat(6) {
                    sb.append(alphabet[random.nextInt(alphabet.length)])
                }
                inputs.add(sb.toString())
            }

            val cipherTexts = inputs.map { aesFf1.encrypt(it) }
            assertEquals(inputs.size, cipherTexts.distinct().size, "10,000개의 랜덤 문자열에 대해 암호문은 모두 고유해야 합니다.")
        }
    }

    @Nested
    @DisplayName("커스텀 알파벳 (ASCII 32-126) 테스트")
    inner class CustomAlphabetTests {

        @Test
        @DisplayName("특수문자 포함 암복호화 성공")
        fun `특수문자 포함 암복호화 성공`() {
            val plainText = "Hello_World!@# 123"

            val encrypted = aesFf1Ascii.encrypt(plainText)
            println("[DEBUG_LOG] Encrypted with special chars: $encrypted")

            val decrypted = aesFf1Ascii.decrypt(encrypted)

            assertEquals(plainText, decrypted)
            assertEquals(plainText.length, encrypted.length)
        }

        @Test
        @DisplayName("포맷 유지 확인 (ASCII 출력 가능 문자)")
        fun `포맷 유지 확인`() {
            val asciiPrintableRegex = Regex("^[\\x20-\\x7E]*$")
            // Radix 95일 때 최소 길이는 ceil(log95(1,000,000)) = 4
            val inputs = listOf("Hello World!", "@#$%^&*", "    ", "----")

            for (input in inputs) {
                val encrypted = aesFf1Ascii.encrypt(input)
                assertEquals(input.length, encrypted.length)
                assertTrue(asciiPrintableRegex.matches(encrypted), "Encrypted string '$encrypted' should only contain ASCII printable characters")
            }
        }
    }

    @Nested
    @DisplayName("최대 길이 검수 테스트")
    inner class LengthLimitTests {

        @Test
        @DisplayName("긴 문자열(1024자) 암복호화 성공")
        fun `긴 문자열 암복호화 성공`() {
            val longText = "a".repeat(1024)
            val encrypted = aesFf1.encrypt(longText)
            val decrypted = aesFf1.decrypt(encrypted)

            assertEquals(longText, decrypted)
            assertEquals(longText.length, encrypted.length)
        }

        @Test
        @DisplayName("매우 긴 문자열(10000자) 암복호화 성공")
        fun `매우 긴 문자열 암복호화 성공`() {
            val veryLongText = "abc123XYZ".repeat(1111) // 9999자
            val encrypted = aesFf1.encrypt(veryLongText)
            val decrypted = aesFf1.decrypt(encrypted)

            assertEquals(veryLongText, decrypted)
            assertEquals(veryLongText.length, encrypted.length)
        }
    }

    @Nested
    @DisplayName("Tweak 생성 테스트")
    inner class TweakGenerationTests {

        @Test
        @DisplayName("랜덤 Tweak 생성 확인")
        fun `랜덤 Tweak 생성 확인`() {
            val tweakLen = 16
            val tweak1 = AesFf1Crypto.generateTweak(tweakLen)
            val tweak2 = AesFf1Crypto.generateTweak(tweakLen)

            println("[DEBUG_LOG] Tweak 1: $tweak1")
            println("[DEBUG_LOG] Tweak 2: $tweak2")

            assertEquals(tweakLen, tweak1.length)
            assertEquals(tweakLen, tweak2.length)
            assertTrue(tweak1 != tweak2, "생성된 두 Tweak은 서로 달라야 합니다.")
        }

        @Test
        @DisplayName("커스텀 길이 Tweak 생성 확인")
        fun `커스텀 길이 Tweak 생성 확인`() {
            val length = 32
            val tweak = AesFf1Crypto.generateTweak(length)
            assertEquals(length, tweak.length)
        }

        @Test
        @DisplayName("생성된 Tweak을 사용한 암복호화 성공")
        fun `생성된 Tweak을 사용한 암복호화 성공`() {
            val generatedTweak = AesFf1Crypto.generateTweak()
            val crypto = AesFf1Crypto(secretKeyString, generatedTweak)
            val plainText = "HelloTweak123"

            val encrypted = crypto.encrypt(plainText)
            val decrypted = crypto.decrypt(encrypted)

            assertEquals(plainText, decrypted)
        }
    }

    @Nested
    @DisplayName("실패 케이스 테스트")
    inner class FailureTests {

        @Test
        @DisplayName("잘못된 AES 키 길이 (16, 24, 32바이트가 아님)")
        fun `잘못된 키 길이 실패`() {
            val invalidKey = "short-key"
            assertThrows<IllegalArgumentException> {
                AesFf1Crypto(invalidKey, tweak)
            }
        }

        @Test
        @DisplayName("범위를 벗어난 문자 포함 (특수문자 등) 실패")
        fun `지원하지 않는 문자 포함 실패`() {
            val invalidInput = "Hello_World!" // 기본 Radix 62에서는 특수문자가 포함되면 안 됨
            assertThrows<Exception> {
                aesFf1.encrypt(invalidInput)
            }
        }

        @Test
        @DisplayName("복호화 시 유효하지 않은 문자열 (범위 밖 문자)")
        fun `유효하지 않은 문자열 복호화 실패`() {
            val invalidCipher = "Invalid_한글#"
            assertThrows<Exception> {
                aesFf1.decrypt(invalidCipher)
            }
        }
    }
}
