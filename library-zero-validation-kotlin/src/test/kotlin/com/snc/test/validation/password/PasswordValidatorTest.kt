package com.snc.test.validation.password

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.validation.password.PasswordValidator
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PasswordValidatorTest : BaseJUnit5Test() {

    @Nested
    inner class Validation {

        @Test
        fun `암호 유효성 검사`() {
            // given
            val data1 = "ab1@ab1@99"
            val data2 = "Ab12Ab1299"
            val data3 = "Ab1@Ab1@99"
            // when
            val v1 = PasswordValidator.validate(data1)
            val v2 = PasswordValidator.validate(data2)
            val v3 = PasswordValidator.validate(data3)
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
            val v1 = PasswordValidator.validate(data)
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(false, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자`() {
            // given
            val data = "aBCd1234abcd"
            // when
            val v1 = PasswordValidator.validate(data)
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(false, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자 + 특수문자 1`() {
            // given
            val data = "aBCd1234!@#$"
            // when
            val v1 = PasswordValidator.validate(data)
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(true, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자 + 특수문자 2`() {
            // given
            val data = " aBCd 1234 "
            // when
            val v1 = PasswordValidator.validate(data.trim())
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(true, v1)
        }
    }

    @Nested
    inner class ValidationWith {

        @Test
        fun `파라마터 테스트`() {
            val data = " abcd 1234 "
            val v1 = PasswordValidator.validate(data.trim())
            val v2 = PasswordValidator.validate(data.trim(), false)
            val v3 = PasswordValidator.validate(data.trim(), false, false)
            val v4 = PasswordValidator.validate(data.trim(), false, false, false)
            val v5 = PasswordValidator.validate(data.trim(), false, false, false, false)

            println(v1)
            println(v2)
            println(v3)
            println(v4)
            println(v5)
        }
    }
}
