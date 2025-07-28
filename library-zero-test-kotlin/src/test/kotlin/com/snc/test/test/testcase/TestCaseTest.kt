package com.snc.test.test.testcase

import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.test.testcase.TestCase
import com.snc.zero.test.testcase.givenContext
import com.snc.zero.test.testcase.givenContextMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class TestCaseTest : BaseJUnit5Test() {

    @Nested
    inner class SingleValue() {

        @Test
        fun `TestCase 클래스 테스트 - 싱글 변수 설정`() {
            TestCase.given {
                "a"
            }.`when` { data ->
                data.uppercase()
            }.then { result ->
                assertEquals("A", result)
            }
        }
    }

    @Nested
    inner class MultiValue() {

        @Test
        fun `TestCase 클래스 테스트 - 변수 2개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                givenContext(name, number)
            }.`when` { (name, number) ->
                "$name: $number".uppercase()
            }.then { result ->
                assertEquals("TEST: 42", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 3개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                val sex = "Female"
                givenContext(name, number, sex)
            }.`when` { (name, number, sex) ->
                "$name: $number: $sex".uppercase()
            }.then { result ->
                assertEquals("TEST: 42: FEMALE", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 4개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                val sex = "Female"
                val tall = 185
                givenContext(name, number, sex, tall)
            }.`when` { (name, number, sex, tall) ->
                "$name: $number: $sex $tall".uppercase()
            }.then { result ->
                assertEquals("TEST: 42: FEMALE 185", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 5개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                val sex = "Female"
                val tall = 185
                val weight = 72
                givenContext(name, number, sex, tall, weight)
            }.`when` { (name, number, sex, tall, weight) ->
                "$name: $number: $sex $tall $weight".uppercase()
            }.then { result ->
                assertEquals("TEST: 42: FEMALE 185 72", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 n개 설정`() {
            TestCase.given {
                givenContextMap(
                    "name" to "test",
                    "number" to 42,
                    "active" to true
                )
            }.`when` { context ->
                val name: String = context.get("name")
                val number: Int = context.get("number")
                val active: Boolean = context.get("active")

                if (active) "$name: $number" else "inactive"
            }.then { result ->
                assertEquals("test: 42", result)
            }
        }
    }

    @Nested
    inner class ExceptionCase() {

        @Test
        fun `TestCase 클래스 테스트 - 예외 케이스 1`() {
            TestCase.given {
                "not a number"
            }.`when` { data ->
                data.toInt()
            }.catch { e ->
                assertThrows(NumberFormatException::class.java) { throw e }
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 예외 케이스 2`() {
            TestCase.given {
                "not a number"
            }.`when` { data ->
                val e = assertThrows(NumberFormatException::class.java) {
                    data.toInt()
                }
                e.message!!
            }.then { result ->
                println(result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 실패 케이스 (의도적으로 실패하는 테스트) 1`() {
            try {
                TestCase.given {
                    5
                }.`when` { data ->
                    data * 2
                }.then { result ->
                    assertEquals(11, result)
                }
            } catch (e: AssertionError) {
                println("Test failed as expected: ${e.message}")
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 실패 케이스 (의도적으로 실패하는 테스트) 2`() {
            try {
                TestCase.given {
                    5
                }.catch { e ->
                    println("catch: ${e.message}")
                }
            } catch (e: AssertionError) {
                println("Test failed as expected: ${e.message}")
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 실패 케이스 (의도적으로 실패하는 테스트) 3`() {
            try {
                TestCase.given {
                    5
                }.`when` {
                    throw AssertionError("test test")
                }.catch { e ->
                    println("catch: ${e.message}")
                }
            } catch (e: AssertionError) {
                println("Test failed as expected: ${e.message}")
            }
        }
    }
}
