package com.snc.zero.core.timer

import java.util.TimerTask
import java.util.Timer

/**
 * Tick Timer
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class TickTimer {

    private var tickTimer: Timer? = null

    fun start(timeInMillis: Long, listener: OnTickTimerListener) {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                listener.onTick()
            }
        }
        if (null == tickTimer) {
            tickTimer = Timer()
        }
        tickTimer?.schedule(timerTask, timeInMillis, timeInMillis)
    }

    fun stop() {
        tickTimer?.cancel()
        tickTimer = null
    }

    fun isStopped(): Boolean {
        return null == tickTimer
    }

    interface OnTickTimerListener {
        fun onTick()
    }
}
