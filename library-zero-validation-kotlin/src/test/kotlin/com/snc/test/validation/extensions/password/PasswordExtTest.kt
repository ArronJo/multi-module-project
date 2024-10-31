package com.snc.test.validation.extensions.password

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.validation.extensions.password.validatePassword
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PasswordExtTest : BaseJUnit5Test() {

    @Test
    fun `암호 유효성 검사`() {
        // given
        val data1 = "ab1@ab1@99"
        val data2 = "Ab12Ab1299"
        val data3 = "Ab1@Ab1@99"
        // when
        val v1 = data1.validatePassword()
        val v2 = data2.validatePassword()
        val v3 = data3.validatePassword()
        // then
        logger.debug { "비밀번호 검수 결과: $data1 -> $v1" }
        logger.debug { "비밀번호 검수 결과: $data2 -> $v2" }
        logger.debug { "비밀번호 검수 결과: $data3 -> $v3" }

        assertAll(
            { assertEquals(false, v1) },
            { assertEquals(false, v2) },
            { assertEquals(true, v3) }
        )
    }

    @Test
    fun `영문 + 숫자`() {
        // given
        val data = "abcd1234abcd"
        // when
        val v1 = data.validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(false, v1)
    }

    @Test
    fun `영소 + 영대 + 숫자`() {
        // given
        val data = "aBCd1234abcd"
        // when
        val v1 = data.validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(false, v1)
    }

    @Test
    fun `영소 + 영대 + 숫자 + 특수문자 1`() {
        // given
        val data = "aBCd1234!@#$"
        // when
        val v1 = data.validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(true, v1)
    }

    @Test
    fun `영소 + 영대 + 숫자 + 특수문자 2`() {
        // given
        val data = " aBCd 1234 "
        // when
        val v1 = data.trim().validatePassword()
        // then
        logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
        assertEquals(true, v1)
    }
}
