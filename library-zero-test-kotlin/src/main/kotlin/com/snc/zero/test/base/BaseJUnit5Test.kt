package com.snc.zero.test.base

import com.snc.zero.test.timer.TestTimer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo

open class BaseJUnit5Test {

    private val timer = TestTimer()

    companion object {
        var count = 0

        /*
        @JvmStatic
        @BeforeAll
        fun setupBeforeAll() {
            println()
        }

        @JvmStatic
        @AfterAll
        fun setupAfterAll() {
            println()
        }
         */
    }

    @BeforeEach
    open fun beforeEach(testInfo: TestInfo) {
        println("\nTask Case ${++count} -> ${testInfo.displayName}")
        timer.start()
    }

    @AfterEach
    open fun afterEach(testInfo: TestInfo) {
        println("Task Result $count elapse: ${timer.stop()}")
    }

    fun assertEquals(expected: Any?, actual: Any?) {
        Assertions.assertEquals(expected, actual)
    }

    fun assertNotEquals(unexpected: Any?, actual: Any?) {
        Assertions.assertNotEquals(unexpected, actual)
    }
}