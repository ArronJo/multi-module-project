package com.snc.test.validation.extensions.validation

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.validation.extensions.validation.isHangul
import com.snc.zero.validation.extensions.validation.isLetter
import com.snc.zero.validation.extensions.validation.isLowerCase
import com.snc.zero.validation.extensions.validation.isNumber
import com.snc.zero.validation.extensions.validation.isUpperCase
import com.snc.zero.validation.extensions.validation.validate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class ValidationExtTest : BaseJUnit5Test() {

    private lateinit var data: String

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        data = "1abc2DEF3ㅁㅇ릐ㅜㅍ치ㅗ감4!@#$%5^&*()6-=_+\\7[]';\":/?.,<>89"
    }

    @Test
    fun `숫자 유효성`() {
        // given
        // when
        val v1 = data.validate(isDigit = true)
        // then
        logger.debug { "문자열 (숫자) 검수 결과: $v1 : $data" }
        assertEquals(1, v1)
    }

    @Test
    fun `영소 유효성`() {
        // given
        // when
        val v1 = data.validate(isLowerCase = true)
        // then
        logger.debug { "문자열 (영소) 검수 결과: $v1 : $data" }
        assertEquals(1, v1)
    }

    @Test
    fun `영대 유효성`() {
        // given
        // when
        val v1 = data.validate(isUpperCase = true)
        // then
        logger.debug { "문자열 (영대) 검수 결과: $v1 : $data" }
        assertEquals(1, v1)
    }

    @Test
    fun `영소 + 영대  유효성`() {
        // given
        // when
        val v1 = data.validate(isUpperCase = true, isLowerCase = true)
        // then
        logger.debug { "문자열 (영소 + 영대) 검수 결과: $v1 : $data" }
        assertEquals(2, v1)
    }

    @Test
    fun `영소 + 영대 + 숫자  유효성`() {
        // given
        // when
        val v1 = data.validate(isLowerCase = true, isUpperCase = true, isDigit = true)
        // then
        logger.debug { "문자열 (영소 + 영대 + 숫자) 검수 결과: $v1 : $data" }
        assertEquals(3, v1)
    }

    @Test
    fun `영소 + 영대 + 숫자 + 특수문자  유효성`() {
        // given
        // when
        val v1 = data.validate(isLowerCase = true, isUpperCase = true, isDigit = true, isSpecialChars = true)
        // then
        logger.debug { "문자열 (영소 + 영대 + 숫자 + 특수문자) 검수 결과: $v1 : $data" }
        assertEquals(4, v1)
    }

    @Test
    fun `숫자로 구성되어 있느냐`() {
        // given
        val data = "1234567890"
        // when
        val v1 = data.isNumber()
        // then
        logger.debug { "숫자로 구성되어 있느냐 결과: $v1 : $data" }
        assertEquals(true, v1)
    }

    @Test
    fun `영소로 구성되어 있느냐`() {
        // given
        val data = "asdfasdffsdsdfasdfasdf"
        // when
        val v1 = data.isLowerCase()
        // then
        logger.debug { "영소로 구성되어 있느냐 결과: $v1 : $data" }
        assertEquals(true, v1)
    }
    @Test
    fun `영대로 구성되어 있느냐`() {
        // given
        val data = "ASDDASASDXCVBVNCFTU"
        // when
        val v1 = data.isUpperCase()
        // then
        logger.debug { "영대로 구성되어 있느냐 결과: $v1 : $data" }
        assertEquals(true, v1)
    }

    @Test
    fun `(영소 + 영대)로 구성되어 있느냐`() {
        // given
        val data = "asdasdasASDDASASDdasasdasdASDasdasdASD"
        // when
        val v1 = data.isLetter()
        // then
        logger.debug { "(영소 + 영대)로 구성되어 있느냐 결과: $v1 : $data" }
        assertEquals(true, v1)
    }

    @Test
    fun `한글로 구성되어 있느냐`() {
        // given
        val data = "가나담ㄴㅇㅌㄴㅊ피ᄩㅡ퍄ㅐㅈ덩ㄹ"
        // when
        val v1 = data.isHangul()
        // then
        logger.debug { "한글로 구성되어 있느냐 결과: $v1 : $data" }
        assertEquals(true, v1)
    }
}