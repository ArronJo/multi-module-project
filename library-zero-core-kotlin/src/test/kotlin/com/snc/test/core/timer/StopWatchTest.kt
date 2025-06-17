package com.snc.test.core.timer

import com.snc.zero.core.timer.StopWatch
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class StopWatchTest : BaseJUnit5Test() {

    companion object {

        private lateinit var timer: StopWatch

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            timer = StopWatch()
        }
    }

    @Test
    fun `스톱워치 테스트 1`() {
        // given
        timer.start()
        Thread.sleep(100)
        // when
        val v1 = timer.stop()
        // then
        logger.debug { "스톱워치 결과 1 : $v1" }
    }

    @Test
    fun `스톱워치 테스트 2`() {
        // given
        timer.start()
        Thread.sleep(100)
        // when
        val v1 = timer.stop()
        Thread.sleep(100)
        val v2 = timer.stop()
        Thread.sleep(100)
        val v3 = timer.stop()
        Thread.sleep(100)
        val v4 = timer.stop()
        Thread.sleep(100)
        val v5 = timer.stop()
        // then
        logger.debug { "스톱워치 결과 2-1 : $v1" }
        logger.debug { "스톱워치 결과 2-2 : $v2" }
        logger.debug { "스톱워치 결과 2-3 : $v3" }
        logger.debug { "스톱워치 결과 2-4 : $v4" }
        logger.debug { "스톱워치 결과 2-5 : $v5" }
    }

    @Test
    fun `스톱워치 테스트 - 예외 테스트`() {
        val timer = StopWatch()
        val r = timer.stop()
        assertEquals(-1L, r)
    }
}
