package com.snc.test.color

import com.snc.zero.color.Color
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@Suppress("NonAsciiCharacters")
class ColorTest : BaseJUnit5Test() {

    @Nested
    inner class `RGB 생성자 테스트` {

        @Test
        fun `정상적인 RGB 값으로 Color 객체를 생성할 수 있다`() {
            val color = Color(255, 128, 0)

            assertEquals(255, color.r)
            assertEquals(128, color.g)
            assertEquals(0, color.b)
        }

        @Test
        fun `RGB 경계값으로 Color 객체를 생성할 수 있다`() {
            val blackColor = Color(0, 0, 0)
            val whiteColor = Color(255, 255, 255)

            assertEquals(0, blackColor.r)
            assertEquals(0, blackColor.g)
            assertEquals(0, blackColor.b)

            assertEquals(255, whiteColor.r)
            assertEquals(255, whiteColor.g)
            assertEquals(255, whiteColor.b)
        }
    }

    @Nested
    inner class `Hex 생성자 테스트` {

        @ParameterizedTest
        @CsvSource(
            "#FF8000, 255, 128, 0",
            "#000000, 0, 0, 0",
            "#FFFFFF, 255, 255, 255",
            "#FF0000, 255, 0, 0",
            "#00FF00, 0, 255, 0",
            "#0000FF, 0, 0, 255"
        )
        fun `# 접두사가 있는 Hex 문자열로 Color 객체를 생성할 수 있다`(
            hex: String,
            expectedR: Int,
            expectedG: Int,
            expectedB: Int
        ) {
            val color = Color(hex)

            assertEquals(expectedR, color.r)
            assertEquals(expectedG, color.g)
            assertEquals(expectedB, color.b)
        }

        @ParameterizedTest
        @CsvSource(
            "FF8000, 255, 128, 0",
            "000000, 0, 0, 0",
            "FFFFFF, 255, 255, 255"
        )
        fun `# 접두사가 없는 Hex 문자열로 Color 객체를 생성할 수 있다`(
            hex: String,
            expectedR: Int,
            expectedG: Int,
            expectedB: Int
        ) {
            val color = Color(hex)

            assertEquals(expectedR, color.r)
            assertEquals(expectedG, color.g)
            assertEquals(expectedB, color.b)
        }

        @ParameterizedTest
        @ValueSource(strings = ["#ff8000", "#abcdef", "#123456"])
        fun `소문자 Hex 문자열로도 Color 객체를 생성할 수 있다`(hex: String) {
            assertDoesNotThrow {
                Color(hex)
            }
        }

        @Test
        fun `잘못된 Hex 문자열은 예외를 발생시킨다`() {
            assertThrows<NumberFormatException> {
                Color("#GGGGGG")
            }
        }

        @Test
        fun `길이가 부족한 Hex 문자열은 예외를 발생시킨다`() {
            assertThrows<StringIndexOutOfBoundsException> {
                Color("#FF80")
            }
        }
    }

    @Nested
    inner class `toHex 메서드 테스트` {

        @ParameterizedTest
        @CsvSource(
            "255, 128, 0, #FF8000",
            "0, 0, 0, #000000",
            "255, 255, 255, #FFFFFF",
            "255, 0, 0, #FF0000",
            "0, 255, 0, #00FF00",
            "0, 0, 255, #0000FF",
            "128, 128, 128, #808080"
        )
        fun `RGB 값을 올바른 Hex 문자열로 변환한다`(
            r: Int,
            g: Int,
            b: Int,
            expectedHex: String
        ) {
            val color = Color(r, g, b)

            assertEquals(expectedHex, color.toHex())
        }

        @Test
        fun `한 자리 Hex 값도 올바르게 0을 패딩하여 변환한다`() {
            val color = Color(15, 5, 10)

            assertEquals("#0F050A", color.toHex())
        }

        @Test
        fun `Hex 생성자와 toHex 메서드의 왕복 변환이 일치한다`() {
            val originalHex = "#FF8000"
            val color = Color(originalHex)
            val convertedHex = color.toHex()

            assertEquals(originalHex, convertedHex)
        }
    }

    @Nested
    inner class `toHsl 메서드 테스트` {

        @Test
        fun `순수한 빨간색을 HSL로 변환한다`() {
            val color = Color(255, 0, 0)
            val hsl = color.toHsl()

            assertEquals(0.0, hsl.h, 0.01)
            assertEquals(1.0, hsl.s, 0.01)
            assertEquals(0.5, hsl.l, 0.01)
        }

        @Test
        fun `순수한 초록색을 HSL로 변환한다`() {
            val color = Color(0, 255, 0)
            val hsl = color.toHsl()

            assertEquals(120.0, hsl.h, 0.01)
            assertEquals(1.0, hsl.s, 0.01)
            assertEquals(0.5, hsl.l, 0.01)
        }

        @Test
        fun `순수한 파란색을 HSL로 변환한다`() {
            val color = Color(0, 0, 255)
            val hsl = color.toHsl()

            assertEquals(240.0, hsl.h, 0.01)
            assertEquals(1.0, hsl.s, 0.01)
            assertEquals(0.5, hsl.l, 0.01)
        }

        @Test
        fun `검은색을 HSL로 변환한다`() {
            val color = Color(0, 0, 0)
            val hsl = color.toHsl()

            assertEquals(0.0, hsl.h, 0.01)
            assertEquals(0.0, hsl.s, 0.01)
            assertEquals(0.0, hsl.l, 0.01)
        }

        @Test
        fun `흰색을 HSL로 변환한다`() {
            val color = Color(255, 255, 255)
            val hsl = color.toHsl()

            assertEquals(0.0, hsl.h, 0.01)
            assertEquals(0.0, hsl.s, 0.01)
            assertEquals(1.0, hsl.l, 0.01)
        }

        @Test
        fun `회색을 HSL로 변환한다`() {
            val color = Color(128, 128, 128)
            val hsl = color.toHsl()

            assertEquals(0.0, hsl.h, 0.01)
            assertEquals(0.0, hsl.s, 0.01)
            assertEquals(0.502, hsl.l, 0.01)
        }

        @Test
        fun `시안색을 HSL로 변환한다`() {
            val color = Color(0, 255, 255)
            val hsl = color.toHsl()

            assertEquals(180.0, hsl.h, 0.01)
            assertEquals(1.0, hsl.s, 0.01)
            assertEquals(0.5, hsl.l, 0.01)
        }

        @Test
        fun `마젠타색을 HSL로 변환한다`() {
            val color = Color(255, 0, 255)
            val hsl = color.toHsl()

            assertEquals(300.0, hsl.h, 0.01)
            assertEquals(1.0, hsl.s, 0.01)
            assertEquals(0.5, hsl.l, 0.01)
        }

        @Test
        fun `노란색을 HSL로 변환한다`() {
            val color = Color(255, 255, 0)
            val hsl = color.toHsl()

            assertEquals(60.0, hsl.h, 0.01)
            assertEquals(1.0, hsl.s, 0.01)
            assertEquals(0.5, hsl.l, 0.01)
        }

        @Test
        fun `음수 Hue 값이 올바르게 처리된다`() {
            // RGB에서 음수 hue가 나올 수 있는 특수한 경우를 테스트
            val color = Color(255, 128, 255)
            val hsl = color.toHsl()

            assertTrue(hsl.h >= 0.0 && hsl.h < 360.0)
        }

        @ParameterizedTest
        @CsvSource(
            "255, 128, 64",
            "192, 192, 128",
            "64, 128, 192",
            "100, 150, 200"
        )
        fun `다양한 RGB 조합에서 HSL 값이 올바른 범위에 있다`(
            r: Int,
            g: Int,
            b: Int
        ) {
            val color = Color(r, g, b)
            val hsl = color.toHsl()

            assertTrue(hsl.h >= 0.0 && hsl.h < 360.0, "Hue는 0-360 범위여야 함")
            assertTrue(hsl.s >= 0.0 && hsl.s <= 1.0, "Saturation은 0-1 범위여야 함")
            assertTrue(hsl.l >= 0.0 && hsl.l <= 1.0, "Lightness는 0-1 범위여야 함")
        }
    }

    @Nested
    inner class `데이터 클래스 특성 테스트` {

        @Test
        fun `같은 RGB 값을 가진 Color 객체는 동일하다`() {
            val color1 = Color(255, 128, 0)
            val color2 = Color(255, 128, 0)

            assertEquals(color1, color2)
            assertEquals(color1.hashCode(), color2.hashCode())
        }

        @Test
        fun `다른 RGB 값을 가진 Color 객체는 다르다`() {
            val color1 = Color(255, 128, 0)
            val color2 = Color(255, 128, 1)

            assertNotEquals(color1, color2)
        }

        @Test
        fun `toString 메서드가 올바른 형식을 반환한다`() {
            val color = Color(255, 128, 0)
            val toString = color.toString()

            assertTrue(toString.contains("255"))
            assertTrue(toString.contains("128"))
            assertTrue(toString.contains("0"))
        }

        @Test
        fun `copy 메서드가 올바르게 동작한다`() {
            val originalColor = Color(255, 128, 0)
            val copiedColor = originalColor.copy(g = 64)

            assertEquals(255, copiedColor.r)
            assertEquals(64, copiedColor.g)
            assertEquals(0, copiedColor.b)
            assertNotEquals(originalColor, copiedColor)
        }

        @Test
        fun `구조 분해 할당이 올바르게 동작한다`() {
            val color = Color(255, 128, 0)
            val (r, g, b) = color

            assertEquals(255, r)
            assertEquals(128, g)
            assertEquals(0, b)
        }
    }

    @Nested
    inner class `통합 테스트` {

        @Test
        fun `Hex 생성자로 만든 Color가 toHex와 toHsl 모두 올바르게 동작한다`() {
            val hex = "#FF8000"
            val color = Color(hex)

            assertEquals(hex, color.toHex())

            val hsl = color.toHsl()
            assertTrue(hsl.h >= 0.0 && hsl.h < 360.0)
            assertTrue(hsl.s >= 0.0 && hsl.s <= 1.0)
            assertTrue(hsl.l >= 0.0 && hsl.l <= 1.0)
        }

        @Test
        fun `RGB 생성자로 만든 Color가 toHex와 toHsl 모두 올바르게 동작한다`() {
            val color = Color(255, 128, 0)

            assertEquals("#FF8000", color.toHex())

            val hsl = color.toHsl()
            assertTrue(hsl.h >= 0.0 && hsl.h < 360.0)
            assertTrue(hsl.s >= 0.0 && hsl.s <= 1.0)
            assertTrue(hsl.l >= 0.0 && hsl.l <= 1.0)
        }
    }
}
