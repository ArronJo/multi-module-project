package com.snc.test.core.util

import com.snc.zero.core.util.StringUtil
import com.snc.zero.test.base.BaseJUnit5Test
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class StringUtilTest : BaseJUnit5Test() {

    @Nested
    @DisplayName("ToString 검증")
    inner class ToStringValidation {

        @Test
        fun `toString with empty array should return brackets`() {
            val result = StringUtil.toString(arrayOf())
            assertEquals("[]", result)
        }

        @Test
        fun `toString with one element`() {
            val result = StringUtil.toString(arrayOf("a"))
            assertEquals("[a]", result)
        }

        @Test
        fun `toString with multiple elements`() {
            val result = StringUtil.toString(arrayOf("a", "b", "c"))
            assertEquals("[a,b,c]", result)
        }

        @Test
        fun `join with default arguments`() {
            val result = StringUtil.join(arrayOf("x", "y", "z"))
            assertEquals("x, y, z", result)
        }
    }

    @Nested
    @DisplayName("Join 검증")
    inner class JoinValidation {
        @Test
        fun `join with custom separator and wrappers`() {
            val result = StringUtil.join(arrayOf("x", "y", "z"), separator = "-", prefix = "<", postfix = ">")
            assertEquals("<x-y-z>", result)
        }
    }

    @Nested
    @DisplayName("Serializable 검증")
    inner class SerializableValidation {
        @Test
        fun `print Serializable 1`() {
            val person = Person("Alice", 30)

            val prettyJson = Json {
                prettyPrint = true
            }

            val jsonString = prettyJson.encodeToString(person)
            println(jsonString)
        }

        @Test
        fun `print Serializable 2`() {
            val jsonInput = """{"name":"Alice","age":30}"""

            val json = Json { ignoreUnknownKeys = true }
            val obj = json.decodeFromString<Person>(jsonInput)

            val prettyJson = Json {
                prettyPrint = true
            }

            val prettyString = prettyJson.encodeToString(obj)
            println(prettyString)
        }
    }

    @Nested
    @DisplayName("isNullOrEmpty 검증")
    inner class IsNullOrEmptyValidation {

        @Nested
        @DisplayName("null 검증")
        inner class NullValidation {

            @Test
            fun `null 문자열은 true를 반환한다`() {
                val value: String? = null
                assertTrue(value.isNullOrEmpty())
            }

            @Test
            fun `null 문자열은 true를 반환한다 2`() {
                val value: String? = null
                assertTrue(StringUtil.isNullOrEmpty(value))
            }
        }

        @Nested
        @DisplayName("빈 문자열 검증")
        inner class EmptyStringValidation {

            @Test
            fun `빈 문자열은 true를 반환한다`() {
                val value = ""
                assertTrue(value.isNullOrEmpty())
            }
        }

        @Nested
        @DisplayName("공백 문자열 검증")
        inner class WhitespaceValidation {

            @Test
            fun `공백만 있는 문자열은 false를 반환한다`() {
                val value = "   "
                assertFalse(value.isNullOrEmpty())
            }
        }

        @Nested
        @DisplayName("일반 문자열 검증")
        inner class NormalStringValidation {

            @Test
            fun `일반 문자열은 false를 반환한다`() {
                val value = "Hello"
                assertFalse(value.isNullOrEmpty())
            }

            @Test
            fun `숫자 문자열은 false를 반환한다`() {
                val value = "123"
                assertFalse(value.isNullOrEmpty())
            }

            @Test
            fun `한글 문자열은 false를 반환한다`() {
                val value = "안녕하세요"
                assertFalse(value.isNullOrEmpty())
            }
        }
    }

    @Serializable
    data class Person(val name: String, val age: Int)
}
