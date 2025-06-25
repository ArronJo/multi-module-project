package com.snc.zero.color.contrast

import kotlin.math.*

/**
 * WCAG 2.1 기준 색상 대비율 계산 및 자동 조정 클래스
 */
class ColorContrastCalculator {
    companion object {
        const val MIN_CONTRAST_AA = 4.5
        const val MIN_CONTRAST_AAA = 7.0
    }

    /**
     * HEX 색상을 RGB로 변환
     */
    fun hexToRgb(hexColor: String): Triple<Int, Int, Int> {
        val hex = hexColor.removePrefix("#")
        val r = hex.substring(0, 2).toInt(16)
        val g = hex.substring(2, 4).toInt(16)
        val b = hex.substring(4, 6).toInt(16)
        return Triple(r, g, b)
    }

    /**
     * RGB 색상을 HEX로 변환
     */
    fun rgbToHex(rgb: Triple<Int, Int, Int>): String {
        val (r, g, b) = rgb
        return "#%02x%02x%02x".format(r, g, b)
    }

    /**
     * 상대 휘도 계산 (WCAG 공식)
     */
    fun getRelativeLuminance(rgb: Triple<Int, Int, Int>): Double {
        fun gammaCorrect(colorValue: Int): Double {
            val normalized = colorValue / 255.0
            return if (normalized <= 0.03928) {
                normalized / 12.92
            } else {
                ((normalized + 0.055) / 1.055).pow(2.4)
            }
        }

        val (r, g, b) = rgb
        val rLum = gammaCorrect(r)
        val gLum = gammaCorrect(g)
        val bLum = gammaCorrect(b)

        return 0.2126 * rLum + 0.7152 * gLum + 0.0722 * bLum
    }

    /**
     * 두 색상 간의 대비율 계산
     */
    fun calculateContrastRatio(color1: Triple<Int, Int, Int>, color2: Triple<Int, Int, Int>): Double {
        val lum1 = getRelativeLuminance(color1)
        val lum2 = getRelativeLuminance(color2)

        val lighter = maxOf(lum1, lum2)
        val darker = minOf(lum1, lum2)

        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * 지정된 대비율을 만족하도록 색상 자동 조정
     */
    fun adjustColorForContrast(
        baseColorHex: String,
        textColorHex: String = "#ffffff",
        targetRatio: Double = 4.5,
        adjustBackground: Boolean = true
    ): ContrastResult {
        val baseRgb = hexToRgb(baseColorHex)
        val textRgb = hexToRgb(textColorHex)

        val currentRatio = calculateContrastRatio(baseRgb, textRgb)

        if (currentRatio >= targetRatio) {
            return ContrastResult(
                originalColor = baseColorHex,
                textColor = textColorHex,
                adjustedColor = baseColorHex,
                originalRatio = currentRatio.roundTo(2),
                targetRatio = targetRatio,
                adjustmentNeeded = false,
                meetsAA = currentRatio >= 4.5,
                meetsAAA = currentRatio >= 7.0
            )
        }

        val adjustedRgb = if (adjustBackground) {
            adjustBackgroundBrightness(baseRgb, textRgb, targetRatio)
        } else {
            adjustTextBrightness(baseRgb, textRgb, targetRatio)
        }

        val adjustedHex = rgbToHex(adjustedRgb)
        val finalRatio = calculateContrastRatio(
            adjustedRgb,
            if (adjustBackground) textRgb else baseRgb
        )

        return ContrastResult(
            originalColor = baseColorHex,
            textColor = textColorHex,
            adjustedColor = adjustedHex,
            originalRatio = currentRatio.roundTo(2),
            finalRatio = finalRatio.roundTo(2),
            targetRatio = targetRatio,
            adjustmentNeeded = true,
            meetsAA = finalRatio >= 4.5,
            meetsAAA = finalRatio >= 7.0
        )
    }

    /**
     * 배경색의 밝기를 조정하여 목표 대비율 달성
     */
    private fun adjustBackgroundBrightness(
        bgRgb: Triple<Int, Int, Int>,
        textRgb: Triple<Int, Int, Int>,
        targetRatio: Double
    ): Triple<Int, Int, Int> {
        val (r, g, b) = bgRgb
        val hsv = rgbToHsv(r, g, b)

        val textLuminance = getRelativeLuminance(textRgb)

        // 이진 탐색으로 적절한 명도 찾기
        var minV = 0.0
        var maxV = 1.0
        var bestRgb = bgRgb

        repeat(100) {
            val testV = (minV + maxV) / 2
            val testRgb = hsvToRgb(hsv.first, hsv.second, testV)

            val ratio = calculateContrastRatio(testRgb, textRgb)

            if (ratio >= targetRatio + 0.05) {
                bestRgb = testRgb

                if (textLuminance > getRelativeLuminance(testRgb)) {
                    minV = testV
                } else {
                    maxV = testV
                }
            } else {
                if (textLuminance > getRelativeLuminance(testRgb)) {
                    maxV = testV
                } else {
                    minV = testV
                }
            }

            if (abs(maxV - minV) < 0.001) return@repeat
        }

        // 최종 검증
        val finalRatio = calculateContrastRatio(bestRgb, textRgb)
        return if (finalRatio < targetRatio) {
            forceContrastAdjustment(bgRgb, textRgb, targetRatio)
        } else {
            bestRgb
        }
    }

    /**
     * 강제로 대비율을 달성하기 위한 극단적 조정
     */
    private fun forceContrastAdjustment(
        bgRgb: Triple<Int, Int, Int>,
        textRgb: Triple<Int, Int, Int>,
        targetRatio: Double
    ): Triple<Int, Int, Int> {
        val textLuminance = getRelativeLuminance(textRgb)
        val (r, g, b) = bgRgb
        val hsv = rgbToHsv(r, g, b)

        val searchResult = findOptimalBrightness(hsv, textRgb, textLuminance, targetRatio)

        return when {
            searchResult.ratio >= targetRatio -> searchResult.rgb
            else -> getFallbackColor(textLuminance)
        }
    }

    private data class BrightnessSearchResult(
        val rgb: Triple<Int, Int, Int>,
        val ratio: Double
    )

    private fun findOptimalBrightness(
        hsv: Triple<Double, Double, Double>,
        textRgb: Triple<Int, Int, Int>,
        textLuminance: Double,
        targetRatio: Double
    ): BrightnessSearchResult {
        var minV = 0.0
        var maxV = 1.0
        var bestResult = BrightnessSearchResult(
            rgb = hsvToRgb(hsv.first, hsv.second, hsv.third),
            ratio = calculateContrastRatio(hsvToRgb(hsv.first, hsv.second, hsv.third), textRgb)
        )

        repeat(50) {
            val testV = (minV + maxV) / 2
            val testRgb = hsvToRgb(hsv.first, hsv.second, testV)
            val ratio = calculateContrastRatio(testRgb, textRgb)

            if (ratio >= targetRatio) {
                bestResult = updateBestResult(bestResult, testRgb, ratio, targetRatio)
                maxV = adjustMaxBrightness(testV, textLuminance)
            } else {
                minV = adjustMinBrightness(testV, textLuminance)
            }

            if (abs(maxV - minV) < 0.001) {
                return@repeat
            }
        }

        return bestResult
    }

    private fun updateBestResult(
        currentBest: BrightnessSearchResult,
        testRgb: Triple<Int, Int, Int>,
        testRatio: Double,
        targetRatio: Double
    ): BrightnessSearchResult {
        return if (testRatio > currentBest.ratio || currentBest.ratio < targetRatio) {
            BrightnessSearchResult(testRgb, testRatio)
        } else {
            currentBest
        }
    }

    private fun adjustMaxBrightness(testV: Double, textLuminance: Double): Double {
        return if (textLuminance > 0.5) testV else testV
    }

    private fun adjustMinBrightness(testV: Double, textLuminance: Double): Double {
        return if (textLuminance > 0.5) testV else testV
    }

    private fun getFallbackColor(textLuminance: Double): Triple<Int, Int, Int> {
        return if (textLuminance > 0.5) Triple(0, 0, 0) else Triple(255, 255, 255)
    }

    /**
     * 텍스트색의 밝기를 조정하여 목표 대비율 달성
     */
    private fun adjustTextBrightness(
        bgRgb: Triple<Int, Int, Int>,
        textRgb: Triple<Int, Int, Int>,
        targetRatio: Double
    ): Triple<Int, Int, Int> {
        val bgLuminance = getRelativeLuminance(bgRgb)

        return if (bgLuminance > 0.5) {
            // 밝은 배경 -> 어두운 텍스트
            for (brightness in 255 downTo 0) {
                val testRgb = Triple(brightness, brightness, brightness)
                val ratio = calculateContrastRatio(bgRgb, testRgb)
                if (ratio >= targetRatio) {
                    return testRgb
                }
            }
            textRgb
        } else {
            // 어두운 배경 -> 밝은 텍스트
            for (brightness in 0..255) {
                val testRgb = Triple(brightness, brightness, brightness)
                val ratio = calculateContrastRatio(bgRgb, testRgb)
                if (ratio >= targetRatio) {
                    return testRgb
                }
            }
            textRgb
        }
    }

    /**
     * RGB를 HSV로 변환
     */
    private fun rgbToHsv(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
        val rNorm = r / 255.0
        val gNorm = g / 255.0
        val bNorm = b / 255.0

        val max = maxOf(rNorm, gNorm, bNorm)
        val min = minOf(rNorm, gNorm, bNorm)
        val diff = max - min

        val h = when {
            diff == 0.0 -> 0.0
            max == rNorm -> ((gNorm - bNorm) / diff) % 6.0
            max == gNorm -> (bNorm - rNorm) / diff + 2.0
            else -> (rNorm - gNorm) / diff + 4.0
        } * 60.0

        val s = if (max == 0.0) 0.0 else diff / max
        val v = max

        return Triple(h, s, v)
    }

    /**
     * HSV를 RGB로 변환
     */
    private fun hsvToRgb(h: Double, s: Double, v: Double): Triple<Int, Int, Int> {
        val c = v * s
        val x = c * (1 - abs((h / 60.0) % 2 - 1))
        val m = v - c

        val (r1, g1, b1) = when {
            h < 60 -> Triple(c, x, 0.0)
            h < 120 -> Triple(x, c, 0.0)
            h < 180 -> Triple(0.0, c, x)
            h < 240 -> Triple(0.0, x, c)
            h < 300 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }

        val r = ((r1 + m) * 255).roundToInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255).roundToInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255).roundToInt().coerceIn(0, 255)

        return Triple(r, g, b)
    }
}

/**
 * 색상 대비율 조정 결과를 담는 데이터 클래스
 */
data class ContrastResult(
    val originalColor: String,
    val textColor: String,
    val adjustedColor: String,
    val originalRatio: Double,
    val finalRatio: Double? = null,
    val targetRatio: Double,
    val adjustmentNeeded: Boolean,
    val meetsAA: Boolean,
    val meetsAAA: Boolean
)

/**
 * Double 확장 함수: 소수점 반올림
 */
fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}
