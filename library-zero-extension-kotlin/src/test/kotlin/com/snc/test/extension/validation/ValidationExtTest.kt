package com.snc.test.extension.validation

import com.snc.zero.extensions.validation.isHangul
import com.snc.zero.extensions.validation.isLetter
import com.snc.zero.extensions.validation.isLowerCase
import com.snc.zero.extensions.validation.isNumber
import com.snc.zero.extensions.validation.isUpperCase
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class ValidationExtTest : BaseJUnit5Test() {

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
