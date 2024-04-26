package com.snc.test.validation.extensions.password

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseTest
import com.snc.zero.validation.extensions.password.validatePassword
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PasswordExtTest : BaseTest() {

    @Test
    fun `영문 + 숫자`() {
        // given
        val data = "abcd1234abcd"
        // when
        val v1 = data.validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(v1, false)
    }

    @Test
    fun `영소 + 영대 + 숫자`() {
        // given
        val data = "aBCd1234abcd"
        // when
        val v1 = data.validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(v1, false)
    }

    @Test
    fun `영소 + 영대 + 숫자 + 특수문자 1`() {
        // given
        val data = "aBCd1234!@#$"
        // when
        val v1 = data.validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(v1, true)
    }

    @Test
    fun `영소 + 영대 + 숫자 + 특수문자 2`() {
        // given
        val data = " aBCd 1234 "
        // when
        val v1 = data.trim().validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(v1, true)
    }
}