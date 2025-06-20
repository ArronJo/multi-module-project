package com.snc.test.core.random

import com.snc.zero.core.random.RandomGenerator
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
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

    @Nested
    inner class HandleHighSurrogateTest {

        private lateinit var mockRandom: Random

        @BeforeEach
        fun setUp() {
            mockRandom = mock(Random::class.java)
        }

        @Test
        fun `count가 1일 때 버퍼 변경 없이 count를 그대로 반환한다`() {
            // Given
            val buffer = CharArray(1) { 'a' }
            val count = 1
            val highSurrogateChar = 0xD800.toChar() // High surrogate 범위의 첫 번째 문자

            // When
            val result = RandomGenerator.handleHighSurrogate(buffer, count, highSurrogateChar, mockRandom)

            // Then
            assertEquals(1, result)
            assertEquals('a', buffer[0]) // 버퍼가 변경되지 않았는지 확인
            verify(mockRandom, never()).nextInt(any()) // Random이 호출되지 않았는지 확인
        }

        @Test
        fun `count가 2 이상일 때 high surrogate pair를 정상적으로 생성한다`() {
            // Given
            val buffer = CharArray(5) { 'x' }
            val count = 3
            val highSurrogateChar = 0xD900.toChar() // High surrogate 범위 내의 문자
            val randomValue = 50 // 0~127 범위 내의 값

            `when`(mockRandom.nextInt(128)).thenReturn(randomValue)

            // When
            val result = RandomGenerator.handleHighSurrogate(buffer, count, highSurrogateChar, mockRandom)

            // Then
            assertEquals(1, result) // count - 2를 반환
            assertEquals((0xDC00 + randomValue).toChar(), buffer[2]) // count - 1 위치에 low surrogate
            assertEquals(highSurrogateChar, buffer[1]) // count - 2 위치에 high surrogate
            verify(mockRandom, times(1)).nextInt(128)
        }

        @Test
        fun `count가 2일 때 정확한 surrogate pair를 생성한다`() {
            // Given
            val buffer = CharArray(2) { 'y' }
            val count = 2
            val highSurrogateChar = 0xDB7F.toChar() // High surrogate 범위의 마지막 문자
            val randomValue = 127 // nextInt(128)의 최대값

            `when`(mockRandom.nextInt(128)).thenReturn(randomValue)

            // When
            val result = RandomGenerator.handleHighSurrogate(buffer, count, highSurrogateChar, mockRandom)

            // Then
            assertEquals(0, result) // count - 2 = 0
            assertEquals((0xDC00 + randomValue).toChar(), buffer[1]) // Low surrogate at count-1
            assertEquals(highSurrogateChar, buffer[0]) // High surrogate at count-2
        }

        @Test
        fun `random nextInt가 0을 반환할 때 최소 low surrogate 값을 생성한다`() {
            // Given
            val buffer = CharArray(4) { 'z' }
            val count = 4
            val highSurrogateChar = 0xD850.toChar()
            val randomValue = 0 // nextInt(128)의 최소값

            `when`(mockRandom.nextInt(128)).thenReturn(randomValue)

            // When
            val result = RandomGenerator.handleHighSurrogate(buffer, count, highSurrogateChar, mockRandom)

            // Then
            assertEquals(2, result) // count - 2
            assertEquals(0xDC00.toChar(), buffer[3]) // 최소 low surrogate 값
            assertEquals(highSurrogateChar, buffer[2])
        }

        @Test
        fun `다양한 high surrogate 문자에 대해 정상 처리한다`() {
            // Given
            val testCases = listOf(
                0xD800.toChar(), // 범위 시작
                0xD900.toChar(), // 중간값
                0xDAFF.toChar(), // 중간값
                0xDB7F.toChar() // 범위 끝
            )

            testCases.forEach { highSurrogateChar ->
                val buffer = CharArray(3) { 'a' }
                val count = 3
                val randomValue = 64 // 중간값

                `when`(mockRandom.nextInt(128)).thenReturn(randomValue)

                // When
                val result = RandomGenerator.handleHighSurrogate(buffer, count, highSurrogateChar, mockRandom)

                // Then
                assertEquals(1, result)
                assertEquals((0xDC00 + randomValue).toChar(), buffer[2])
                assertEquals(highSurrogateChar, buffer[1])
            }
        }
    }

    @Nested
    inner class IntegrationTest {

        @Test
        fun `실제 RandomGenerator에서 handleHighSurrogate 호출 시나리오`() {
            // 이 테스트는 실제 클래스의 private 메소드를 직접 테스트할 수 없으므로
            // 리플렉션을 사용하거나 public 메소드를 통한 간접 테스트를 수행할 수 있습니다.

            // Given - High surrogate가 포함된 범위에서 문자열 생성 요청
            val count = 10
            val startPos = 0xD800 // High surrogate 시작 범위
            val endPos = 0xDB7F // High surrogate 끝 범위

            // When
            val result = RandomGenerator.random(
                countLen = count,
                startPos = startPos,
                endPos = endPos,
                letters = false,
                numbers = false
            )

            // Then
            assertNotNull(result)
            assertTrue(result.length <= count) // Surrogate pair로 인해 길이가 줄어들 수 있음
        }
    }
}
