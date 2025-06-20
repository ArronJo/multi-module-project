package com.snc.test.core.random

import com.snc.zero.core.random.RandomGenerator
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class RandomGeneratorTest : BaseJUnit5Test() {

    @Test
    fun `RandomGenerator random 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.random(12)
        // then
        logger.debug { "RandomGenerator random 1 결과: $v1" }
        //assertEquals(v1, false)

        val v2 = RandomGenerator.random(12, letters = false)
        logger.debug { "RandomGenerator random 2 결과: $v2" }

        val v3 = RandomGenerator.random(12, numbers = false)
        logger.debug { "RandomGenerator random 3 결과: $v3" }

        val v4 = RandomGenerator.random(12, letters = false, numbers = false)
        logger.debug { "RandomGenerator random 4 결과: $v4" }
    }

    @Test
    fun `RandomGenerator randomNumber 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.randomNumber(12)
        // then
        logger.debug { "RandomGenerator randomNumber 1 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `RandomGenerator randomKorean 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.randomKorean(12)
        // then
        logger.debug { "RandomGenerator randomKorean 1 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `RandomGenerator random 커스텀 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.random(
            12,
            0,
            "ABCDEFG".length,
            true,
            false,
            "ABCDEFG".toCharArray(),
            Random()
        )
        // then
        logger.debug { "RandomGenerator random 커스텀 1 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `RandomGenerator random 한글 테스트`() {
        val v1 = RandomGenerator.random(12, 44032, 55203, true, true)
        logger.debug { "RandomGenerator random 한글 1 결과: $v1" }

        val v2 = RandomGenerator.random(12, 44032, 55203, true, false)
        logger.debug { "RandomGenerator random 한글 2 결과: $v2" }

        val v3 = RandomGenerator.random(12, 44032, 55203, false, false)
        logger.debug { "RandomGenerator random 한글 3 결과: $v3" }

        //assertEquals(v1, false)
    }

    @Test
    fun `should generate random string with letters and numbers`() {
        val result = RandomGenerator.random(10, letters = true, numbers = true)
        assertEquals(10, result.length)
        assertTrue(result.all { it.isLetterOrDigit() })
    }

    @Test
    fun `should generate random string with only letters`() {
        val result = RandomGenerator.random(20, letters = true, numbers = false)
        assertEquals(20, result.length)
        assertTrue(result.all { it.isLetter() })
    }

    @Test
    fun `should generate random string with only numbers`() {
        val result = RandomGenerator.random(15, letters = false, numbers = true)
        assertEquals(15, result.length)
        assertTrue(result.all { it.isDigit() })
    }

    @Test
    fun `should generate random string with special chars when letters and numbers are false`() {
        val result = RandomGenerator.random(12, letters = false, numbers = false)
        assertEquals(12, result.length)
        assertTrue(result.any { !it.isLetterOrDigit() })
    }

    @Test
    fun `should generate correct number of Korean characters`() {
        val result = RandomGenerator.randomKorean(5)
        assertEquals(5, result.length)
        assertTrue(result.all { it in '\uAC00'..'\uD7A3' }) // 한글 유니코드 범위
    }

    @Test
    fun `should generate correct number of digits with randomNumber`() {
        val result = RandomGenerator.randomNumber(8)
        assertEquals(8, result.length)
        assertTrue(result.all { it.isDigit() })
    }

    @Test
    fun `should return empty string when length is 0`() {
        val result = RandomGenerator.random(0, letters = true, numbers = true)
        assertEquals("", result)
    }

    @Test
    fun `should throw IllegalArgumentException when length is negative`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            RandomGenerator.random(-1, letters = true, numbers = true)
        }
        assertEquals("Requested random string length -1 is less than 0.", exception.message)
    }

    @Test
    fun `should not include invalid surrogate characters`() {
        val result = RandomGenerator.random(100, letters = true, numbers = false)
        assertFalse(result.any { it.code in 0xDB80..0xDBFF }, "Must not contain invalid surrogate range")
    }

    @Test
    fun `should handle surrogate pairs when applicable`() {
        val result = RandomGenerator.random(100, letters = false, numbers = false)
        // Check surrogate pair integrity (simplified)
        for (i in 0 until result.length - 1) {
            val ch = result[i]
            if (ch.isHighSurrogate()) {
                assertTrue(result[i + 1].isLowSurrogate())
            }
        }
    }

    @Test
    fun `random function skips invalid surrogate range DB80-DBFF`() {
        val invalidSurrogates = CharArray(1) { 0xDB80.toChar() } // 임의로 invalid surrogate 하나만 넣음

        assertThrows<IllegalStateException> {
            val result = RandomGenerator.random(
                countLen = 1,
                startPos = 0,
                endPos = 1,
                letters = false,
                numbers = false,
                chars = invalidSurrogates
            )
            assertEquals(1, result.length) // 요구 길이 충족하되, skip 처리되어 fallback 생성됨
        }
    }

    @Test
    fun `handleLowSurrogate is skipped if only one position left`() {
        val lowSurrogateChar = 0xDC00.toChar() // low surrogate 시작값
        val fixedCharArray = CharArray(1) { lowSurrogateChar }

        assertThrows<IllegalStateException> {
            val result = RandomGenerator.random(
                countLen = 1,
                startPos = 0,
                endPos = 1,
                letters = false,
                numbers = false,
                chars = fixedCharArray
            )

            assertEquals(1, result.length)
        }
    }

    @Test
    fun `handleHighSurrogate is skipped if only one position left`() {
        val highSurrogateChar = 0xD800.toChar() // high surrogate 시작값
        val fixedCharArray = CharArray(1) { highSurrogateChar }

        assertThrows<IllegalStateException> {
            val result = RandomGenerator.random(
                countLen = 1,
                startPos = 0,
                endPos = 1,
                letters = false,
                numbers = false,
                chars = fixedCharArray
            )

            assertEquals(1, result.length)
        }
    }
}
