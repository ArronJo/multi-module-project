package com.snc.zero.test.base

import com.snc.zero.test.timer.TestTimer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.function.Executable
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

    fun assertAll(vararg executables: Executable) {
        Assertions.assertAll(*executables)
    }

    fun assertEquals(expected: Any?, actual: Any?) {
        Assertions.assertEquals(expected, actual)
    }

    fun <T : Enum<T>> assertEquals(expected: Enum<T>?, actual: Enum<T>?) {
        Assertions.assertEquals(expected, actual)
    }

    fun assertEquals(expected: Objects?, actual: Objects?) {
        Assertions.assertEquals(expected, actual)
    }

    fun <T> assertEquals(expected: Array<T>?, actual: Array<T>?) {
        if (expected != null && actual != null) {
            for (i in expected.indices) {
                Assertions.assertEquals(expected[i], actual[i])
            }
        } else {
            Assertions.assertEquals(expected, actual)
        }
    }

    fun assertEquals(expected: Double?, actual: Double?) {
        Assertions.assertEquals(expected, actual)
    }

    fun assertEquals(expected: Int?, actual: Int?) {
        Assertions.assertEquals(expected, actual)
    }

    fun assertEquals(expected: String?, actual: String?) {
        Assertions.assertEquals(expected, actual)
    }

    fun assertEquals(expected: Char?, actual: Char?) {
        Assertions.assertEquals(expected, actual)
    }

    fun assertEquals(expected: Boolean?, actual: Boolean?) {
        Assertions.assertEquals(expected, actual)
    }

    fun assertEquals(expected: Int, actual: Int, message: String?) {
        Assertions.assertEquals(expected, actual, message)
    }

    fun assertNotEquals(unexpected: Any?, actual: Any?) {
        Assertions.assertNotEquals(unexpected, actual)
    }

    fun assertTrue(condition: Boolean) {
        Assertions.assertTrue(condition)
    }

    fun assertFalse(condition: Boolean) {
        Assertions.assertFalse(condition)
    }

    fun assertNull(actual: Any?) {
        Assertions.assertNull(actual)
    }

    fun assertNotNull(actual: Any?) {
        Assertions.assertNotNull(actual)
    }

    fun assertDoesNotThrow(executable: () -> Unit) {
        Assertions.assertDoesNotThrow(executable)
    }

    fun <T : Throwable?> assertThrows(expectedType: Class<T>?, executable: Executable?): T {
        return Assertions.assertThrows(expectedType, executable)
    }

    private val random = SplittableRandom()

    protected fun getRandomInt(min: Int = 0, max: Int): Int {
        return random.nextInt(max - min + 1) + min
    }
}
