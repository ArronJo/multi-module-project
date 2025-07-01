package com.snc.test.extension.text

import com.snc.zero.extensions.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Calendar

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
@DisplayName("Kotlin 확장 함수 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrintExtTest : BaseJUnit5Test() {

    @Test
    fun `Print - Array`() {
        // given
        val data = mutableListOf<String>()
        data.add("A")
        data.add("a")
        data.add("1")
        data.add("가")
        // when
        val v1 = data.toTypedArray().print()
        // then
        logger.debug { "Print - Array 결과: $v1" }
        assertEquals("[A, a, 1, 가]", v1)
    }

    @Test
    fun `Print - CharArray 1`() {
        // given
        val data = "Aa1가"
        // when
        val v1 = data.toCharArray().print()
        // then
        logger.debug { "Print - CharArray 결과: $v1" }
        assertEquals("[41, 61, 31, ac00]", v1)
    }

    @Test
    fun `Print - CharArray 2`() {
        // given
        val data = charArrayOf(0x60.toChar(), 0xAC00.toChar())
        // when
        val v1 = data.print()
        // then
        logger.debug { "Print - CharArray 결과: $v1" }
        //assertEquals("[41, 61, 31, ac00]", v1)
    }

    @Test
    fun `Print - ByteArray`() {
        // given
        val data = "Aa1가"
        // when
        val v1 = data.toByteArray().print()
        // then
        logger.debug { "Print - ByteArray 결과: $v1" }
        assertEquals("[41, 61, 31, ea, b0, 80]", v1)
    }

    @Test
    fun `Print - IntArray 1`() {
        // given
        val data = intArrayOf(1, 2, 3, 4)
        // when
        val v1 = data.print()
        // then
        logger.debug { "Print - IntArray 결과: $v1" }
        assertEquals("[1, 2, 3, 4]", v1)
    }

    @Test
    fun `Print - IntArray 2`() {
        // given
        val data = intArrayOf()
        // when
        val v1 = data.print()
        // then
        logger.debug { "Print - IntArray 결과: $v1" }
        assertEquals("[]", v1)
    }

    @Test
    fun `Print - Calendar`() {
        // given
        val data = Calendar.getInstance()
        // when
        val v1 = data.print()
        // then
        logger.debug { "Print - Calendar 결과: $v1" }
    }

    @Nested
    @DisplayName("CharArray.print() 확장 함수 테스트")
    inner class CharArrayPrintTest {

        @Test
        @DisplayName("빈 배열 처리")
        fun `빈 CharArray는 빈 대괄호를 반환한다`() {
            // given
            val charArray = charArrayOf()

            // when
            val result = charArray.print()

            // then
            assertEquals("[]", result)
        }

        @Test
        @DisplayName("ASCII 범위 문자들 (0-127) 처리")
        fun `ASCII 범위 문자들은 2자리 16진수로 포맷된다`() {
            // given
            val charArray = charArrayOf('A', 'B', 'C') // 65, 66, 67

            // when
            val result = charArray.print()

            // then
            assertEquals("[41, 42, 43]", result)
        }

        @Test
        @DisplayName("ASCII 범위 경계값 테스트 - 0")
        fun `ASCII 범위 최소값 0은 2자리 16진수로 포맷된다`() {
            // given
            val charArray = charArrayOf('\u0000') // code 0

            // when
            val result = charArray.print()

            // then
            assertEquals("[00]", result)
        }

        @Test
        @DisplayName("ASCII 범위 경계값 테스트 - 127")
        fun `ASCII 범위 최대값 127은 2자리 16진수로 포맷된다`() {
            // given
            val charArray = charArrayOf('\u007F') // code 127

            // when
            val result = charArray.print()

            // then
            assertEquals("[7f]", result)
        }

        @Test
        @DisplayName("ASCII 범위 밖 문자들 (128 이상) 처리")
        fun `ASCII 범위를 벗어난 문자들은 4자리 16진수로 포맷된다`() {
            // given
            val charArray = charArrayOf('가', '나', '다') // 한글 문자들

            // when
            val result = charArray.print()

            // then
            assertEquals("[ac00, b098, b2e4]", result)
        }

        @Test
        @DisplayName("ASCII 범위 경계값 테스트 - 128")
        fun `ASCII 범위를 벗어난 최소값 128은 4자리 16진수로 포맷된다`() {
            // given
            val charArray = charArrayOf('\u0080') // code 128

            // when
            val result = charArray.print()

            // then
            assertEquals("[0080]", result)
        }

        @Test
        @DisplayName("ASCII와 non-ASCII 문자 혼합")
        fun `ASCII와 non-ASCII 문자가 혼합된 경우 각각 다른 포맷으로 출력된다`() {
            // given
            val charArray = charArrayOf('A', '가', 'B', '나')

            // when
            val result = charArray.print()

            // then
            assertEquals("[41, ac00, 42, b098]", result)
        }

        @Test
        @DisplayName("단일 문자 처리")
        fun `단일 문자도 올바르게 포맷된다`() {
            // given
            val charArray = charArrayOf('Z')

            // when
            val result = charArray.print()

            // then
            assertEquals("[5a]", result)
        }

        @Test
        @DisplayName("특수 문자 처리")
        fun `특수 문자들도 올바르게 처리된다`() {
            // given
            val charArray = charArrayOf('\n', '\t', ' ', '!', '@')

            // when
            val result = charArray.print()

            // then
            assertEquals("[0a, 09, 20, 21, 40]", result)
        }
    }
}
