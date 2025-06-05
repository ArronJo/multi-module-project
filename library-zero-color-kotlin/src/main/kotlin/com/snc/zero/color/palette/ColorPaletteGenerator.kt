package com.snc.zero.color.palette
import kotlin.math.*

data class Color(val r: Int, val g: Int, val b: Int) {
    constructor(hex: String) : this(
        hex.substring(1, 3).toInt(16),
        hex.substring(3, 5).toInt(16),
        hex.substring(5, 7).toInt(16)
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

class ColorPaletteGenerator {

    /**
     * 기준 색상(500)으로부터 100~900 단계의 컬러 팔레트를 생성합니다.
     * @param baseColor 500 단계에 해당하는 기준 색상
     * @return 100부터 900까지의 컬러 팔레트 맵
     */
    fun generatePalette(baseColor: Color): Map<Int, Color> {
        val baseHsl = baseColor.toHsl()
        val palette = mutableMapOf<Int, Color>()

        // 각 단계별 명도 조절 비율
        val lightnessMultipliers = mapOf(
            100 to 0.95, // 가장 밝음
            200 to 0.85,
            300 to 0.75,
            400 to 0.65,
            500 to 1.0, // 기준값
            600 to 0.85,
            700 to 0.70,
            800 to 0.55,
            900 to 0.40 // 가장 어두움
        )

        // 각 단계별 채도 조절 비율
        val saturationMultipliers = mapOf(
            100 to 0.20, // 채도 낮음
            200 to 0.40,
            300 to 0.60,
            400 to 0.80,
            500 to 1.0, // 기준값
            600 to 1.0,
            700 to 1.0,
            800 to 0.95,
            900 to 0.90 // 채도 약간 낮음
        )

        for (step in 100..900 step 100) {
            val lightnessMultiplier = lightnessMultipliers[step] ?: 1.0
            val saturationMultiplier = saturationMultipliers[step] ?: 1.0

            val newLightness = when {
                step < 500 -> {
                    // 밝은 단계: 명도를 높임
                    val factor = (500 - step) / 400.0
                    baseHsl.l + (1.0 - baseHsl.l) * factor * lightnessMultiplier
                }
                step > 500 -> {
                    // 어두운 단계: 명도를 낮춤
                    val factor = (step - 500) / 400.0
                    baseHsl.l * (1.0 - factor * (1.0 - lightnessMultiplier))
                }
                else -> baseHsl.l // 500 단계는 기준값 유지
            }

            val newSaturation = (baseHsl.s * saturationMultiplier).coerceIn(0.0, 1.0)
            val adjustedHsl = HSL(baseHsl.h, newSaturation, newLightness.coerceIn(0.0, 1.0))

            palette[step] = adjustedHsl.toColor()
        }

        return palette
    }

    /**
     * 팔레트를 보기 좋게 출력합니다. (색상 미리보기 포함)
     */
    fun printPalette(palette: Map<Int, Color>) {
        println("컬러 팔레트:")
        println("단계\t|\tHEX\t\t|\tRGB\t\t\t|\t색상 미리보기")
        println("--------|---------------|-----------------------|------------------")

        for (step in 100..900 step 100) {
            val color = palette[step]
            color?.let {
                val colorPreview = getColorPreview(it)
                println("$step\t|\t${color.toHex()}\t|\tRGB(${color.r}, ${color.g}, ${color.b})\t|\t$colorPreview")
            }
        }
    }

    /**
     * 팔레트를 컬러 블록으로 시각적으로 출력합니다.
     */
    fun printPaletteBlocks(palette: Map<Int, Color>) {
        println("\n컬러 팔레트 블록:")

        // 상단 단계 표시
        print("     ")
        for (step in 100..900 step 100) {
            print("  $step  ")
        }
        println()

        // 컬러 블록 출력 (여러 줄로)
        (0..2).forEach { row ->
            print("     ")
            for (step in 100..900 step 100) {
                val color = palette[step]
                color?.let {
                    print(getColorBlock(it))
                }
            }
            println()
        }

        // HEX 값 표시
        print("     ")
        for (step in 100..900 step 100) {
            val color = palette[step]
            color?.let {
                val hex = it.toHex()
                print(" $hex")
            }
        }
        println()
    }

    /**
     * ANSI 색상 코드로 색상 미리보기를 생성합니다.
     */
    private fun getColorPreview(color: Color): String {
        val bgCode = "\u001B[48;2;${color.r};${color.g};${color.b}m"
        val resetCode = "\u001B[0m"
        val textColor = if (isLightColor(color)) "\u001B[30m" else "\u001B[37m" // 검은색 또는 흰색 텍스트

        return "$bgCode$textColor  ${color.toHex()}  $resetCode"
    }

    /**
     * 컬러 블록을 생성합니다.
     */
    private fun getColorBlock(color: Color): String {
        val bgCode = "\u001B[48;2;${color.r};${color.g};${color.b}m"
        val resetCode = "\u001B[0m"

        return "$bgCode      $resetCode"
    }

    /**
     * 색상이 밝은지 어두운지 판단합니다. (텍스트 색상 결정용)
     */
    private fun isLightColor(color: Color): Boolean {
        // 상대적 휘도 계산 (W3C 공식)
        val luminance = (0.299 * color.r + 0.587 * color.g + 0.114 * color.b) / 255
        return luminance > 0.5
    }
}
