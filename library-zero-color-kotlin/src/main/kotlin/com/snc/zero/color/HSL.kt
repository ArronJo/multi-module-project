package com.snc.zero.color

import kotlin.math.abs
import kotlin.math.round

/**
 * H(Hue, 색조), S(Saturation, 채도), L(Lightness, 명도)
 * 세 가지 요소로 표현하는 색상 모델
 */
data class HSL(val h: Double, val s: Double, val l: Double) {
    fun toColor(): Color {
        val c = (1.0 - abs(2.0 * l - 1.0)) * s
        val x = c * (1.0 - abs((h / 60.0) % 2.0 - 1.0))
        val m = l - c / 2.0

        val (rPrime, gPrime, bPrime) = when ((h / 60.0).toInt()) {
            0 -> Triple(c, x, 0.0)
            1 -> Triple(x, c, 0.0)
            2 -> Triple(0.0, c, x)
            3 -> Triple(0.0, x, c)
            4 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }

        return Color(
            round((rPrime + m) * 255).toInt().coerceIn(0, 255),
            round((gPrime + m) * 255).toInt().coerceIn(0, 255),
            round((bPrime + m) * 255).toInt().coerceIn(0, 255)
        )
    }
}
