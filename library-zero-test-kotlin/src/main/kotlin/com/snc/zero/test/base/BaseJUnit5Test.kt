package com.snc.zero.test.base

import com.snc.zero.test.timer.TestTimer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import java.util.SplittableRandom

/**
 * 각 Class 실행 실행시
 * @JvmStatic
 * @BeforeAll
 * fun beforeClass() {
 *  println("Before Class : $count")
 * }
 *
 * 각 Class 실행 종료시
 * @JvmStatic
 * @AfterAll
 * fun afterClass() {
 *  println("After Class : $count")
 * }
 */
open class BaseJUnit5Test {

    private val timer = TestTimer()

    companion object {
        var count = 0
    }

    // 각 TestCase 실행 전
    @BeforeEach
    open fun beforeEach(testInfo: TestInfo) {
        println("\n[S] Task Case ${++count} -> ${testInfo.displayName}")
        timer.start()
    }

    // 각 TestCase 실행 전
    @AfterEach
    open fun afterEach(testInfo: TestInfo) {
        println("[E] Task Result $count elapse: ${timer.stop()}\n\n")
    }

    private val random = SplittableRandom()

    protected fun getRandomInt(min: Int = 0, max: Int): Int {
        return random.nextInt(max - min + 1) + min
    }

    data class RGB(val r: Int, val g: Int, val b: Int) {
        fun toHex(): String = "#%02X%02X%02X".format(r, g, b)
    }

    fun String.toRGB(): RGB {
        val hex = this.removePrefix("#")
        require(hex.length == 6) { "Hex string must be 6 characters" }
        val r = hex.substring(0, 2).toInt(16)
        val g = hex.substring(2, 4).toInt(16)
        val b = hex.substring(4, 6).toInt(16)
        return RGB(r, g, b)
    }

    /**
     * ANSI SGR 코드
     * 예시: "\u001B[30m" = "이후 텍스트를 검정색으로 출력해라"
     * 마무리: \u001B[30m + 텍스트 + \u001B[0m
     * "\u001b[30m" = 텍스트 검정 (black)
     * "\u001b[37m" = 텍스트 흰색 (white)
     * 인데...실제 색상이 잘 반영이 안되어서 rgb 값으로 처리 했다.
     * "\u001B[38;2;r;g;bm"	ANSI 24비트 전경색 지정 (텍스트 색상)
     * "\u001B[48;2;r;g;bm"	ANSI 24비트 배경색 지정
     */
    protected fun getColorPreview(bgHexString: String, textHexString: String = ""): String {
        val color = bgHexString.toRGB()
        val bgCode = "\u001B[48;2;${color.r};${color.g};${color.b}m"
        val resetCode = "\u001B[0m"
        val textColor = if (textHexString.isNotBlank()) {
            val textColor = textHexString.toRGB()
            "\u001B[38;2;${textColor.r};${textColor.g};${textColor.b}m"
        } else {
            if (isLightColor(bgHexString)) "\u001B[38;2;0;0;0m" else "\u001B[38;2;255;255;255m"
        }
        return "$bgCode$textColor  ${color.toHex()}  $resetCode"
    }

    protected fun isLightColor(hexString: String): Boolean {
        val color = hexString.toRGB()
        // YIQ 공식: 밝기 계산
        val brightness = (color.r * 299 + color.g * 587 + color.b * 114) / 1000
        return brightness >= 128
    }

}
