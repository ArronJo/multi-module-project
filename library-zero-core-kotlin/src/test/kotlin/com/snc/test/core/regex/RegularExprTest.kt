package com.snc.test.core.regex

import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_EMAIL
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_HANGUL
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_INPUT_CHARS
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PHONE_NUM
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PW_ENG_NUM_SPC
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PW_ENG_UC_LC_NUM_SPC
import com.snc.zero.core.regex.RegularExpr.Companion.find
import com.snc.zero.core.regex.RegularExpr.Companion.matches
import com.snc.zero.core.util.StringUtil
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class RegularExprTest : BaseJUnit5Test() {

    @Test
    fun `RegularExpr 테스트`() {
        val arr: Array<String> = find("[a-zA-Z]+", "/api/envelopes")
        println("API: ${StringUtil.toString(arr)}")
        //println("API: ${arrayListOf("1","2").joinToString(prefix = "[", separator = "|", postfix = "]")}")
        //println("API: ${listOf("1")}")
        println()

        println("PATTERN_DIGIT: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_LETTER_LOWER: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_LETTER_UPPER: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")

        println("PATTERN_INPUT_HANGUL: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_HANGUL: ${find(PATTERN_HANGUL, "ㄱㅎㅏ123ㅣ가").joinToString(separator = "")}")
        println("PATTERN_PHONE_NUM: ${StringUtil.toString(find(PATTERN_PHONE_NUM, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_EMAIL ${StringUtil.toString(find(PATTERN_EMAIL, "ㄱㅎㅏ12aas@hanwha.com3ㅣ가"))}")
        println()

        println("PATTERN_PWD_ENG_UC_LC_NUM_SPC_ALL: ${matches(PATTERN_PW_ENG_UC_LC_NUM_SPC, "Ab1@Ab1@11")}")
        println("PATTERN_PWD_ENG_NUM_SPC: ${matches(PATTERN_PW_ENG_NUM_SPC, "Ab1@Ab1@11")}")
        println()
    }

    @Test
    fun `matches - 영어 대소문자 숫자 특수문자 포함 비밀번호 유효`() {
        val password = "Abcd1234!@"
        val result = matches(PATTERN_PW_ENG_UC_LC_NUM_SPC, password)
        assertTrue(result)
    }

    @Test
    fun `matches - 영어 숫자 특수문자 포함 비밀번호 유효`() {
        val password = "abcd1234!@"
        val result = matches(PATTERN_PW_ENG_NUM_SPC, password)
        assertTrue(result)
    }

    @Test
    fun `matches - 전화번호 유효`() {
        val phone = "010-1234-5678"
        val result = matches(PATTERN_PHONE_NUM, phone)
        assertTrue(result)
    }

    @Test
    fun `matches - 이메일 유효`() {
        val email = "user@example.com"
        val result = matches(PATTERN_EMAIL, email)
        assertTrue(result)
    }

    @Test
    fun `matches - 한글 문자열 유효`() {
        val input = "한글입력테스트"
        val result = matches(PATTERN_HANGUL, input)
        assertTrue(result)
    }

    @Test
    fun `matches - 입력 문자셋 유효`() {
        val input = "abcABC123 한글"
        val result = matches(PATTERN_INPUT_CHARS, input)
        assertTrue(result)
    }

    @Test
    fun `find - 문자열 내 이메일 추출`() {
        val text = "문의: user@example.com 또는 admin@test.co.kr 로 연락주세요."
        val found = find(PATTERN_EMAIL, text)
        assertArrayEquals(arrayOf("user@example.com", "admin@test.co.kr"), found)
    }

    @Test
    fun `find - 문자열 내 전화번호 추출`() {
        val text = "전화번호는 010-1234-5678 또는 +82-10-1111-2222 입니다."
        val found = find(PATTERN_PHONE_NUM, text)
        assertArrayEquals(arrayOf("010-1234-5678", "+82-10-1111-2222"), found)
    }

    @Test
    fun `find - 정규식 문법 오류 발생시 빈 배열 반환`() {
        val invalidRegex = "[abc" // 닫히지 않은 괄호
        val found = find(invalidRegex, "abcabcabc")
        assertArrayEquals(emptyArray<String>(), found)
    }
}
