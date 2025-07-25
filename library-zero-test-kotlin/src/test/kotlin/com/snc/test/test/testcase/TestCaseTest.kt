package com.snc.test.test.testcase

import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.test.testcase.TestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class TestCaseTest : BaseJUnit5Test() {

    @Test
    fun `TestCase 클래스 테스트 - 성공 1`() {
        TestCase.given {
            "a"
        }.`when` { data ->
            data.uppercase()
        }.then { result ->
            assertEquals("A", result)
        }
    }

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
