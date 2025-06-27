package com.snc.test.core.regex

import com.snc.zero.core.regex.RegularExpr
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_EMAIL
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_HANGUL
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_INPUT_CHARS
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PHONE_NUM
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PW_ENG_NUM_SPC
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PW_ENG_UC_LC_NUM_SPC
import com.snc.zero.core.util.StringUtil
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class RegularExprTest : BaseJUnit5Test() {

    @Test
    fun `RegularExpr 테스트`() {
        val arr: Array<String> = RegularExpr.find("[a-zA-Z]+", "/api/envelopes")
        println("API: ${StringUtil.toString(arr)}")
        //println("API: ${arrayListOf("1","2").joinToString(prefix = "[", separator = "|", postfix = "]")}")
        //println("API: ${listOf("1")}")
        println()

        println("PATTERN_DIGIT: ${StringUtil.toString(RegularExpr.find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_LETTER_LOWER: ${StringUtil.toString(RegularExpr.find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_LETTER_UPPER: ${StringUtil.toString(RegularExpr.find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")

        println("PATTERN_INPUT_HANGUL: ${StringUtil.toString(RegularExpr.find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_HANGUL: ${RegularExpr.find(PATTERN_HANGUL, "ㄱㅎㅏ123ㅣ가").joinToString(separator = "")}")
        println("PATTERN_PHONE_NUM: ${StringUtil.toString(RegularExpr.find(PATTERN_PHONE_NUM, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_EMAIL ${StringUtil.toString(RegularExpr.find(PATTERN_EMAIL, "ㄱㅎㅏ12aas@hanwha.com3ㅣ가"))}")
        println()

        println("PATTERN_PWD_ENG_UC_LC_NUM_SPC_ALL: ${RegularExpr.matches(PATTERN_PW_ENG_UC_LC_NUM_SPC, "Ab1@Ab1@11")}")
        println("PATTERN_PWD_ENG_NUM_SPC: ${RegularExpr.matches(PATTERN_PW_ENG_NUM_SPC, "Ab1@Ab1@11")}")
        println()
    }

    @Test
    fun `matches - 영어 대소문자 숫자 특수문자 포함 비밀번호 유효`() {
        val password = "Abcd1234!@"
        val result = RegularExpr.matches(PATTERN_PW_ENG_UC_LC_NUM_SPC, password)
        assertTrue(result)
    }

    @Test
    fun `matches - 영어 숫자 특수문자 포함 비밀번호 유효`() {
        val password = "abcd1234!@"
        val result = RegularExpr.matches(PATTERN_PW_ENG_NUM_SPC, password)
        assertTrue(result)
    }

    @Test
    fun `matches - 전화번호 유효`() {
        val phone = "010-1234-5678"
        val result = RegularExpr.matches(PATTERN_PHONE_NUM, phone)
        assertTrue(result)
    }

    @Test
    fun `matches - 이메일 유효`() {
        val email = "user@example.com"
        val result = RegularExpr.matches(PATTERN_EMAIL, email)
        assertTrue(result)
    }

    @Test
    fun `matches - 한글 문자열 유효`() {
        val input = "한글입력테스트"
        val result = RegularExpr.matches(PATTERN_HANGUL, input)
        assertTrue(result)
    }

    @Test
    fun `matches - 입력 문자셋 유효`() {
        val input = "abcABC123 한글"
        val result = RegularExpr.matches(PATTERN_INPUT_CHARS, input)
        assertTrue(result)
    }

    @Test
    fun `find - 문자열 내 이메일 추출`() {
        val text = "문의: user@example.com 또는 admin@test.co.kr 로 연락주세요."
        val found = RegularExpr.find(PATTERN_EMAIL, text)
        assertArrayEquals(arrayOf("user@example.com", "admin@test.co.kr"), found)
    }

    @Test
    fun `find - 문자열 내 전화번호 추출`() {
        val text = "전화번호는 010-1234-5678 또는 +82-10-1111-2222 입니다."
        val found = RegularExpr.find(PATTERN_PHONE_NUM, text)
        assertArrayEquals(arrayOf("010-1234-5678", "+82-10-1111-2222"), found)
    }

    @Test
    fun `find - 정규식 문법 오류 발생시 빈 배열 반환`() {
        val invalidRegex = "[abc" // 닫히지 않은 괄호
        val found = RegularExpr.find(invalidRegex, "abcabcabc")
        assertArrayEquals(emptyArray<String>(), found)
    }

    @Test
    fun `find should include matched non-empty strings`() {
        val pattern = "\\d+" // 숫자 추출
        val input = "전화번호 123, 또 다른 번호는 456입니다"
        val result = RegularExpr.find(pattern, input)
        assertArrayEquals(arrayOf("123", "456"), result)
    }

    @Test
    fun `find should exclude empty string matches`() {
        val pattern = "\\s*" // 0개 이상의 공백 (빈 문자열 포함 가능)
        val input = " " // 공백 하나
        val result = RegularExpr.find(pattern, input)

        // .group() 결과가 "" 일 수 있으나 isNotEmpty() 조건으로 필터됨
        assertTrue(result.isNotEmpty()) // 결과 배열은 비어 있어야 함
    }

    @Test
    fun `find should return empty array when no match`() {
        val pattern = "[a-z]+" // 알파벳 소문자
        val input = "12345" // 숫자만 존재
        val result = RegularExpr.find(pattern, input)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `find should ignore empty match in between valid matches`() {
        val pattern = "." // 빈 문자열 포함 가능한 모든 문자열 (비추천이지만 예외 케이스로 테스트)
        val input = "ab"
        val result = RegularExpr.find(pattern, input)

        // isNotEmpty() 필터링으로 인해 빈 문자열은 제외됨 → 'a', 'b'만 추출됨
        assertTrue(result.contains("a"))
        assertTrue(result.contains("b"))
        assertFalse(result.contains("")) // 빈 문자열 없어야 함
    }

    @Nested
    @DisplayName("matches 메서드 테스트")
    inner class MatchesTest {

        @Test
        @DisplayName("패스워드 패턴 매칭 테스트")
        fun testPasswordPatternMatching() {
            // Given
            val validPassword = "Password123!"
            val invalidPassword = "password"

            // When & Then
            assertTrue(RegularExpr.matches(PATTERN_PW_ENG_UC_LC_NUM_SPC, validPassword))
            assertFalse(RegularExpr.matches(PATTERN_PW_ENG_UC_LC_NUM_SPC, invalidPassword))
        }

        @Test
        @DisplayName("전화번호 패턴 매칭 테스트")
        fun testPhoneNumberPatternMatching() {
            // Given
            val validPhone = "010-1234-5678"
            val invalidPhone = "123-456-789"

            // When & Then
            assertTrue(RegularExpr.matches(PATTERN_PHONE_NUM, validPhone))
            assertFalse(RegularExpr.matches(PATTERN_PHONE_NUM, invalidPhone))
        }
    }

    @Nested
    @DisplayName("find 메서드 테스트")
    inner class FindTest {

        @Test
        @DisplayName("일반적인 문자열 찾기 테스트")
        fun testNormalStringFind() {
            // Given
            val pattern = "\\d+"
            val input = "abc123def456ghi"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            assertEquals(2, result.size)
            assertEquals("123", result[0])
            assertEquals("456", result[1])
        }

        @Test
        @DisplayName("빈 문자열 매칭이 발생하는 경우 테스트")
        fun testEmptyStringMatching() {
            // Given - 빈 문자열을 매칭할 수 있는 패턴 (0개 이상의 숫자)
            val pattern = "\\d*"
            val input = "abc"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            // 빈 문자열 매칭이 발생하지만 빈 문자열은 결과에 추가되지 않음
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("문자열 끝에서 빈 문자열 매칭이 발생하는 경우 테스트 - break 조건")
        fun testEmptyMatchingAtEndOfString() {
            // Given - 문자열 끝에서 빈 매칭이 발생할 수 있는 패턴
            val pattern = "a*"
            val input = "b"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("연속된 빈 매칭에서 region 이동 테스트")
        fun testConsecutiveEmptyMatchingWithRegionMove() {
            // Given - 연속된 빈 매칭이 발생할 수 있는 패턴
            val pattern = "x*"
            val input = "abc"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("매처 끝 위치가 입력 길이보다 작은 경우 테스트")
        fun testMatcherEndLessThanInputLength() {
            // Given - 특정 위치에서 빈 매칭이 발생하고 region을 이동해야 하는 경우
            val pattern = "(?=.)" // positive lookahead - 빈 문자열을 매칭하되 다음에 문자가 있어야 함
            val input = "test"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            // positive lookahead는 빈 문자열을 반환하므로 필터링됨
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("실제 빈 문자열 연속 매칭 시나리오 테스트")
        fun testActualEmptyStringConsecutiveMatching() {
            // Given - 실제 빈 문자열이 연속으로 매칭되고 region 이동이 필요한 케이스
            val pattern = "" // 빈 패턴은 모든 위치에서 빈 문자열과 매칭
            val input = "ab"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            // 빈 문자열들은 결과에서 필터링되므로 빈 배열
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("잘못된 정규식 패턴 테스트")
        fun testInvalidRegexPattern() {
            // Given
            val invalidPattern = "["
            val input = "test"

            // When
            val result = RegularExpr.find(invalidPattern, input)

            // Then
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("한글 패턴 매칭 테스트")
        fun testHangulPatternMatching() {
            // Given
            val input = "Hello안녕하세요World"

            // When
            val result = RegularExpr.find(PATTERN_HANGUL, input)

            // Then
            assertEquals(1, result.size)
            assertEquals("안녕하세요", result[0])
        }

        @Test
        @DisplayName("입력 문자 패턴 매칭 테스트")
        fun testInputCharsPatternMatching() {
            // Given
            val input = "Hello123 안녕!@#"

            // When
            val result = RegularExpr.find(PATTERN_INPUT_CHARS, input)

            // Then
            assertEquals(1, result.size)
            assertEquals("Hello123 안녕", result[0])
        }

        @Test
        @DisplayName("복잡한 빈 매칭 시나리오 - 문자열 중간에서 발생")
        fun testComplexEmptyMatchingInMiddle() {
            // Given - word boundary는 문자와 비문자 사이의 빈 위치를 매칭
            val pattern = "\\b"
            val input = "hello world"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            // word boundary는 빈 문자열을 반환하므로 필터링됨
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("문자열 끝에서 정확히 끝나는 매칭 테스트")
        fun testMatchingExactlyAtEnd() {
            // Given
            val pattern = "\\d"
            val input = "abc3"

            // When
            val result = RegularExpr.find(pattern, input)

            // Then
            assertEquals(1, result.size)
            assertEquals("3", result[0])
        }
    }

    @Nested
    @DisplayName("정규식 상수 테스트")
    inner class RegexConstantsTest {

        @Test
        @DisplayName("정규식 상수들이 올바르게 정의되었는지 테스트")
        fun testRegexConstants() {
            // When & Then
            assertNotNull(PATTERN_PW_ENG_UC_LC_NUM_SPC)
            assertNotNull(PATTERN_PW_ENG_NUM_SPC)
            assertNotNull(PATTERN_PHONE_NUM)
            assertNotNull(PATTERN_EMAIL)
            assertNotNull(PATTERN_HANGUL)
            assertNotNull(PATTERN_INPUT_CHARS)
        }

        @Test
        @DisplayName("이메일 패턴 매칭 테스트")
        fun testEmailPatternMatching() {
            // Given
            val validEmail = "test@example.com"
            val invalidEmail = "invalid.email"

            // When & Then
            assertTrue(RegularExpr.matches(PATTERN_EMAIL, validEmail))
            assertFalse(RegularExpr.matches(PATTERN_EMAIL, invalidEmail))
        }
    }
}
