package com.snc.test.core.timer

import com.snc.zero.core.timer.TickTimer
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TickTimerTest : BaseJUnit5Test() {

    private lateinit var tickTimer: TickTimer
    private lateinit var listener: TickTimer.OnTickTimerListener

    @BeforeEach
    fun setUp() {
        tickTimer = TickTimer()
        listener = mock()
    }

    @AfterEach
    fun tearDown() {
        tickTimer.stop()
    }

    @Test
    fun `start() should call onTick repeatedly`() {
        val latch = CountDownLatch(3)

        // Custom listener that counts ticks
        val countingListener = object : TickTimer.OnTickTimerListener {
            override fun onTick() {
                latch.countDown()
            }
        }

        tickTimer.start(100L, countingListener)

        // Wait up to 500ms for 3 ticks (should be sufficient for 3 x 100ms)
        val completed = latch.await(500, TimeUnit.MILLISECONDS)

        assertTrue(completed, "Expected onTick to be called 3 times within 500ms")
    }

    @Test
    fun `stop() should cancel the timer`() {
        val latch = CountDownLatch(1)

        val countingListener = object : TickTimer.OnTickTimerListener {
            override fun onTick() {
                latch.countDown()
            }
        }

        tickTimer.start(100L, countingListener)
        tickTimer.stop()

        // Wait to ensure no ticks happen after stop
        val triggered = latch.await(300, TimeUnit.MILLISECONDS)

        assertFalse(triggered, "Expected onTick to NOT be called after stop()")
    }

    @Test
    fun `isStopped() should reflect timer state`() {
        assertTrue(tickTimer.isStopped(), "Timer should be stopped initially")

        tickTimer.start(100L, listener)
        assertFalse(tickTimer.isStopped(), "Timer should not be stopped after start")

        tickTimer.stop()
        assertTrue(tickTimer.isStopped(), "Timer should be stopped after stop()")
    }
}
