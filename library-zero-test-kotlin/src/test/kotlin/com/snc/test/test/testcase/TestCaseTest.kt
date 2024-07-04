package com.snc.test.test.testcase

import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.test.testcase.TestCase
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class TestCaseTest : BaseJUnit5Test() {

    @Test
    fun `TestCase 클래스 테스트 - 성공 1`() {
        TestCase.create<String, String, Unit>()
            .given {
                "a"
            }.whens { data ->
                data.uppercase()
            }.then { result ->
                assertEquals("A", result)
            }
    }

    @Test
    fun `TestCase 클래스 테스트 - 예외 케이스 1`() {
        TestCase.create<String, Int, Unit>()
            .given {
                "not a number"
            }.whens { data ->
                data.toInt()
            }.catch { e ->
                assertThrows(NumberFormatException::class.java) { throw e }
            }
    }

    @Test
    fun `TestCase 클래스 테스트 - 예외 케이스 2`() {
        TestCase.create<String, Any, Unit>()
            .given {
                "not a number"
            }.whens { data ->
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
            TestCase.create<Int, Int, Unit>()
                .given {
                    5
                }.whens { data ->
                    data * 2
                }.then { result ->
                    assertEquals(11, result)
                }
        } catch (e: AssertionError) {
            println("Test 3 failed as expected: ${e.message}")
        }
    }
}