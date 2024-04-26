package com.snc.test.validation.extensions.validation

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseTest
import com.snc.zero.validation.extensions.validation.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class ValidationExtTest : BaseTest() {

    private lateinit var data: String

    @BeforeEach
    fun beforeEach() {
        data = "1abc2DEF3ㅁㅇ릐ㅜㅍ치ㅗ감4!@#$%5^&*()6-=_+\\7[]';\":/?.,<>89"
    }

    @Test
    fun `숫자 유효성`() {
        // given
        // when
        val v1 = data.validate(isDigit = true)
        // then
        logger.debug { "문자열 (숫자) 검수 결과: $v1" }
        assertEquals(v1, 1)
    }

    @Test
    fun `영소 유효성`() {
        // given
        // when
        val v1 = data.validate(isLowerCase = true)
        // then
        logger.debug { "문자열 (영소) 검수 결과: $v1" }
        assertEquals(v1, 1)
    }

    @Test
    fun `영대 유효성`() {
        // given
        // when
        val v1 = data.validate(isUpperCase = true)
        // then
        logger.debug { "문자열 (영대) 검수 결과: $v1" }
        assertEquals(v1, 1)
    }

    @Test
    fun `영소 + 영대  유효성`() {
        // given
        // when
        val v1 = data.validate(isUpperCase = true, isLowerCase = true)
        // then
        logger.debug { "문자열 (영소 + 영대) 검수 결과: $v1" }
        assertEquals(v1, 2)
    }

    @Test
    fun `영소 + 영대 + 숫자  유효성`() {
        // given
        // when
        val v1 = data.validate(isLowerCase = true, isUpperCase = true, isDigit = true)
        // then
        logger.debug { "문자열 (영소 + 영대 + 숫자) 검수 결과: $v1" }
        assertEquals(v1, 3)
    }

    @Test
    fun `영소 + 영대 + 숫자 + 특수문자  유효성`() {
        // given
        // when
        val v1 = data.validate(isLowerCase = true, isUpperCase = true, isDigit = true, isSpecialChars = true)
        // then
        logger.debug { "문자열 (영소 + 영대 + 숫자 + 특수문자) 검수 결과: $v1" }
        assertEquals(v1, 4)
    }

    @Test
    fun `숫자로 구성되어 있느냐`() {
        // given
        val data = "1234567890"
        // when
        val v1 = data.isNumber()
        // then
        logger.debug { "숫자로 구성되어 있느냐 결과: $v1" }
        assertEquals(v1, true)
    }

    @Test
    fun `영소로 구성되어 있느냐`() {
        // given
        val data = "asdfasdffsdsdfasdfasdf"
        // when
        val v1 = data.isLowerCase()
        // then
        logger.debug { "영소로 구성되어 있느냐 결과: $v1" }
        assertEquals(v1, true)
    }
    @Test
    fun `영대로 구성되어 있느냐`() {
        // given
        val data = "ASDDASASDXCVBVNCFTU"
        // when
        val v1 = data.isUpperCase()
        // then
        logger.debug { "영대로 구성되어 있느냐 결과: $v1" }
        assertEquals(v1, true)
    }

    @Test
    fun `(영소 + 영대)로 구성되어 있느냐`() {
        // given
        val data = "asdasdasASDDASASDdasasdasdASDasdasdASD"
        // when
        val v1 = data.isLetter()
        // then
        logger.debug { "(영소 + 영대)로 구성되어 있느냐 결과: $v1" }
        assertEquals(v1, true)
    }

    @Test
    fun `한글로 구성되어 있느냐`() {
        // given
        val data = "가나담ㄴㅇㅌㄴㅊ피ㅡ퍄ㅐㅈ덩ㄹ"
        // when
        val v1 = data.isHangul()
        // then
        logger.debug { "한글로 구성되어 있느냐 결과: $v1" }
        assertEquals(v1, true)
    }
}