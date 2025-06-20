package com.snc.test.extension.text

import com.snc.zero.extension.text.safeSubstring
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("NonAsciiCharacters")
@DisplayName("Kotlin 확장 함수 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StringExtTest : BaseJUnit5Test() {

    @Nested
    inner class SafeSubstringTest {

        @Nested
        @DisplayName("정상 범위 처리")
        inner class NormalRangeProcessing {

            @Test
            @DisplayName("정상적인 startIndex와 endIndex로 substring을 반환한다")
            fun `정상적인 startIndex와 endIndex로 substring을 반환한다`() {
                // given
                val text = "Hello World"

                // when & then
                assertEquals("Hello", text.safeSubstring(0, 5))
                assertEquals("World", text.safeSubstring(6, 11))
                assertEquals("llo W", text.safeSubstring(2, 7))
            }

            @Test
            @DisplayName("endIndex 생략 시 문자열 끝까지 반환한다")
            fun `endIndex 생략 시 문자열 끝까지 반환한다`() {
                // given
                val text = "Hello World"

                // when & then
                assertEquals("Hello World", text.safeSubstring(0))
                assertEquals("World", text.safeSubstring(6))
                assertEquals("d", text.safeSubstring(10))
            }

            @Test
            @DisplayName("startIndex와 endIndex가 같을 때 빈 문자열을 반환한다")
            fun `startIndex와 endIndex가 같을 때 빈 문자열을 반환한다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("", text.safeSubstring(0, 0))
                assertEquals("", text.safeSubstring(3, 3))
                assertEquals("", text.safeSubstring(5, 5))
            }
        }

        @Nested
        @DisplayName("음수 인덱스 처리")
        inner class NegativeIndexProcessing {

            @Test
            @DisplayName("음수 startIndex는 0으로 보정된다")
            fun `음수 startIndex는 0으로 보정된다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("Hello", text.safeSubstring(-1))
                assertEquals("Hello", text.safeSubstring(-10))
                assertEquals("Hel", text.safeSubstring(-5, 3))
            }

            @Test
            @DisplayName("음수 endIndex는 startIndex로 보정된다")
            fun `음수 endIndex는 startIndex로 보정된다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("", text.safeSubstring(2, -1))
                assertEquals("", text.safeSubstring(0, -5))
                assertEquals("", text.safeSubstring(3, -2))
            }

            @Test
            @DisplayName("startIndex와 endIndex가 모두 음수일 때 빈 문자열을 반환한다")
            fun `startIndex와 endIndex가 모두 음수일 때 빈 문자열을 반환한다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("", text.safeSubstring(-5, -3))
                assertEquals("", text.safeSubstring(-1, -1))
                assertEquals("", text.safeSubstring(-10, -2))
            }
        }

        @Nested
        @DisplayName("범위 초과 인덱스 처리")
        inner class OutOfBoundsIndexProcessing {

            @Test
            @DisplayName("startIndex가 문자열 길이를 초과하면 빈 문자열을 반환한다")
            fun `startIndex가 문자열 길이를 초과하면 빈 문자열을 반환한다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("", text.safeSubstring(5))
                assertEquals("", text.safeSubstring(10))
                assertEquals("", text.safeSubstring(100, 200))
            }

            @Test
            @DisplayName("endIndex가 문자열 길이를 초과하면 문자열 끝까지 반환한다")
            fun `endIndex가 문자열 길이를 초과하면 문자열 끝까지 반환한다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("Hello", text.safeSubstring(0, 10))
                assertEquals("ello", text.safeSubstring(1, 100))
                assertEquals("o", text.safeSubstring(4, 50))
            }

            @Test
            @DisplayName("startIndex와 endIndex가 모두 범위를 초과하면 빈 문자열을 반환한다")
            fun `startIndex와 endIndex가 모두 범위를 초과하면 빈 문자열을 반환한다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("", text.safeSubstring(10, 15))
                assertEquals("", text.safeSubstring(6, 8))
                assertEquals("", text.safeSubstring(100, 200))
            }
        }

        @Nested
        @DisplayName("역순 인덱스 처리")
        inner class ReverseIndexProcessing {

            @Test
            @DisplayName("startIndex가 endIndex보다 클 때 빈 문자열을 반환한다")
            fun `startIndex가 endIndex보다 클 때 빈 문자열을 반환한다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("", text.safeSubstring(3, 1))
                assertEquals("", text.safeSubstring(5, 2))
                assertEquals("", text.safeSubstring(4, 0))
            }

            @Test
            @DisplayName("범위를 초과하는 역순 인덱스도 안전하게 처리한다")
            fun `범위를 초과하는 역순 인덱스도 안전하게 처리한다`() {
                // given
                val text = "Hello"

                // when & then
                assertEquals("", text.safeSubstring(10, 5))
                assertEquals("", text.safeSubstring(15, 3))
                assertEquals("", text.safeSubstring(100, 50))
            }
        }

        @Nested
        @DisplayName("경계값 테스트")
        inner class EdgeCaseTests {

            @Test
            @DisplayName("빈 문자열에 대해 안전하게 처리한다")
            fun `빈 문자열에 대해 안전하게 처리한다`() {
                // given
                val text = ""

                // when & then
                assertEquals("", text.safeSubstring(0))
                assertEquals("", text.safeSubstring(-1))
                assertEquals("", text.safeSubstring(1))
                assertEquals("", text.safeSubstring(0, 1))
                assertEquals("", text.safeSubstring(-5, 5))
            }

            @Test
            @DisplayName("단일 문자 문자열을 안전하게 처리한다")
            fun `단일 문자 문자열을 안전하게 처리한다`() {
                // given
                val text = "A"

                // when & then
                assertEquals("A", text.safeSubstring(0))
                assertEquals("A", text.safeSubstring(0, 1))
                assertEquals("", text.safeSubstring(1))
                assertEquals("", text.safeSubstring(0, 0))
                assertEquals("A", text.safeSubstring(-1, 5))
            }

            @Test
            @DisplayName("전체 문자열 범위로 호출할 때 원본과 동일하다")
            fun `전체 문자열 범위로 호출할 때 원본과 동일하다`() {
                // given
                val text = "Hello World"

                // when & then
                assertEquals(text, text.safeSubstring(0, text.length))
                assertEquals(text, text.safeSubstring(0))
                assertEquals(text, text.safeSubstring(-1, text.length + 10))
            }
        }

        @Nested
        @DisplayName("극단적인 값 처리")
        inner class ExtremeValueTests {

            @Test
            @DisplayName("매우 큰 인덱스 값도 안전하게 처리한다")
            fun `매우 큰 인덱스 값도 안전하게 처리한다`() {
                // given
                val text = "Test"

                // when & then
                assertDoesNotThrow {
                    assertEquals("", text.safeSubstring(Int.MAX_VALUE))
                    assertEquals("", text.safeSubstring(Int.MAX_VALUE, Int.MAX_VALUE))
                    assertEquals("Test", text.safeSubstring(0, Int.MAX_VALUE))
                }
            }

            @Test
            @DisplayName("매우 작은 인덱스 값도 안전하게 처리한다")
            fun `매우 작은 인덱스 값도 안전하게 처리한다`() {
                // given
                val text = "Test"

                // when & then
                assertDoesNotThrow {
                    assertEquals("Test", text.safeSubstring(Int.MIN_VALUE))
                    assertEquals("", text.safeSubstring(Int.MIN_VALUE, Int.MIN_VALUE))
                    assertEquals("Test", text.safeSubstring(Int.MIN_VALUE, Int.MAX_VALUE))
                }
            }
        }

        @Nested
        @DisplayName("실제 사용 시나리오")
        inner class RealWorldScenarios {

            @Test
            @DisplayName("파일 확장자 추출 시나리오")
            fun `파일 확장자 추출 시나리오에서 안전하게 동작한다`() {
                // given
                val fileName1 = "document.txt"
                val fileName2 = "image"
                val fileName3 = ".hidden"

                // when & then
                val lastDotIndex1 = fileName1.lastIndexOf('.')
                assertEquals("txt", fileName1.safeSubstring(lastDotIndex1 + 1))

                val lastDotIndex2 = fileName2.lastIndexOf('.')
                assertEquals("image", fileName2.safeSubstring(lastDotIndex2 + 1)) // -1 + 1 = 0

                val lastDotIndex3 = fileName3.lastIndexOf('.')
                assertEquals("hidden", fileName3.safeSubstring(lastDotIndex3 + 1))
            }

            @Test
            @DisplayName("문자열 파싱 시나리오")
            fun `문자열 파싱 시나리오에서 안전하게 동작한다`() {
                // given
                val data = "name:John,age:30"
                val colonIndex = data.indexOf(':')
                val commaIndex = data.indexOf(',')

                // when & then
                assertEquals("name", data.safeSubstring(0, colonIndex))
                assertEquals("John", data.safeSubstring(colonIndex + 1, commaIndex))
                assertEquals("age:30", data.safeSubstring(commaIndex + 1))
            }

            @Test
            @DisplayName("잘못된 인덱스로 파싱할 때도 예외가 발생하지 않는다")
            fun `잘못된 인덱스로 파싱할 때도 예외가 발생하지 않는다`() {
                // given
                val data = "no-separator-here"
                val separatorIndex = data.indexOf(':') // -1 반환

                // when & then
                assertDoesNotThrow {
                    assertEquals("no-separator-here", data.safeSubstring(separatorIndex))
                    assertEquals("", data.safeSubstring(separatorIndex, separatorIndex)) // -1, -1
                }
            }
        }
    }
}
