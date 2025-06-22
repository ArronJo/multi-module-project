package com.snc.test.extension.text

import com.snc.zero.extension.text.toHexString
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HexStringExtTest : BaseJUnit5Test() {

    @Test
    fun `ToHexString 테스트 - String 1`() {
        // given
        val data = "abcdefABCDEF"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 1: data -> $v" }
        assertEquals("616263646566414243444546", v)
    }

    @Test
    fun `ToHexString 테스트 - String 2`() {
        // given
        val data = "1234567890"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 2: data -> $v" }
        assertEquals("31323334353637383930", v)
    }

    @Test
    fun `ToHexString 테스트 - String 3`() {
        // given
        val data = " !@#$%^&*()"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 3: data -> $v" }
        assertEquals("2021402324255e262a2829", v)
    }

    @Test
    fun `ToHexString 테스트 - Char 1`() {
        val v = 0x60.toChar().toHexString()
        assertEquals("60", v)
    }

    @Test
    fun `ToHexString 테스트 - Char 2`() {
        val v = 0xAC00.toChar().toHexString()
        assertEquals("ac00", v)
    }

    @Nested
    @DisplayName("ASCII 범위 문자 테스트 (0-127)")
    inner class AsciiRangeTest {

        @Test
        @DisplayName("경계값 테스트 - 코드 0 (NULL 문자)")
        fun testNullCharacter() {
            // Given
            val nullChar = 0.toChar()

            // When
            val result = nullChar.toHexString()

            // Then
            assertEquals("00", result)
        }

        @Test
        @DisplayName("경계값 테스트 - 코드 127 (DEL 문자)")
        fun testDelCharacter() {
            // Given
            val delChar = 127.toChar()

            // When
            val result = delChar.toHexString()

            // Then
            assertEquals("7f", result)
        }

        @Test
        @DisplayName("일반 ASCII 문자 테스트 - 'A' (코드 65)")
        fun testLetterA() {
            // Given
            val letterA = 'A'

            // When
            val result = letterA.toHexString()

            // Then
            assertEquals("41", result)
        }

        @Test
        @DisplayName("일반 ASCII 문자 테스트 - 공백 문자 (코드 32)")
        fun testSpaceCharacter() {
            // Given
            val spaceChar = ' '

            // When
            val result = spaceChar.toHexString()

            // Then
            assertEquals("20", result)
        }
    }

    @Nested
    @DisplayName("확장 ASCII 및 유니코드 문자 테스트 (128 이상)")
    inner class ExtendedUnicodeTest {

        @Test
        @DisplayName("경계값 테스트 - 코드 128 (첫 번째 확장 ASCII)")
        fun testFirstExtendedAscii() {
            // Given
            val extendedChar = 128.toChar()

            // When
            val result = extendedChar.toHexString()

            // Then
            assertEquals("0080", result)
        }

        @Test
        @DisplayName("한글 문자 테스트 - '가' (코드 44032)")
        fun testKoreanCharacter() {
            // Given
            val koreanChar = '가'

            // When
            val result = koreanChar.toHexString()

            // Then
            assertEquals("ac00", result)
        }

        @Test
        @DisplayName("라틴 확장 문자 테스트 - 'À' (코드 192)")
        fun testLatinExtendedCharacter() {
            // Given
            val latinChar = 'À'

            // When
            val result = latinChar.toHexString()

            // Then
            assertEquals("00c0", result)
        }

        @Test
        @DisplayName("높은 유니코드 값 테스트 - '€' (코드 8364)")
        fun testEuroSymbol() {
            // Given
            val euroChar = '€'

            // When
            val result = euroChar.toHexString()

            // Then
            assertEquals("20ac", result)
        }
    }

    @Nested
    @DisplayName("경계값 상세 테스트")
    inner class BoundaryValueTest {

        @Test
        @DisplayName("ASCII 범위 마지막 문자 - 코드 126 ('~')")
        fun testTildeCharacter() {
            // Given
            val tildeChar = '~'

            // When
            val result = tildeChar.toHexString()

            // Then
            assertEquals("7e", result)
        }

        @Test
        @DisplayName("확장 범위 시작 바로 다음 - 코드 129")
        fun testCode129() {
            // Given
            val char129 = 129.toChar()

            // When
            val result = char129.toHexString()

            // Then
            assertEquals("0081", result)
        }

        @Test
        @DisplayName("16진수 포맷 확인 - 소문자 사용")
        fun testHexFormatLowerCase() {
            // Given
            val charWithHighValue = 255.toChar()

            // When
            val result = charWithHighValue.toHexString()

            // Then
            assertEquals("00ff", result)
            assertTrue(result.all { it.isDigit() || it in 'a'..'f' })
        }
    }

    @Nested
    @DisplayName("특수 케이스 테스트")
    inner class SpecialCaseTest {

        @Test
        @DisplayName("개행 문자 테스트 - '\\n' (코드 10)")
        fun testNewlineCharacter() {
            // Given
            val newlineChar = '\n'

            // When
            val result = newlineChar.toHexString()

            // Then
            assertEquals("0a", result)
        }

        @Test
        @DisplayName("탭 문자 테스트 - '\\t' (코드 9)")
        fun testTabCharacter() {
            // Given
            val tabChar = '\t'

            // When
            val result = tabChar.toHexString()

            // Then
            assertEquals("09", result)
        }

        @Test
        @DisplayName("백슬래시 문자 테스트 - '\\\\' (코드 92)")
        fun testBackslashCharacter() {
            // Given
            val backslashChar = '\\'

            // When
            val result = backslashChar.toHexString()

            // Then
            assertEquals("5c", result)
        }
    }
}
