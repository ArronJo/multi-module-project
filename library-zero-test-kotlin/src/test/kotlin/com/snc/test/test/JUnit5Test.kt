package com.snc.test.test

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.*

@Suppress("NonAsciiCharacters")
class JUnit5Test : BaseJUnit5Test() {

    companion object {
        var taskCount = 0

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            println("Before Class : $taskCount")
        }

        @JvmStatic
        @AfterAll
        fun afterClass() {
            println("After Class : $taskCount")
        }
    }

    @BeforeEach
    fun beforeEach() {
        taskCount++
        println("Before Each : $taskCount")
    }

    @AfterEach
    fun afterEach() {
        println("After Each : $taskCount")
    }

    @Test
    fun `Test 라이브러리가 잘 동작하는지 확인한다 1`() {
        println("Case 1 : Test 라이브러리가 잘 동작하는지 확인한다")
    }

    @Test
    fun `Test 라이브러리가 잘 동작하는지 확인한다 2`() {
        println("Case 2 : Test 라이브러리가 잘 동작하는지 확인한다")
    }
}
