package com.snc.zero.test.timer

import com.snc.zero.test.extensions.debug.displayInSeconds
import kotlin.math.max

class TestTimer {

    private var startTime: Long = 0

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun stop(): String {
        return max(System.currentTimeMillis() - startTime, 0).displayInSeconds()
    }
}