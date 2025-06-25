package com.snc.zero.color

data class Color(val r: Int, val g: Int, val b: Int) {
    constructor(hex: String) : this(
        hex.removePrefix("#").substring(0, 2).toInt(16),
        hex.removePrefix("#").substring(2, 4).toInt(16),
        hex.removePrefix("#").substring(4, 6).toInt(16)
    )

    fun toHex(): String = "#%02X%02X%02X".format(r, g, b)

    fun toHsl(): HSL {
        val rNorm = r / 255.0
        val gNorm = g / 255.0
        val bNorm = b / 255.0

        val max = maxOf(rNorm, gNorm, bNorm)
        val min = minOf(rNorm, gNorm, bNorm)
        val delta = max - min

        val lightness = (max + min) / 2.0

        val saturation = if (delta == 0.0) {
            0.0
        } else {
            if (lightness < 0.5) {
                delta / (max + min)
            } else {
                delta / (2.0 - max - min)
            }
        }

        val hue = when {
            delta == 0.0 -> 0.0
            max == rNorm -> ((gNorm - bNorm) / delta) % 6.0
            max == gNorm -> (bNorm - rNorm) / delta + 2.0
            else -> (rNorm - gNorm) / delta + 4.0
        } * 60.0

        return HSL(if (hue < 0) hue + 360 else hue, saturation, lightness)
    }
}
