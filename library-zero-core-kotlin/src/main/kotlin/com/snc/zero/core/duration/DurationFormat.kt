package com.snc.zero.core.duration

import java.time.Duration

class DurationFormat {

    companion object {

        fun formatDuration(ms: Long): String {
            if (ms == 0L) return "0ms"

            val elapsed = Duration.ofMillis(ms)
            val parts = mutableListOf<String>()

            // 각 단위가 0보다 클 때만 리스트에 추가
            elapsed.toHours().takeIf { it > 0 }?.let { parts.add("${it}h") }
            elapsed.toMinutesPart().takeIf { it > 0 }?.let { parts.add("${it}m") }
            elapsed.toSecondsPart().takeIf { it > 0 }?.let { parts.add("${it}s") }
            elapsed.toMillisPart().takeIf { it > 0 }?.let { parts.add("${it}ms") }

            // 리스트의 요소들을 공백으로 구분하여 합침
            return parts.joinToString(" ")
        }
    }
}
