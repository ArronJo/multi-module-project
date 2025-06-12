package com.snc.zero.core.timer

import kotlin.math.max

/**
 * 스톱 워치
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class StopWatch {

    private val timer = mutableListOf<Long>()

    fun start(): Long {
        val startTime = System.currentTimeMillis()
        timer.clear()
        timer.add(startTime)
        return startTime
    }

    fun stop(): Long {
        if (timer.isEmpty()) {
            return -1L
        }
        val endTime = System.currentTimeMillis()
        timer.add(endTime)
        val elapseTime = endTime - timer[0]
        return max(elapseTime, 0)
    }
}
