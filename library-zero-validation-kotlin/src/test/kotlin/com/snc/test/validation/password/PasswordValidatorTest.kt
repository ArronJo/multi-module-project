package com.snc.test.validation.password

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.validation.password.PasswordValidator
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PasswordValidatorTest : BaseJUnit5Test() {

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

        private val data = "1abc2DEF3ㅁㅇ릐ㅜㅍ치ㅗ감4!@#$%5^&*()6-=_+\\7[]';\":/?.,<>89"

        @Test
        fun `숫자 유효성`() {
            // given
            // when
            val v1 = PasswordValidator.validate(data, isDigit = true)
            // then
            println("문자열 (숫자) 검수 결과: $v1 : $data")
            assertEquals(1, v1)
        }

        @Test
        fun `영소 유효성`() {
            // given
            // when
            val v1 = PasswordValidator.validate(data, isLowerCase = true)
            // then
            println("문자열 (영소) 검수 결과: $v1 : $data")
            assertEquals(1, v1)
        }

        @Test
        fun `영대 유효성`() {
            // given
            // when
            val v1 = PasswordValidator.validate(data, isUpperCase = true)
            // then
            println("문자열 (영대) 검수 결과: $v1 : $data")
            assertEquals(1, v1)
        }

        @Test
        fun `영소 + 영대  유효성`() {
            // given
            // when
            val v1 = PasswordValidator.validate(data, isUpperCase = true, isLowerCase = true)
            // then
            println("문자열 (영소 + 영대) 검수 결과: $v1 : $data")
            assertEquals(2, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자  유효성`() {
            // given
            // when
            val v1 = PasswordValidator.validate(data, isLowerCase = true, isUpperCase = true, isDigit = true)
            // then
            println("문자열 (영소 + 영대 + 숫자) 검수 결과: $v1 : $data")
            assertEquals(3, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자 + 특수문자  유효성`() {
            // given
            // when
            val v1 = PasswordValidator.validate(
                data,
                isLowerCase = true,
                isUpperCase = true,
                isDigit = true,
                isSpecialChars = true
            )
            // then
            println("문자열 (영소 + 영대 + 숫자 + 특수문자) 검수 결과: $v1 : $data")
            assertEquals(4, v1)
        }
    }

    @Nested
    inner class Validation {

        @Test
        fun `암호 유효성 검사`() {
            // given
            val data1 = "ab1@ab1@99"
            val data2 = "Ab12Ab1299"
            val data3 = "Ab1@Ab1@99"
            // when
            val v1 = PasswordValidator.isValid(data1, true, true, true, true)
            val v2 = PasswordValidator.isValid(data2, true, true, true, true)
            val v3 = PasswordValidator.isValid(data3, true, true, true, true)
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
            val v1 = PasswordValidator.isValid(data)
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(false, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자`() {
            // given
            val data = "aBCd1234abcd"
            // when
            val v1 = PasswordValidator.isValid(data)
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(false, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자 + 특수문자 1`() {
            // given
            val data = "aBCd1234!@#$"
            // when
            val v1 = PasswordValidator.isValid(data)
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(true, v1)
        }

        @Test
        fun `영소 + 영대 + 숫자 + 특수문자 2`() {
            // given
            val data = " aBCd 1234 "
            // when
            val v1 = PasswordValidator.isValid(data.trim())
            // then
            logger.debug { "조건(영소,영대,숫자,특수문자) : 비밀번호 검수 결과: $data -> $v1" }
            assertEquals(true, v1)
        }
    }

    @Nested
    inner class IsValidTest {

        @Test
        fun `모든 조건을 만족하는 문자열은 true를 반환한다`() {
            // Given
            val password = "Abc123!@#"

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertTrue(result)
        }

        @Test
        fun `숫자가 없는 문자열은 false를 반환한다`() {
            // Given
            val password = "Abc!@#"

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertFalse(result)
        }

        @Test
        fun `대문자가 없는 문자열은 false를 반환한다`() {
            // Given
            val password = "abc123!@#"

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertFalse(result)
        }

        @Test
        fun `소문자가 없는 문자열은 false를 반환한다`() {
            // Given
            val password = "ABC123!@#"

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertFalse(result)
        }

        @Test
        fun `특수문자가 없는 문자열은 false를 반환한다`() {
            // Given
            val password = "Abc123"

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertFalse(result)
        }

        @Test
        fun `빈 문자열은 false를 반환한다`() {
            // Given
            val password = ""

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertFalse(result)
        }

        @Test
        fun `숫자가 없어도 나머지 조건을 만족하면 true를 반환한다`() {
            // Given
            val password = "Abc!@#"

            // When
            val result = PasswordValidator.isValid(password, isDigit = false)

            // Then
            assertTrue(result)
        }

        @Test
        fun `대문자가 없고 숫자 조건이 false면 false를 반환한다`() {
            // Given
            val password = "abc!@#"

            // When
            val result = PasswordValidator.isValid(password, isDigit = false)

            // Then
            assertFalse(result)
        }

        @Test
        fun `대문자가 없어도 나머지 조건을 만족하면 true를 반환한다`() {
            // Given
            val password = "abc123!@#"

            // When
            val result = PasswordValidator.isValid(password, isUpperCase = false)

            // Then
            assertTrue(result)
        }

        @Test
        fun `숫자가 없고 대문자 조건이 false면 false를 반환한다`() {
            // Given
            val password = "abc!@#"

            // When
            val result = PasswordValidator.isValid(password, isUpperCase = false)

            // Then
            assertFalse(result)
        }

        @Test
        fun `소문자가 없어도 나머지 조건을 만족하면 true를 반환한다`() {
            // Given
            val password = "ABC123!@#"

            // When
            val result = PasswordValidator.isValid(password, isLowerCase = false)

            // Then
            assertTrue(result)
        }

        @Test
        fun `숫자가 없고 소문자 조건이 false면 false를 반환한다`() {
            // Given
            val password = "ABC!@#"

            // When
            val result = PasswordValidator.isValid(password, isLowerCase = false)

            // Then
            assertFalse(result)
        }

        @Test
        fun `특수문자가 없어도 나머지 조건을 만족하면 true를 반환한다`() {
            // Given
            val password = "Abc123"

            // When
            val result = PasswordValidator.isValid(password, isSpecialChars = false)

            // Then
            assertTrue(result)
        }

        @Test
        fun `숫자가 없고 특수문자 조건이 false면 false를 반환한다`() {
            // Given
            val password = "Abc"

            // When
            val result = PasswordValidator.isValid(password, isSpecialChars = false)

            // Then
            assertFalse(result)
        }

        @Test
        fun `숫자와 대문자 조건이 false일 때 나머지 조건 만족하면 true를 반환한다`() {
            // Given
            val password = "abc!@#"

            // When
            val result = PasswordValidator.isValid(password, isDigit = false, isUpperCase = false)

            // Then
            assertTrue(result)
        }

        @Test
        fun `숫자와 대문자 조건이 false일 때 소문자가 없으면 false를 반환한다`() {
            // Given
            val password = "!@#"

            // When
            val result = PasswordValidator.isValid(password, isDigit = false, isUpperCase = false)

            // Then
            assertFalse(result)
        }

        @Test
        fun `모든 조건이 false일 때는 항상 true를 반환한다`() {
            // Given
            val password = "anystring"

            // When
            val result = PasswordValidator.isValid(
                password,
                isDigit = false,
                isUpperCase = false,
                isLowerCase = false,
                isSpecialChars = false
            )

            // Then
            assertTrue(result)
        }

        @Test
        fun `각 조건을 최소 1개씩만 만족해도 true를 반환한다`() {
            // Given
            val password = "A1a!"

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertTrue(result)
        }

        @Test
        fun `특수문자로 다양한 문자를 포함해도 검증된다`() {
            // Given
            val password = "Abc123!@#$%^&*()"

            // When
            val result = PasswordValidator.isValid(password)

            // Then
            assertTrue(result)
        }
    }
}
