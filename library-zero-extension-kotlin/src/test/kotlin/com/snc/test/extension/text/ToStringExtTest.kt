package com.snc.test.extension.text

import com.snc.zero.extension.text.print
import com.snc.zero.extension.text.printJSON
import com.snc.zero.extension.text.safeSubstring
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
@DisplayName("Kotlin 확장 함수 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToStringExtTest : BaseJUnit5Test() {

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
    fun `Print - CharArray`() {
        // given
        val data = "Aa1가"
        // when
        val v1 = data.toCharArray().print()
        // then
        logger.debug { "Print - CharArray 결과: $v1" }
        assertEquals("[41, 61, 31, ac00]", v1)
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

    @Test
    fun `Print - JSON`() {
        val str =
            "{ \"user_agent\": { \"family\": \"Chrome Mobile WebView\", \"major\": \"56\", \"minor\": \"0\", \"patch\": \"2924\" }, \"os\": { \"Android\", \"major\": \"7\", \"minor\": \"0\", \"patch\": \"\", \"patch_minor\": \"\" }, \"device\": { \"family\": \"Samsung SM-P585N0\" } }"
        println(str.printJSON())
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

    @Nested
    @DisplayName("Any.printJSON() 확장 함수 테스트")
    inner class AnyPrintJSONTest {

        @Nested
        @DisplayName("기본 동작 테스트")
        inner class BasicBehaviorTest {

            @Test
            @DisplayName("빈 문자열 처리")
            fun `빈 문자열은 그대로 반환된다`() {
                // given
                val obj = ""

                // when
                val result = obj.printJSON()

                // then
                assertEquals("", result)
            }

            @Test
            @DisplayName("특수 문자가 없는 일반 문자열")
            fun `특수 문자가 없는 문자열은 그대로 반환된다`() {
                // given
                val obj = "simple text"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("simple text", result)
            }
        }

        @Nested
        @DisplayName("여는 괄호 처리 테스트")
        inner class OpenBracketTest {

            @Test
            @DisplayName("대괄호 [ 처리")
            fun `대괄호는 새 줄과 들여쓰기가 추가된다`() {
                // given
                val obj = "test[content]"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("test[\n  content\n]", result)
            }

            @Test
            @DisplayName("중괄호 { 처리")
            fun `중괄호는 새 줄과 들여쓰기가 추가된다`() {
                // given
                val obj = "test{content}"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("test{\n  content\n}", result)
            }

            @Test
            @DisplayName("중첩된 여는 괄호 처리")
            fun `중첩된 여는 괄호는 들여쓰기 레벨이 증가한다`() {
                // given
                val obj = "{[("

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  [\n    (\n      ", result)
            }
        }

        @Nested
        @DisplayName("닫는 괄호 처리 테스트")
        inner class CloseBracketTest {

            @Test
            @DisplayName("소괄호 ) 처리")
            fun `소괄호 닫기는 새 줄과 들여쓰기 감소가 적용된다`() {
                // given
                val obj = "(content)"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("(\n  content\n)", result)
            }

            @Test
            @DisplayName("대괄호 ] 처리")
            fun `대괄호 닫기는 새 줄과 들여쓰기 감소가 적용된다`() {
                // given
                val obj = "[content]"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("[\n  content\n]", result)
            }

            @Test
            @DisplayName("중괄호 } 처리")
            fun `중괄고 닫기는 새 줄과 들여쓰기 감소가 적용된다`() {
                // given
                val obj = "{content}"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  content\n}", result)
            }

            @Test
            @DisplayName("중첩된 닫는 괄호 처리")
            fun `중첩된 닫는 괄호는 들여쓰기 레벨이 감소한다`() {
                // given
                val obj = "({[test]})"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("(\n  {\n    [\n      test\n    ]\n  }\n)", result)
            }
        }

        @Nested
        @DisplayName("쉼표 처리 테스트")
        inner class CommaTest {

            @Test
            @DisplayName("쉼표 처리")
            fun `쉼표는 새 줄과 들여쓰기가 추가된다`() {
                // given
                val obj = "a,b,c"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("a,\nb,\nc", result)
            }

            @Test
            @DisplayName("쉼표 후 공백 처리")
            fun `쉼표 후 공백은 무시되고 다음 문자까지 건너뛴다`() {
                // given
                val obj = "a,  b, c"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("a,\nb,\nc", result)
            }

            @Test
            @DisplayName("쉼표 후 여러 공백 처리")
            fun `쉼표 후 여러 공백도 모두 건너뛴다`() {
                // given
                val obj = "a,    b,     c"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("a,\nb,\nc", result)
            }
        }

        @Nested
        @DisplayName("nextCharPos 내부 함수 테스트")
        inner class NextCharPosTest {

            @Test
            @DisplayName("쉼표 후 공백 없음")
            fun `쉼표 다음이 바로 문자인 경우`() {
                // given
                val obj = "a,b"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("a,\nb", result)
            }

            @Test
            @DisplayName("여는 괄호 후 공백 없음")
            fun `여는 괄호 다음이 바로 문자인 경우`() {
                // given
                val obj = "{a"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  a", result)
            }

            @Test
            @DisplayName("문자열 끝에 특수 문자가 있는 경우")
            fun `문자열 끝에 특수 문자가 있는 경우 인덱스 처리`() {
                // given
                val obj = "a("

                // when
                val result = obj.printJSON()

                // then
                assertEquals("a(\n  ", result)
            }
        }

        @Nested
        @DisplayName("들여쓰기 옵션 테스트")
        inner class IndentationTest {

            @Test
            @DisplayName("기본 들여쓰기 (2칸)")
            fun `기본 들여쓰기는 2칸이다`() {
                // given
                val obj = "{a}"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  a\n}", result)
            }

            @Test
            @DisplayName("사용자 정의 들여쓰기 크기")
            fun `사용자 정의 들여쓰기 크기를 적용할 수 있다`() {
                // given
                val obj = "{a}"

                // when
                val result = obj.printJSON(indentSize = 4)

                // then
                assertEquals("{\n    a\n}", result)
            }

            @Test
            @DisplayName("들여쓰기 크기 0")
            fun `들여쓰기 크기가 0이면 공백이 추가되지 않는다`() {
                // given
                val obj = "{a}"

                // when
                val result = obj.printJSON(indentSize = 0)

                // then
                assertEquals("{\na\n}", result)
            }

            @Test
            @DisplayName("사용자 정의 패딩 문자")
            fun `사용자 정의 패딩 문자를 사용할 수 있다`() {
                // given
                val obj = "{a}"

                // when
                val result = obj.printJSON(padChar = '-')

                // then
                assertEquals("{\n--a\n}", result)
            }

            @Test
            @DisplayName("들여쓰기와 패딩 문자 조합")
            fun `들여쓰기 크기와 패딩 문자를 함께 사용할 수 있다`() {
                // given
                val obj = "{a}"

                // when
                val result = obj.printJSON(indentSize = 3, padChar = '*')

                // then
                assertEquals("{\n***a\n}", result)
            }
        }

        @Nested
        @DisplayName("복합 시나리오 테스트")
        inner class ComplexScenarioTest {

            @Test
            @DisplayName("복잡한 JSON 구조")
            fun `복잡한 JSON 구조를 올바르게 포맷한다`() {
                // given
                val obj = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  \"name\":\"John\",\n  \"age\":30,\n  \"city\":\"New York\"\n}", result)
            }

            @Test
            @DisplayName("중첩된 배열과 객체")
            fun `중첩된 배열과 객체를 올바르게 포맷한다`() {
                // given
                val obj = "{\"users\":[{\"name\":\"John\"},{\"name\":\"Jane\"}]}"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  \"users\":[\n    {\n      \"name\":\"John\"\n    },\n    {\n      \"name\":\"Jane\"\n    }\n  ]\n}", result)
            }

            @Test
            @DisplayName("모든 괄호 타입이 포함된 경우")
            fun `모든 괄호 타입이 포함된 경우를 처리한다`() {
                // given
                val obj = "func({key:[1,2,3]})"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("func(\n  {\n    key:[\n      1,\n      2,\n      3\n    ]\n  }\n)", result)
            }

            @Test
            @DisplayName("최대 깊이 중첩")
            fun `깊은 중첩 구조를 올바르게 처리한다`() {
                // given
                val obj = "{{{{a}}}}"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  {\n    {\n      {\n        a\n      }\n    }\n  }\n}", result)
            }
        }

        @Nested
        @DisplayName("경계 조건 테스트")
        inner class EdgeCaseTest {

            @Test
            @DisplayName("각 특수 문자 개별 처리")
            fun `각 특수 문자를 개별적으로 처리한다`() {
                // 여는 소괄호
                assertEquals("(\n  ", "(".printJSON())

                // 닫는 소괄호
                assertEquals("\n)", ")".printJSON())

                // 여는 대괄호
                assertEquals("[\n  ", "[".printJSON())

                // 닫는 대괄호
                assertEquals("\n]", "]".printJSON())

                // 여는 중괄호
                assertEquals("{\n  ", "{".printJSON())

                // 닫는 중괄호
                assertEquals("\n}", "}".printJSON())

                // 쉼표
                assertEquals(",\n", ",".printJSON())
            }

            @Test
            @DisplayName("매우 긴 문자열")
            fun `매우 긴 문자열도 올바르게 처리한다`() {
                // given
                val longString = "a".repeat(1000)
                val obj = "{$longString}"

                // when
                val result = obj.printJSON()

                // then
                assertEquals("{\n  $longString\n}", result)
            }

            @Test
            @DisplayName("nextCharPos 경계 조건 - 인덱스 범위 초과")
            fun `nextCharPos에서 인덱스 범위를 초과하는 경우를 처리한다`() {
                // given
                val obj = "a," // 쉼표 다음에 아무것도 없는 경우

                // when
                val result = obj.printJSON()

                // then
                assertEquals("a,\n", result)
            }
        }
    }

    @Nested
    @DisplayName("특수 문자 개별 처리")
    inner class SpecialCharacterIndividualProcessing {

        @Test
        @DisplayName("여는 괄호들을 개별적으로 처리한다")
        fun `여는 괄호들을 개별적으로 처리한다`() {
            // 여는 소괄호
            assertEquals("(\n  ", "(".printJSON())

            // 여는 대괄호
            assertEquals("[\n  ", "[".printJSON())

            // 여는 중괄호
            assertEquals("{\n  ", "{".printJSON())
        }

        @Test
        @DisplayName("닫는 괄호들을 개별적으로 처리한다")
        fun `닫는 괄호들을 개별적으로 처리한다`() {
            // 닫는 소괄호
            assertEquals("\n)", ")".printJSON())

            // 닫는 대괄호
            assertEquals("\n]", "]".printJSON())

            // 닫는 중괄호
            assertEquals("\n}", "}".printJSON())
        }

        @Test
        @DisplayName("쉼표를 개별적으로 처리한다")
        fun `쉼표를 개별적으로 처리한다`() {
            assertEquals(",\n", ",".printJSON())
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    inner class EdgeCaseTests {

        @Test
        @DisplayName("빈 문자열을 처리한다")
        fun `빈 문자열을 처리한다`() {
            assertEquals("", "".printJSON())
        }

        @Test
        @DisplayName("일반 문자를 처리한다")
        fun `일반 문자를 처리한다`() {
            assertEquals("a", "a".printJSON())
            assertEquals("hello", "hello".printJSON())
        }
    }

    @Nested
    @DisplayName("복잡한 케이스 처리")
    inner class ComplexCaseTests {

        @Test
        @DisplayName("실제 출력 확인 및 디버깅")
        fun `실제 출력을 확인한다`() {
            // 실제 출력 값을 확인하기 위한 테스트
            val testCase = "{ \"a\" :  1 , \"b\" :  2 }"
            val result = testCase.printJSON()

            println("Input: '$testCase'")
            println("Output: '$result'")
            println("Expected: '{\n  \"a\" :  1,\n  \"b\" :  2\n}'")

            // 각 문자별 분석
            result.forEachIndexed { index, char ->
                when (char) {
                    '\n' -> println("[$index] = '\\n'")
                    ' ' -> println("[$index] = 'space'")
                    else -> println("[$index] = '$char'")
                }
            }
        }

        @Test
        @DisplayName("indentLevel 음수 방지 확인")
        fun `indentLevel이 음수가 되지 않는다`() {
            // 여러 닫는 괄호가 연속으로 나와도 에러가 발생하지 않아야 함
            assertDoesNotThrow {
                ")))".printJSON()
                "}}}".printJSON()
                "]]]".printJSON()
            }
        }
    }

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
