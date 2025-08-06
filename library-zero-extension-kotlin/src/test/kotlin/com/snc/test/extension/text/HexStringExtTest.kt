package com.snc.test.extension.text

import com.snc.zero.extensions.text.toHexString
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HexStringExtTest : BaseJUnit5Test() {

    @Nested
    @DisplayName("ToHexString() 기본 테스트")
    inner class ToHexString() {

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
    }

    @Nested
    @DisplayName("ASCII 범위 문자 테스트 (0-127)")
    inner class AsciiRangeTest {

        @Test
        @DisplayName("숫자 문자는 2자리 16진수로 변환된다")
        fun `숫자 문자는 2자리 16진수로 변환된다`() {
            assertEquals("30", '0'.toHexString())
            assertEquals("39", '9'.toHexString())
        }

        @Test
        @DisplayName("영문 소문자는 2자리 16진수로 변환된다")
        fun `영문 소문자는 2자리 16진수로 변환된다`() {
            assertEquals("61", 'a'.toHexString())
            assertEquals("7a", 'z'.toHexString())
        }

        @Test
        @DisplayName("영문 대문자는 2자리 16진수로 변환된다")
        fun `영문 대문자는 2자리 16진수로 변환된다`() {
            assertEquals("41", 'A'.toHexString())
            assertEquals("5a", 'Z'.toHexString())
        }

        @Test
        @DisplayName("특수문자는 2자리 16진수로 변환된다")
        fun `특수문자는 2자리 16진수로 변환된다`() {
            assertEquals("20", ' '.toHexString()) // 공백
            assertEquals("21", '!'.toHexString())
            assertEquals("40", '@'.toHexString())
            assertEquals("23", '#'.toHexString())
        }

        @Test
        @DisplayName("제어문자는 2자리 16진수로 변환된다")
        fun `제어문자는 2자리 16진수로 변환된다`() {
            assertEquals("00", '\u0000'.toHexString()) // NULL
            assertEquals("09", '\t'.toHexString()) // TAB
            assertEquals("0a", '\n'.toHexString()) // LF
            assertEquals("0d", '\r'.toHexString()) // CR
            assertEquals("7f", '\u007F'.toHexString()) // DEL
        }

        @Test
        @DisplayName("ASCII 경계값 테스트")
        fun `ASCII 경계값 테스트`() {
            assertEquals("00", '\u0000'.toHexString()) // 최소값
            assertEquals("7f", '\u007F'.toHexString()) // 최대값 (127)
        }

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
        @DisplayName("ASCII와 비ASCII 경계값 테스트")
        fun `ASCII와 비ASCII 경계값 테스트`() {
            assertEquals("7f", '\u007F'.toHexString()) // 127 - ASCII 최대값
            assertEquals("0080", '\u0080'.toHexString()) // 128 - 비ASCII 최소값
        }

        @Test
        @DisplayName("최소값과 최대값 BMP 문자 테스트")
        fun `최소값과 최대값 BMP 문자 테스트`() {
            assertEquals("00", '\u0000'.toHexString()) // 최솟값
            assertEquals("ffff", '\uFFFF'.toHexString()) // BMP 최댓값
        }

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

    @Nested
    @DisplayName("추가 조건 커버리지 테스트")
    inner class AdditionalCoverageTest {

        @Test
        @DisplayName("코드 1 테스트 - 범위 내 최솟값 근처")
        fun testCode1() {
            // Given
            val char1 = 1.toChar()

            // When
            val result = char1.toHexString()

            // Then
            assertEquals("01", result)
        }

        @Test
        @DisplayName("코드 126 테스트 - ASCII 최댓값 바로 전")
        fun testCode126() {
            // Given
            val char126 = 126.toChar()

            // When
            val result = char126.toHexString()

            // Then
            assertEquals("7e", result)
        }

        @Test
        @DisplayName("코드 200 테스트 - 확장 범위 중간값")
        fun testCode200() {
            // Given
            val char200 = 200.toChar()

            // When
            val result = char200.toHexString()

            // Then
            assertEquals("00c8", result)
        }

        @Test
        @DisplayName("코드 127 정확한 경계값 재검증")
        fun testExactBoundary127() {
            // Given
            val exactBoundary = 127.toChar()

            // When
            val result = exactBoundary.toHexString()

            // Then
            assertEquals("7f", result)
            assertTrue(exactBoundary.code == 127)
            assertTrue(exactBoundary.code in 0..127)
        }

        @Test
        @DisplayName("코드 128 정확한 경계값 재검증")
        fun testExactBoundary128() {
            // Given
            val exactBoundary = 128.toChar()

            // When
            val result = exactBoundary.toHexString()

            // Then
            assertEquals("0080", result)
            assertTrue(exactBoundary.code == 128)
            assertFalse(exactBoundary.code in 0..127)
        }

        @Test
        @DisplayName("음수 범위 테스트 - 실제로는 불가능하지만 논리적 확인")
        fun testNegativeRangeLogic() {
            // 실제 Char는 음수가 될 수 없지만, 범위 조건의 논리적 완성도를 위해
            // 0 값에서 범위 시작점 확인
            val zeroChar = 0.toChar()
            val result = zeroChar.toHexString()

            assertEquals("00", result)
            assertTrue(zeroChar.code >= 0)
            assertTrue(zeroChar.code in 0..127)
        }

        @Test
        @DisplayName("최대 유니코드 값 테스트")
        fun testMaxUnicodeValue() {
            // Given
            val maxChar = 65535.toChar() // Char의 최댓값

            // When
            val result = maxChar.toHexString()

            // Then
            assertEquals("ffff", result)
            assertFalse(maxChar.code in 0..127)
        }

        @Test
        @DisplayName("중간 범위 경계 테스트 - 범위 조건의 모든 분기")
        fun testAllBranchConditions() {
            // 범위 내: true 분기 테스트
            val inRange = 64.toChar() // '@' 문자
            assertEquals("40", inRange.toHexString())

            // 범위 밖: false 분기 테스트
            val outRange = 256.toChar()
            assertEquals("0100", outRange.toHexString())

            // 경계값들 재확인
            assertTrue(0.toChar().code in 0..127)
            assertTrue(127.toChar().code in 0..127)
            assertFalse(128.toChar().code in 0..127)
        }
    }

    @Nested
    @DisplayName("비ASCII 범위 문자 테스트 (128 이상)")
    inner class NonAsciiRangeTest {

        @Test
        @DisplayName("확장 ASCII는 4자리 16진수로 변환된다")
        fun `확장 ASCII는 4자리 16진수로 변환된다`() {
            assertEquals("0080", '\u0080'.toHexString()) // 128
            assertEquals("00ff", '\u00FF'.toHexString()) // 255
        }

        @Test
        @DisplayName("한글은 4자리 16진수로 변환된다")
        fun `한글은 4자리 16진수로 변환된다`() {
            assertEquals("ac00", '가'.toHexString())
            assertEquals("d7a3", '힣'.toHexString())
            assertEquals("d55c", '한'.toHexString())
            assertEquals("ae00", '글'.toHexString())
        }

        @Test
        @DisplayName("중국어는 4자리 16진수로 변환된다")
        fun `중국어는 4자리 16진수로 변환된다`() {
            assertEquals("4e2d", '中'.toHexString())
            assertEquals("6587", '文'.toHexString())
        }

        @Test
        @DisplayName("일본어는 4자리 16진수로 변환된다")
        fun `일본어는 4자리 16진수로 변환된다`() {
            assertEquals("3042", 'あ'.toHexString())
            assertEquals("3044", 'い'.toHexString())
            assertEquals("3046", 'う'.toHexString())
        }

        @Test
        @DisplayName("이모지는 4자리 16진수로 변환된다")
        fun `이모지는 4자리 16진수로 변환된다`() {
            // BMP 범위 내의 이모지들
            assertEquals("263a", '☺'.toHexString())
            assertEquals("2764", '❤'.toHexString())
        }

        @Test
        @DisplayName("수학 기호는 4자리 16진수로 변환된다")
        fun `수학 기호는 4자리 16진수로 변환된다`() {
            assertEquals("221e", '∞'.toHexString())
            assertEquals("03c0", 'π'.toHexString())
            assertEquals("03b1", 'α'.toHexString())
        }
    }

    @Nested
    @DisplayName("포맷팅 정확성 테스트")
    inner class FormattingTest {

        @Test
        @DisplayName("02x 포맷 함수 호출 테스트")
        fun `02x 포맷 함수 호출 테스트`() {
            // "%02x".format(code) 호출 경로 테스트
            assertEquals("00", '\u0000'.toHexString()) // 0
            assertEquals("01", '\u0001'.toHexString()) // 1
            assertEquals("0f", '\u000F'.toHexString()) // 15
            assertEquals("10", '\u0010'.toHexString()) // 16
            assertEquals("7f", '\u007F'.toHexString()) // 127
        }

        @Test
        @DisplayName("04x 포맷 함수 호출 테스트")
        fun `04x 포맷 함수 호출 테스트`() {
            // "%04x".format(code) 호출 경로 테스트
            assertEquals("0080", '\u0080'.toHexString()) // 128
            assertEquals("00ff", '\u00FF'.toHexString()) // 255
            assertEquals("0100", '\u0100'.toHexString()) // 256
            assertEquals("ffff", '\uFFFF'.toHexString()) // 최대값
        }

        @Test
        @DisplayName("앞자리 0이 올바르게 패딩된다")
        fun `앞자리 0이 올바르게 패딩된다`() {
            assertEquals("01", '\u0001'.toHexString()) // ASCII 범위 패딩
            assertEquals("0f", '\u000F'.toHexString()) // ASCII 범위 패딩
            assertEquals("0100", '\u0100'.toHexString()) // 비ASCII 범위 패딩
            assertEquals("00ff", '\u00FF'.toHexString()) // 비ASCII 범위 패딩
        }

        @Test
        @DisplayName("16진수는 소문자로 출력된다")
        fun `16진수는 소문자로 출력된다`() {
            val result = 'A'.toHexString()
            assertEquals("41", result)
            assertTrue(result.all { it.isDigit() || it in 'a'..'f' })

            val unicodeResult = '가'.toHexString()
            assertEquals("ac00", unicodeResult)
            assertTrue(unicodeResult.all { it.isDigit() || it in 'a'..'f' })
        }

        @Test
        @DisplayName("포맷 문자열 길이 검증")
        fun `포맷 문자열 길이 검증`() {
            // ASCII 범위: 정확히 2자리
            (0..127).map { it.toChar() }.forEach { char ->
                assertEquals(2, char.toHexString().length)
            }

            // 비ASCII 범위: 정확히 4자리
            val nonAsciiSamples = listOf(128, 255, 256, 1000, 65535)
            nonAsciiSamples.map { it.toChar() }.forEach { char ->
                assertEquals(4, char.toHexString().length)
            }
        }
    }

    @Nested
    @DisplayName("예외 상황 및 에러 케이스")
    inner class ErrorCaseTest {

        @Test
        @DisplayName("널 문자도 올바르게 처리된다")
        fun `널 문자도 올바르게 처리된다`() {
            assertDoesNotThrow {
                val result = '\u0000'.toHexString()
                assertEquals("00", result)
            }
        }

        @Test
        @DisplayName("최대 유니코드 BMP 문자도 올바르게 처리된다")
        fun `최대 유니코드 BMP 문자도 올바르게 처리된다`() {
            assertDoesNotThrow {
                val result = '\uFFFF'.toHexString()
                assertEquals("ffff", result)
            }
        }

        @Test
        @DisplayName("반환값은 항상 non-null이고 비어있지 않다")
        fun `반환값은 항상 non-null이고 비어있지 않다`() {
            val testChars = listOf('a', '가', '\u0000', '\uFFFF', '1', '@')

            testChars.forEach { char ->
                val result = char.toHexString()
                assertNotNull(result)
                assertTrue(result.isNotEmpty())
                assertTrue(result.length == 2 || result.length == 4)
            }
        }
    }

    @Nested
    @DisplayName("조건 커버리지 완전 테스트")
    inner class ConditionCoverageTest {

        @Test
        @DisplayName("범위 연산자의 하한 경계 조건 테스트")
        fun `범위 연산자의 하한 경계 조건 테스트`() {
            // code >= 0 조건 테스트
            assertEquals("00", '\u0000'.toHexString()) // code == 0 (경계값)
            assertEquals("01", '\u0001'.toHexString()) // code > 0
        }

        @Test
        @DisplayName("범위 연산자의 상한 경계 조건 테스트")
        fun `범위 연산자의 상한 경계 조건 테스트`() {
            // code <= 127 조건 테스트
            assertEquals("7e", '\u007E'.toHexString()) // code < 127
            assertEquals("7f", '\u007F'.toHexString()) // code == 127 (경계값)
            assertEquals("0080", '\u0080'.toHexString()) // code > 127
        }

        @Test
        @DisplayName("in 연산자 false 조건 상세 테스트")
        fun `in 연산자 false 조건 상세 테스트`() {
            // code < 0은 불가능하지만 code > 127 케이스를 더 다양하게
            assertEquals("0080", '\u0080'.toHexString()) // 128
            assertEquals("0081", '\u0081'.toHexString()) // 129
            assertEquals("00ff", '\u00FF'.toHexString()) // 255
            assertEquals("0100", '\u0100'.toHexString()) // 256
            assertEquals("ffff", '\uFFFF'.toHexString()) // 최대값
        }

        @Test
        @DisplayName("범위 검사의 모든 분기 조건 테스트")
        fun `범위 검사의 모든 분기 조건 테스트`() {
            // in 0..127의 내부 구현: code >= 0 && code <= 127
            // 각각의 && 연산자 조건을 모두 테스트

            // true && true = true (ASCII 범위)
            val asciiChars = listOf('\u0000', '\u0001', '\u007E', '\u007F')
            asciiChars.forEach { char ->
                val result = char.toHexString()
                assertEquals(2, result.length)
            }

            // false || true = false (범위 밖)
            val nonAsciiChars = listOf('\u0080', '\u0100', '\u1000', '\uFFFF')
            nonAsciiChars.forEach { char ->
                val result = char.toHexString()
                assertEquals(4, result.length)
            }
        }

        @Test
        @DisplayName("코드 포인트 값 직접 검증")
        fun `코드 포인트 값 직접 검증`() {
            // Char.code 값을 직접 검증하여 조건 커버리지 보장
            assertTrue('\u0000'.code == 0)
            assertTrue('\u007F'.code == 127)
            assertTrue('\u0080'.code == 128)

            // 조건식의 각 부분 검증
            assertTrue('\u0000'.code >= 0)
            assertTrue('\u0000'.code <= 127)
            assertTrue('\u007F'.code >= 0)
            assertTrue('\u007F'.code <= 127)

            assertFalse('\u0080'.code <= 127)
            assertTrue('\u0080'.code >= 0) // 항상 true이지만 명시적 테스트
        }
    }

    @Nested
    @DisplayName("성능 및 일관성 테스트")
    inner class PerformanceConsistencyTest {

        @Test
        @DisplayName("동일한 문자는 항상 동일한 결과를 반환한다")
        fun `동일한 문자는 항상 동일한 결과를 반환한다`() {
            val testChar = '한'
            val results = (1..100).map { testChar.toHexString() }

            assertTrue(results.all { it == results.first() })
            assertEquals("d55c", results.first())
        }

        @Test
        @DisplayName("ASCII 범위는 모두 2자리, 비ASCII는 모두 4자리다")
        fun `ASCII 범위는 모두 2자리, 비ASCII는 모두 4자리다`() {
            // ASCII 범위 테스트
            (0..127).forEach { code ->
                val char = code.toChar()
                val result = char.toHexString()
                assertEquals(2, result.length, "ASCII 문자 '$char'($code)는 2자리여야 함")
            }

            // 비ASCII 범위 샘플 테스트
            val nonAsciiSamples = listOf(128, 255, 0x1000, 0xFFFF)
            nonAsciiSamples.forEach { code ->
                val char = code.toChar()
                val result = char.toHexString()
                assertEquals(4, result.length, "비ASCII 문자 '$char'($code)는 4자리여야 함")
            }
        }
    }
}
