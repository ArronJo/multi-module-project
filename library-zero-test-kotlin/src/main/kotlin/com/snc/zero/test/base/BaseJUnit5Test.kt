package com.snc.zero.test.base

import com.snc.zero.test.timer.TestTimer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import java.util.*

/**
 * 각 Class 실행 실행시
 * @JvmStatic
 * @BeforeAll
 * fun beforeClass() {
 *  println("Before Class : $count")
 * }
 *
 * 각 Class 실행 종료시
 * @JvmStatic
 * @AfterAll
 * fun afterClass() {
 *  println("After Class : $count")
 * }
 */
open class BaseJUnit5Test {

    private val timer = TestTimer()

    companion object {
        var count = 0
    }

    // 각 TestCase 실행 전
    @BeforeEach
    open fun beforeEach(testInfo: TestInfo) {
        println("\n[S] Task Case ${++count} -> ${testInfo.displayName}")
        timer.start()
    }

    // 각 TestCase 실행 전
    @AfterEach
    open fun afterEach(testInfo: TestInfo) {
        println("[E] Task Result $count elapse: ${timer.stop()}\n\n")
    }

    private val random = SplittableRandom()

    protected fun getRandomInt(min: Int = 0, max: Int): Int {
        return random.nextInt(max - min + 1) + min
    }
}
