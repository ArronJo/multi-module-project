package com.snc.test.color

import com.snc.zero.color.HSL
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("HSL 클래스 테스트")
class HSLTest : BaseJUnit5Test() {

    @Nested
    @DisplayName("HSL 객체 생성 테스트")
    inner class HSLCreationTest {

        @Test
        @DisplayName("HSL 객체를 정상적으로 생성할 수 있다")
        fun `should create HSL object with given values`() {
            // given
            val h = 120.0
            val s = 0.5
            val l = 0.7

            // when
            val hsl = HSL(h, s, l)

            // then
            assertEquals(h, hsl.h)
            assertEquals(s, hsl.s)
            assertEquals(l, hsl.l)
        }
    }

    @Nested
    @DisplayName("toColor() 메서드 테스트")
    inner class ToColorTest {

        @Nested
        @DisplayName("색상 영역별 변환 테스트 (h값 기준)")
        inner class HueRangeTest {

            @Test
            @DisplayName("h가 0~59 범위일 때 올바른 RGB 값으로 변환된다")
            fun `should convert to correct RGB when h is in range 0-59`() {
                // given
                val hsl = HSL(30.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(255, color.r)
                assertEquals(128, color.g)
                assertEquals(0, color.b)
            }

            @Test
            @DisplayName("h가 60~119 범위일 때 올바른 RGB 값으로 변환된다")
            fun `should convert to correct RGB when h is in range 60-119`() {
                // given
                val hsl = HSL(90.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(128, color.r)
                assertEquals(255, color.g)
                assertEquals(0, color.b)
            }

            @Test
            @DisplayName("h가 120~179 범위일 때 올바른 RGB 값으로 변환된다")
            fun `should convert to correct RGB when h is in range 120-179`() {
                // given
                val hsl = HSL(150.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(0, color.r)
                assertEquals(255, color.g)
                assertEquals(128, color.b)
            }

            @Test
            @DisplayName("h가 180~239 범위일 때 올바른 RGB 값으로 변환된다")
            fun `should convert to correct RGB when h is in range 180-239`() {
                // given
                val hsl = HSL(210.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(0, color.r)
                assertEquals(128, color.g)
                assertEquals(255, color.b)
            }

            @Test
            @DisplayName("h가 240~299 범위일 때 올바른 RGB 값으로 변환된다")
            fun `should convert to correct RGB when h is in range 240-299`() {
                // given
                val hsl = HSL(270.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(128, color.r)
                assertEquals(0, color.g)
                assertEquals(255, color.b)
            }

            @Test
            @DisplayName("h가 300~359 범위일 때 올바른 RGB 값으로 변환된다")
            fun `should convert to correct RGB when h is in range 300-359`() {
                // given
                val hsl = HSL(330.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(255, color.r)
                assertEquals(0, color.g)
                assertEquals(128, color.b)
            }

            @Test
            @DisplayName("h가 360 이상일 때도 올바른 RGB 값으로 변환된다")
            fun `should convert to correct RGB when h is 360 or more`() {
                // given
                val hsl = HSL(390.0, 1.0, 0.5) // 390 = 30 + 360
                // 390 / 60 = 6.5, (6.5).toInt() = 6, when문에서 else 분기로 가서 Triple(c, 0.0, x)
                // c = 1.0, x = 0.5

                // when
                val color = hsl.toColor()

                // then
                assertEquals(255, color.r)
                assertEquals(0, color.g)
                assertEquals(128, color.b)
            }
        }

        @Nested
        @DisplayName("특수한 HSL 값 테스트")
        inner class SpecialHSLValuesTest {

            @Test
            @DisplayName("순수한 흰색(H=0, S=0, L=1)을 변환한다")
            fun `should convert pure white`() {
                // given
                val hsl = HSL(0.0, 0.0, 1.0)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(255, color.r)
                assertEquals(255, color.g)
                assertEquals(255, color.b)
            }

            @Test
            @DisplayName("순수한 검정색(H=0, S=0, L=0)을 변환한다")
            fun `should convert pure black`() {
                // given
                val hsl = HSL(0.0, 0.0, 0.0)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(0, color.r)
                assertEquals(0, color.g)
                assertEquals(0, color.b)
            }

            @Test
            @DisplayName("회색(H=0, S=0, L=0.5)을 변환한다")
            fun `should convert gray`() {
                // given
                val hsl = HSL(0.0, 0.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(128, color.r)
                assertEquals(128, color.g)
                assertEquals(128, color.b)
            }

            @Test
            @DisplayName("채도가 0인 색상을 변환한다")
            fun `should convert color with zero saturation`() {
                // given
                val hsl = HSL(120.0, 0.0, 0.3)
                // c = (1 - |2*0.3 - 1|) * 0.0 = 0.4 * 0.0 = 0
                // x = 0 * (1 - |2 - 1|) = 0
                // m = 0.3 - 0/2 = 0.3
                // 모든 RGB 값이 m과 같아짐
                // r = g = b = round(0.3 * 255) = round(76.5) = 76

                // when
                val color = hsl.toColor()

                // then
                assertEquals(76, color.r)
                assertEquals(76, color.g)
                assertEquals(76, color.b)
            }
        }

        @Nested
        @DisplayName("RGB 값 범위 제한 테스트")
        inner class RGBClampingTest {

            @Test
            @DisplayName("계산된 RGB 값이 0~255 범위를 벗어나지 않는다")
            fun `should clamp RGB values to 0-255 range`() {
                // given
                val hsl = HSL(0.0, 1.0, 1.0)

                // when
                val color = hsl.toColor()

                // then
                assert(color.r in 0..255)
                assert(color.g in 0..255)
                assert(color.b in 0..255)
            }
        }

        @Nested
        @DisplayName("경계값 테스트")
        inner class BoundaryValuesTest {

            @Test
            @DisplayName("H=0도에서 정확히 변환된다")
            fun `should convert correctly at H=0`() {
                // given
                val hsl = HSL(0.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(255, color.r)
                assertEquals(0, color.g)
                assertEquals(0, color.b)
            }

            @Test
            @DisplayName("H=60도에서 정확히 변환된다")
            fun `should convert correctly at H=60`() {
                // given
                val hsl = HSL(60.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(255, color.r)
                assertEquals(255, color.g)
                assertEquals(0, color.b)
            }

            @Test
            @DisplayName("H=120도에서 정확히 변환된다")
            fun `should convert correctly at H=120`() {
                // given
                val hsl = HSL(120.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(0, color.r)
                assertEquals(255, color.g)
                assertEquals(0, color.b)
            }

            @Test
            @DisplayName("H=180도에서 정확히 변환된다")
            fun `should convert correctly at H=180`() {
                // given
                val hsl = HSL(180.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(0, color.r)
                assertEquals(255, color.g)
                assertEquals(255, color.b)
            }

            @Test
            @DisplayName("H=240도에서 정확히 변환된다")
            fun `should convert correctly at H=240`() {
                // given
                val hsl = HSL(240.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(0, color.r)
                assertEquals(0, color.g)
                assertEquals(255, color.b)
            }

            @Test
            @DisplayName("H=300도에서 정확히 변환된다")
            fun `should convert correctly at H=300`() {
                // given
                val hsl = HSL(300.0, 1.0, 0.5)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(255, color.r)
                assertEquals(0, color.g)
                assertEquals(255, color.b)
            }
        }

        @Nested
        @DisplayName("다양한 채도와 명도 조합 테스트")
        inner class SaturationLightnessTest {

            @Test
            @DisplayName("낮은 채도와 높은 명도 조합을 변환한다")
            fun `should convert low saturation high lightness`() {
                // given
                val hsl = HSL(180.0, 0.2, 0.8)
                // c = (1 - |2*0.8 - 1|) * 0.2 = 0.4 * 0.2 = 0.08
                // x = 0.08 * (1 - |3.0 % 2.0 - 1.0|) = 0.08 * 1 = 0.08
                // m = 0.8 - 0.08/2 = 0.76
                // (h/60).toInt() = 3이므로 Triple(0.0, x, c) = (0.0, 0.08, 0.08)
                // r = round((0.0 + 0.76) * 255) = 194, g = round((0.08 + 0.76) * 255) = 214, b = round((0.08 + 0.76) * 255) = 214

                // when
                val color = hsl.toColor()

                // then
                assertEquals(194, color.r)
                assertEquals(214, color.g)
                assertEquals(214, color.b)
            }

            @Test
            @DisplayName("높은 채도와 낮은 명도 조합을 변환한다")
            fun `should convert high saturation low lightness`() {
                // given
                val hsl = HSL(240.0, 0.8, 0.3)

                // when
                val color = hsl.toColor()

                // then
                assertEquals(15, color.r)
                assertEquals(15, color.g)
                assertEquals(138, color.b)
            }

            @Test
            @DisplayName("중간 채도와 중간 명도 조합을 변환한다")
            fun `should convert medium saturation medium lightness`() {
                // given
                val hsl = HSL(90.0, 0.5, 0.6)
                // c = (1 - |2*0.6 - 1|) * 0.5 = 0.8 * 0.5 = 0.4
                // x = 0.4 * (1 - |1.5 - 1|) = 0.4 * 0.5 = 0.2
                // m = 0.6 - 0.4/2 = 0.4
                // (h/60).toInt() = 1이므로 Triple(x, c, 0.0) = (0.2, 0.4, 0)
                // r = (0.2 + 0.4) * 255 = 153, g = (0.4 + 0.4) * 255 = 204, b = (0 + 0.4) * 255 = 102

                // when
                val color = hsl.toColor()

                // then
                assertEquals(153, color.r)
                assertEquals(204, color.g)
                assertEquals(102, color.b)
            }
        }
    }
}
