package com.snc.test.test.timer

import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.test.testcase.TestCase
import com.snc.zero.test.timer.TestTimer
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class TestTimerTest : BaseJUnit5Test() {

    @Test
    fun `TestTimer 테스트 - 1`() {
        val t = TestTimer()
        TestCase.create<Unit, String, Unit>()
            .given {
                t.start()
            }.whens {
                t.stop()
            }.then { result ->
                println("TestTimer: $result")
                assertNotEquals("", result)
            }
    }
}