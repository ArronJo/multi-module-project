package com.snc.test.color.contrast

import com.snc.zero.color.contrast.ColorContrastCalculator
import com.snc.zero.color.contrast.roundTo
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@Suppress("NonAsciiCharacters")
@DisplayName("색상 대비율 계산기 테스트")
class ColorContrastCalculatorTest : BaseJUnit5Test() {

    private lateinit var calculator: ColorContrastCalculator

    @BeforeEach
    fun setUp() {
        calculator = ColorContrastCalculator()
    }

    data class ColorTestCase(val color: String, val description: String)

    @Nested
    @DisplayName("WCAG 기준 대비율 테스트")
    inner class WCAGContrastRatioTest {

        @Test
        @DisplayName("WCAG AA/AAA 기준 4.5:1, 7:1 대비율 테스트")
        fun `웹 접근성을 준수하기 위해 고대비 4_5와 7_0 대 1 색상 테스트`() {
            // Given
            val baseColor = "#3498db"
            val textColor = "#ffffff"
            val targetRatio = 4.5

            println("\n=== 입력 정보 ===")
            println("기본 색상: $baseColor")
            println("텍스트 색상: $textColor")
            println("목표 대비율: $targetRatio:1")

            // When
            val result = calculator.adjustColorForContrast(
                baseColorHex = baseColor,
                textColorHex = textColor,
                targetRatio = targetRatio
            )

            // Then
            assertNotNull(result, "조정 결과가 null이면 안됩니다")
            assertNotNull(result.originalColor, "원본 색상이 null이면 안됩니다")
            assertNotNull(result.adjustedColor, "조정된 색상이 null이면 안됩니다")
            assertEquals(targetRatio, result.targetRatio, "목표 대비율이 일치해야 합니다")

            println("\n=== WCAG 기준 정보 ===")
            println("레벨 AA: 일반 텍스트: 최소 4.5:1")
            println("큰 텍스트 (18pt 이상 또는 14pt 굵은 글씨): 최소 3:1")
            println("레벨 AAA: 일반 텍스트 최소 7:1, 큰 텍스트 최소 4.5:1 ")

            println("\n=== 조정 결과 ===")
            println("택스트 색상: ${getColorPreview(result.textColor)}")
            println("원본 색상: ${getColorPreview(result.originalColor, result.textColor)}")
            println("조정된 색상: ${getColorPreview(result.adjustedColor, result.textColor)}")
            println("원본 대비율: ${result.originalRatio}:1")

            if (result.adjustmentNeeded) {
                println("조정된 대비율: ${result.finalRatio}:1")
                println("조정 필요 여부: ✓")
                result.finalRatio?.let { finalRatio ->
                    assertTrue(
                        finalRatio >= targetRatio,
                        "조정된 대비율이 목표 대비율보다 크거나 같아야 합니다. 실제: $finalRatio, 목표: $targetRatio"
                    )
                }
            } else {
                println("조정 필요 여부: ❌ (이미 목표 달성)")
                assertTrue(
                    result.originalRatio >= targetRatio,
                    "조정이 필요없다면 원본 대비율이 목표 대비율보다 크거나 같아야 합니다"
                )
            }

            println("WCAG AA 기준 (4.5:1) 만족: ${if (result.meetsAA) "✓" else "❌"}")
            println("WCAG AAA 기준 (7.0:1) 만족: ${if (result.meetsAAA) "✓" else "❌"}")

            // 실제 대비율 재검증
            val actualRatio = calculator.calculateContrastRatio(
                calculator.hexToRgb(result.adjustedColor),
                calculator.hexToRgb(textColor)
            )
            println("실제 검증 대비율: ${actualRatio.roundTo(2)}:1")
            println("목표 달성: ${if (actualRatio >= targetRatio) "✓" else "❌"}")

            // 실제 대비율 검증
            assertTrue(
                actualRatio >= targetRatio - 0.01, // 부동소수점 오차 허용
                "실제 대비율이 목표 대비율 이상이어야 합니다. 실제: $actualRatio, 목표: $targetRatio"
            )

            println()
        }

        @Test
        @DisplayName("AAA 기준 7:1 대비율 테스트")
        fun `AAA 기준 7대1 대비율 테스트`() {
            // Given
            val baseColor = "#3498db"
            val textColor = "#ffffff"
            val targetRatio = 7.0

            // When
            val result = calculator.adjustColorForContrast(
                baseColorHex = baseColor,
                textColorHex = textColor,
                targetRatio = targetRatio
            )

            // Then
            assertNotNull(result)
            assertEquals(targetRatio, result.targetRatio)

            if (result.adjustmentNeeded) {
                result.finalRatio?.let { finalRatio ->
                    assertTrue(
                        finalRatio >= targetRatio,
                        "AAA 기준 7:1을 만족해야 합니다. 실제: $finalRatio"
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("웹 접근성 고대비 색상 데모 테스트")
    inner class HighContrastColorDemoTest {

        @Test
        @DisplayName("다양한 색상에 대한 대비율 자동 조정 결과 확인")
        fun `웹 접근성을 준수하기 위해 고대비 색상 데모 테스트 해보자`() {
            // Given
            val testCases = listOf(
                ColorTestCase("#3498db", "밝은 파란색 + 흰색 텍스트"),
                ColorTestCase("#e74c3c", "밝은 빨간색 + 흰색 텍스트"),
                ColorTestCase("#f39c12", "주황색 + 흰색 텍스트"),
                ColorTestCase("#27ae60", "초록색 + 흰색 텍스트")
            )

            println("=== 색상 대비율 자동 조정 결과 ===\n")

            testCases.forEach { (baseColor, description) ->
                println("📝 $description")
                println("원본 색상: ${getColorPreview(baseColor)}")

                // When
                val result = calculator.adjustColorForContrast(baseColor)

                // Then
                assertNotNull(result, "조정 결과가 null이면 안됩니다")
                assertNotNull(result.adjustedColor, "조정된 색상이 null이면 안됩니다")
                assertTrue(result.originalRatio > 0, "원본 대비율은 0보다 커야 합니다")
                assertTrue(result.finalRatio != null, "최종 대비율이 설정되어야 합니다")

                println("조정된 색상: ${getColorPreview(result.adjustedColor)}")
                println("원본 대비율: ${result.originalRatio}:1")
                result.finalRatio?.let { println("조정된 대비율: $it:1") }
                println("AA 기준 만족: ${if (result.meetsAA) "✓" else "❌"}")
                println("AAA 기준 만족: ${if (result.meetsAAA) "✓" else "❌"}")
                println("-".repeat(50))
            }
        }
    }

    @Nested
    @DisplayName("색상 변환 기능 테스트")
    inner class ColorConversionTest {

        @Test
        @DisplayName("HEX to RGB 변환 테스트")
        fun `HEX to RGB 변환 테스트`() {
            // Given & When & Then
            val whiteRgb = calculator.hexToRgb("#ffffff")
            assertEquals(Triple(255, 255, 255), whiteRgb, "흰색 변환이 정확해야 합니다")

            val blackRgb = calculator.hexToRgb("#000000")
            assertEquals(Triple(0, 0, 0), blackRgb, "검은색 변환이 정확해야 합니다")

            val blueRgb = calculator.hexToRgb("#3498db")
            assertEquals(Triple(52, 152, 219), blueRgb, "파란색 변환이 정확해야 합니다")

            // # 없이도 동작하는지 테스트
            val redRgb = calculator.hexToRgb("e74c3c")
            assertEquals(Triple(231, 76, 60), redRgb, "# 없는 헥스 코드도 처리되어야 합니다")
        }

        @Test
        @DisplayName("RGB to HEX 변환 테스트")
        fun `RGB to HEX 변환 테스트`() {
            // Given & When & Then
            val whiteHex = calculator.rgbToHex(Triple(255, 255, 255))
            assertEquals("#ffffff", whiteHex, "흰색 RGB → HEX 변환이 정확해야 합니다")

            val blackHex = calculator.rgbToHex(Triple(0, 0, 0))
            assertEquals("#000000", blackHex, "검은색 RGB → HEX 변환이 정확해야 합니다")

            val blueHex = calculator.rgbToHex(Triple(52, 152, 219))
            assertEquals("#3498db", blueHex, "파란색 RGB → HEX 변환이 정확해야 합니다")
        }

        @Test
        @DisplayName("HEX ↔ RGB 양방향 변환 테스트")
        fun `HEX RGB 양방향 변환 테스트`() {
            // Given
            val originalHexColors = listOf("#ffffff", "#000000", "#3498db", "#e74c3c", "#f39c12")

            originalHexColors.forEach { originalHex ->
                // When
                val rgb = calculator.hexToRgb(originalHex)
                val convertedHex = calculator.rgbToHex(rgb)

                // Then
                assertEquals(
                    originalHex,
                    convertedHex,
                    "HEX → RGB → HEX 변환 후 원본과 같아야 합니다: $originalHex"
                )
            }
        }
    }

    @Nested
    @DisplayName("상대 휘도 및 대비율 계산 테스트")
    inner class LuminanceAndContrastTest {

        @Test
        @DisplayName("상대 휘도 계산 테스트")
        fun `상대 휘도 계산 테스트`() {
            // Given & When & Then
            val whiteLuminance = calculator.getRelativeLuminance(Triple(255, 255, 255))
            assertEquals(1.0, whiteLuminance, 0.01, "흰색의 상대 휘도는 1.0에 가까워야 합니다")

            val blackLuminance = calculator.getRelativeLuminance(Triple(0, 0, 0))
            assertEquals(0.0, blackLuminance, 0.01, "검은색의 상대 휘도는 0.0에 가까워야 합니다")

            // 회색은 중간값
            val grayLuminance = calculator.getRelativeLuminance(Triple(128, 128, 128))
            assertTrue(
                grayLuminance > blackLuminance && grayLuminance < whiteLuminance,
                "회색의 휘도는 검은색과 흰색 사이여야 합니다"
            )
        }

        @Test
        @DisplayName("대비율 계산 테스트")
        fun `대비율 계산 테스트`() {
            // Given
            val white = Triple(255, 255, 255)
            val black = Triple(0, 0, 0)

            // When
            val contrastRatio = calculator.calculateContrastRatio(white, black)

            // Then
            assertTrue(contrastRatio > 20.0, "흰색과 검은색의 대비율은 20:1 이상이어야 합니다")

            // 동일한 색상의 대비율은 1:1
            val sameColorRatio = calculator.calculateContrastRatio(white, white)
            assertEquals(1.0, sameColorRatio, 0.01, "동일한 색상의 대비율은 1:1이어야 합니다")
        }

        @Test
        @DisplayName("WCAG 기준 대비율 상수 테스트")
        fun `WCAG 기준 대비율 상수 테스트`() {
            // Then
            assertEquals(4.5, ColorContrastCalculator.MIN_CONTRAST_AA, "AA 기준은 4.5:1이어야 합니다")
            assertEquals(7.0, ColorContrastCalculator.MIN_CONTRAST_AAA, "AAA 기준은 7.0:1이어야 합니다")
        }
    }

    @Nested
    @DisplayName("색상 대비율 경계값 테스트")
    inner class ContrastRatioBoundaryTest {

        @Test
        @DisplayName("AA 기준 경계값 테스트 (4.5:1)")
        fun `AA 기준 경계값 테스트`() {
            // Given
            val aaThreshold = ColorContrastCalculator.MIN_CONTRAST_AA

            // When & Then
            // 흰색과 검은색은 AA 기준을 만족해야 함
            val result = calculator.adjustColorForContrast("#000000", "#ffffff", aaThreshold)

            assertNotNull(result)
            assertTrue(result.meetsAA, "검은색과 흰색 조합은 AA 기준을 만족해야 합니다")

            val finalRatio = result.finalRatio ?: result.originalRatio
            assertTrue(
                finalRatio >= aaThreshold,
                "최종 대비율이 AA 기준 이상이어야 합니다. 실제: $finalRatio"
            )
        }

        @Test
        @DisplayName("AAA 기준 경계값 테스트 (7:1)")
        fun `AAA 기준 경계값 테스트`() {
            // Given
            val aaaThreshold = ColorContrastCalculator.MIN_CONTRAST_AAA

            // When & Then
            val result = calculator.adjustColorForContrast("#000000", "#ffffff", aaaThreshold)

            assertNotNull(result)
            assertTrue(result.meetsAAA, "검은색과 흰색 조합은 AAA 기준을 만족해야 합니다")

            val finalRatio = result.finalRatio ?: result.originalRatio
            assertTrue(
                finalRatio >= aaaThreshold,
                "최종 대비율이 AAA 기준 이상이어야 합니다. 실제: $finalRatio"
            )
        }

        @Test
        @DisplayName("동일한 색상 조합의 대비율 테스트")
        fun `동일한 색상 조합의 대비율 테스트`() {
            // Given
            val sameColor = "#3498db"

            // When
            val result = calculator.adjustColorForContrast(sameColor, sameColor, 4.5)

            // Then
            assertNotNull(result)
            assertTrue(result.adjustmentNeeded, "동일한 색상은 조정이 필요해야 합니다")
            assertNotEquals(
                result.originalColor,
                result.adjustedColor,
                "동일한 색상의 경우 조정된 색상이 달라야 합니다"
            )
        }
    }

    @Nested
    @DisplayName("색상 헥스 코드 유효성 테스트")
    inner class HexCodeValidationTest {

        @Test
        @DisplayName("유효한 헥스 코드 테스트")
        fun `유효한 헥스 코드 테스트`() {
            // Given
            val validHexCodes = listOf(
                "#000000",
                "#ffffff",
                "#3498db",
                "#e74c3c",
                "#f39c12",
                "#27ae60",
                "#9b59b6",
                "#1abc9c"
            )

            // When & Then
            validHexCodes.forEach { hexCode ->
                assertDoesNotThrow("유효한 헥스 코드 처리 중 예외가 발생하면 안됩니다: $hexCode") {
                    val result = calculator.adjustColorForContrast(hexCode)
                    assertNotNull(result, "유효한 헥스 코드에 대해 결과가 반환되어야 합니다: $hexCode")
                }
            }
        }

        @Test
        @DisplayName("잘못된 헥스 코드 처리 테스트")
        fun `잘못된 헥스 코드 처리 테스트`() {
            // Given
            val invalidHexCodes = listOf(
                "invalid",
                "#gggggg",
                "#12345",
                ""
            )

            // When & Then
            invalidHexCodes.forEach { hexCode ->
                assertThrows<Exception>("잘못된 헥스 코드는 예외를 발생시켜야 합니다: $hexCode") {
                    calculator.adjustColorForContrast(hexCode)
                }
            }
        }

        @Test
        @DisplayName("# 접두사가 없는 헥스 코드 테스트")
        fun `접두사 없는 헥스 코드 테스트`() {
            // Given
            val hexWithoutPrefix = "3498db"
            val hexWithPrefix = "#3498db"

            // When
            val resultWithoutPrefix = calculator.adjustColorForContrast(hexWithoutPrefix)
            val resultWithPrefix = calculator.adjustColorForContrast(hexWithPrefix)

            // Then
            // 두 결과의 대비율이 동일해야 함 (# 유무와 관계없이)
            assertEquals(
                resultWithPrefix.originalRatio,
                resultWithoutPrefix.originalRatio,
                0.01,
                "# 접두사 유무와 관계없이 동일한 결과가 나와야 합니다"
            )
        }
    }

    @Nested
    @DisplayName("ContrastResult 데이터 클래스 테스트")
    inner class ContrastResultTest {

        @Test
        @DisplayName("ContrastResult 객체 생성 및 속성 테스트")
        fun `ContrastResult 객체 생성 및 속성 테스트`() {
            // Given
            val result = calculator.adjustColorForContrast("#3498db", "#ffffff", 4.5)

            // Then
            assertNotNull(result.originalColor, "원본 색상이 설정되어야 합니다")
            assertNotNull(result.adjustedColor, "조정된 색상이 설정되어야 합니다")
            assertTrue(result.originalRatio > 0, "원본 대비율이 0보다 커야 합니다")
            assertEquals(4.5, result.targetRatio, "목표 대비율이 설정되어야 합니다")

            // meetsAA와 meetsAAA는 실제 대비율에 따라 결정
            val finalRatio = result.finalRatio ?: result.originalRatio
            assertEquals(finalRatio >= 4.5, result.meetsAA, "AA 기준 충족 여부가 정확해야 합니다")
            assertEquals(finalRatio >= 7.0, result.meetsAAA, "AAA 기준 충족 여부가 정확해야 합니다")
        }
    }

    @Nested
    @DisplayName("Double 확장 함수 테스트")
    inner class DoubleExtensionTest {

        @Test
        @DisplayName("roundTo 확장 함수 테스트")
        fun `roundTo 확장 함수 테스트`() {
            // Given & When & Then
            assertEquals(3.14, 3.14159.roundTo(2), "소수점 2자리 반올림이 정확해야 합니다")
            assertEquals(3.1, 3.14159.roundTo(1), "소수점 1자리 반올림이 정확해야 합니다")
            assertEquals(3.0, 3.14159.roundTo(0), "소수점 0자리 반올림이 정확해야 합니다")
            assertEquals(1.235, 1.23456.roundTo(3), "소수점 3자리 반올림이 정확해야 합니다")
        }
    }

    @Nested
    @DisplayName("색상 변환 기능")
    inner class ColorConversion2Test {

        @Nested
        @DisplayName("HEX to RGB 변환")
        inner class HexToRgbTest {

            @Test
            @DisplayName("흰색 변환 테스트")
            fun `흰색 HEX를 RGB로 변환한다`() {
                // given
                val hexColor = "#ffffff"

                // when
                val result = calculator.hexToRgb(hexColor)

                // then
                assertEquals(Triple(255, 255, 255), result)
            }

            @Test
            @DisplayName("검은색 변환 테스트")
            fun `검은색 HEX를 RGB로 변환한다`() {
                // given
                val hexColor = "#000000"

                // when
                val result = calculator.hexToRgb(hexColor)

                // then
                assertEquals(Triple(0, 0, 0), result)
            }

            @ParameterizedTest
            @CsvSource(
                "#ff0000, 255, 0, 0",
                "#00ff00, 0, 255, 0",
                "#0000ff, 0, 0, 255",
                "#808080, 128, 128, 128",
                "#123456, 18, 52, 86"
            )
            @DisplayName("다양한 색상 변환 테스트")
            fun `다양한 HEX 색상을 RGB로 변환한다`(hex: String, r: Int, g: Int, b: Int) {
                // when
                val result = calculator.hexToRgb(hex)

                // then
                assertEquals(Triple(r, g, b), result)
            }

            @Test
            @DisplayName("# 접두사 없는 HEX 변환 테스트")
            fun `접두사 없는 HEX 색상을 변환한다`() {
                // given
                val hexColor = "ff00ff"

                // when
                val result = calculator.hexToRgb(hexColor)

                // then
                assertEquals(Triple(255, 0, 255), result)
            }
        }

        @Nested
        @DisplayName("RGB to HEX 변환")
        inner class RgbToHexTest {

            @Test
            @DisplayName("흰색 변환 테스트")
            fun `RGB 흰색을 HEX로 변환한다`() {
                // given
                val rgb = Triple(255, 255, 255)

                // when
                val result = calculator.rgbToHex(rgb)

                // then
                assertEquals("#ffffff", result)
            }

            @Test
            @DisplayName("검은색 변환 테스트")
            fun `RGB 검은색을 HEX로 변환한다`() {
                // given
                val rgb = Triple(0, 0, 0)

                // when
                val result = calculator.rgbToHex(rgb)

                // then
                assertEquals("#000000", result)
            }

            @ParameterizedTest
            @CsvSource(
                "255, 0, 0, #ff0000",
                "0, 255, 0, #00ff00",
                "0, 0, 255, #0000ff",
                "128, 128, 128, #808080",
                "18, 52, 86, #123456"
            )
            @DisplayName("다양한 RGB to HEX 변환 테스트")
            fun `다양한 RGB를 HEX로 변환한다`(r: Int, g: Int, b: Int, expectedHex: String) {
                // given
                val rgb = Triple(r, g, b)

                // when
                val result = calculator.rgbToHex(rgb)

                // then
                assertEquals(expectedHex, result)
            }
        }

        @Nested
        @DisplayName("색상 변환 상호 변환 테스트")
        inner class ConversionRoundTripTest {

            @ParameterizedTest
            @ValueSource(strings = ["#ffffff", "#000000", "#ff0000", "#00ff00", "#0000ff", "#123456"])
            @DisplayName("HEX -> RGB -> HEX 상호 변환 테스트")
            fun `HEX에서 RGB로 변환 후 다시 HEX로 변환하면 원래 값이 나온다`(originalHex: String) {
                // when
                val rgb = calculator.hexToRgb(originalHex)
                val convertedHex = calculator.rgbToHex(rgb)

                // then
                assertEquals(originalHex, convertedHex)
            }
        }
    }

    @Nested
    @DisplayName("상대 휘도 계산")
    inner class RelativeLuminanceTest {

        @Test
        @DisplayName("흰색 상대 휘도 테스트")
        fun `흰색의 상대 휘도는 1이다`() {
            // given
            val whiteRgb = Triple(255, 255, 255)

            // when
            val luminance = calculator.getRelativeLuminance(whiteRgb)

            // then
            assertEquals(1.0, luminance, 0.001)
        }

        @Test
        @DisplayName("검은색 상대 휘도 테스트")
        fun `검은색의 상대 휘도는 0이다`() {
            // given
            val blackRgb = Triple(0, 0, 0)

            // when
            val luminance = calculator.getRelativeLuminance(blackRgb)

            // then
            assertEquals(0.0, luminance, 0.001)
        }

        @Test
        @DisplayName("빨간색 상대 휘도 테스트")
        fun `빨간색의 상대 휘도를 계산한다`() {
            // given
            val redRgb = Triple(255, 0, 0)

            // when
            val luminance = calculator.getRelativeLuminance(redRgb)

            // then
            assertTrue(luminance > 0.0)
            assertTrue(luminance < 1.0)
            assertEquals(0.2126, luminance, 0.01)
        }

        @Test
        @DisplayName("초록색 상대 휘도 테스트")
        fun `초록색의 상대 휘도를 계산한다`() {
            // given
            val greenRgb = Triple(0, 255, 0)

            // when
            val luminance = calculator.getRelativeLuminance(greenRgb)

            // then
            assertTrue(luminance > 0.0)
            assertTrue(luminance < 1.0)
            assertEquals(0.7152, luminance, 0.01)
        }

        @Test
        @DisplayName("파란색 상대 휘도 테스트")
        fun `파란색의 상대 휘도를 계산한다`() {
            // given
            val blueRgb = Triple(0, 0, 255)

            // when
            val luminance = calculator.getRelativeLuminance(blueRgb)

            // then
            assertTrue(luminance > 0.0)
            assertTrue(luminance < 1.0)
            assertEquals(0.0722, luminance, 0.01)
        }

        @Test
        @DisplayName("회색 상대 휘도 테스트")
        fun `회색의 상대 휘도를 계산한다`() {
            // given
            val grayRgb = Triple(128, 128, 128)

            // when
            val luminance = calculator.getRelativeLuminance(grayRgb)

            // then
            assertTrue(luminance > 0.0)
            assertTrue(luminance < 1.0)
            assertEquals(0.216, luminance, 0.01)
        }
    }

    @Nested
    @DisplayName("대비율 계산")
    inner class ContrastRatioTest {

        @Test
        @DisplayName("흰색과 검은색 대비율 테스트")
        fun `흰색과 검은색의 대비율은 21이다`() {
            // given
            val white = Triple(255, 255, 255)
            val black = Triple(0, 0, 0)

            // when
            val ratio = calculator.calculateContrastRatio(white, black)

            // then
            assertEquals(21.0, ratio, 0.1)
        }

        @Test
        @DisplayName("같은 색상 대비율 테스트")
        fun `같은 색상의 대비율은 1이다`() {
            // given
            val color = Triple(128, 128, 128)

            // when
            val ratio = calculator.calculateContrastRatio(color, color)

            // then
            assertEquals(1.0, ratio, 0.001)
        }

        @Test
        @DisplayName("색상 순서 무관 테스트")
        fun `대비율은 색상 순서에 무관하다`() {
            // given
            val color1 = Triple(255, 0, 0)
            val color2 = Triple(0, 255, 0)

            // when
            val ratio1 = calculator.calculateContrastRatio(color1, color2)
            val ratio2 = calculator.calculateContrastRatio(color2, color1)

            // then
            assertEquals(ratio1, ratio2, 0.001)
        }

        @ParameterizedTest
        @CsvSource(
            "255, 255, 255, 0, 0, 0, 21.0",
            "255, 0, 0, 255, 255, 255, 3.998",
            "0, 255, 0, 255, 255, 255, 1.372",
            "0, 0, 255, 255, 255, 255, 8.592"
        )
        @DisplayName("다양한 색상 대비율 테스트")
        fun `다양한 색상 조합의 대비율을 계산한다`(
            r1: Int,
            g1: Int,
            b1: Int,
            r2: Int,
            g2: Int,
            b2: Int,
            expectedRatio: Double
        ) {
            // given
            val color1 = Triple(r1, g1, b1)
            val color2 = Triple(r2, g2, b2)

            // when
            val ratio = calculator.calculateContrastRatio(color1, color2)

            // then
            assertEquals(expectedRatio, ratio, 0.1)
        }
    }

    @Nested
    @DisplayName("색상 대비 조정")
    inner class AdjustColorForContrastTest {

        @Nested
        @DisplayName("조정 불필요한 경우")
        inner class NoAdjustmentNeededTest {

            @Test
            @DisplayName("이미 충분한 대비율을 가진 색상")
            fun `이미 충분한 대비율을 가진 색상은 조정하지 않는다`() {
                // given
                val baseColor = "#ffffff"
                val textColor = "#000000"
                val targetRatio = 4.5

                // when
                val result = calculator.adjustColorForContrast(baseColor, textColor, targetRatio)

                // then
                assertFalse(result.adjustmentNeeded)
                assertEquals(baseColor, result.adjustedColor)
                assertEquals(baseColor, result.originalColor)
                assertEquals(textColor, result.textColor)
                assertTrue(result.originalRatio >= targetRatio)
                assertTrue(result.meetsAA)
                assertTrue(result.meetsAAA)
            }
        }

        @Nested
        @DisplayName("배경색 조정")
        inner class BackgroundAdjustmentTest {

            @Test
            @DisplayName("배경색을 어둡게 조정")
            fun `밝은 배경색을 어둡게 조정하여 대비율을 높인다`() {
                // given
                val baseColor = "#f0f0f0"
                val textColor = "#ffffff"
                val targetRatio = 4.5

                // when
                val result = calculator.adjustColorForContrast(
                    baseColor,
                    textColor,
                    targetRatio,
                    adjustBackground = true
                )

                // then
                assertTrue(result.adjustmentNeeded)
                assertNotEquals(baseColor, result.adjustedColor)
                assertTrue(result.finalRatio!! >= targetRatio)
                assertTrue(result.meetsAA)
            }

            @Test
            @DisplayName("배경색을 밝게 조정")
            fun `어두운 배경색을 밝게 조정하여 대비율을 높인다`() {
                // given
                val baseColor = "#101010"
                val textColor = "#000000"
                val targetRatio = 4.5

                // when
                val result = calculator.adjustColorForContrast(
                    baseColor,
                    textColor,
                    targetRatio,
                    adjustBackground = true
                )

                // then
                assertTrue(result.adjustmentNeeded)
                assertNotEquals(baseColor, result.adjustedColor)
                assertTrue(result.finalRatio!! >= targetRatio)
                assertTrue(result.meetsAA)
            }
        }

        @Nested
        @DisplayName("텍스트색 조정")
        inner class TextAdjustmentTest {

            @Test
            @DisplayName("텍스트색 조정으로 대비율 개선")
            fun `텍스트색을 조정하여 대비율을 높인다`() {
                // given
                val baseColor = "#808080"
                val textColor = "#909090"
                val targetRatio = 4.5

                // when
                val result = calculator.adjustColorForContrast(
                    baseColor,
                    textColor,
                    targetRatio,
                    adjustBackground = false
                )

                // then
                assertTrue(result.adjustmentNeeded)
                assertTrue(result.finalRatio!! >= targetRatio)
                assertTrue(result.meetsAA)
            }
        }

        @Nested
        @DisplayName("목표 대비율별 테스트")
        inner class TargetRatioTest {

            @Test
            @DisplayName("AA 기준 대비율 4.5 달성")
            fun `AA 기준 대비율 4점5를 달성한다`() {
                // given
                val baseColor = "#cccccc"
                val textColor = "#ffffff"
                val targetRatio = ColorContrastCalculator.MIN_CONTRAST_AA

                // when
                val result = calculator.adjustColorForContrast(baseColor, textColor, targetRatio)

                // then
                assertTrue(result.finalRatio!! >= ColorContrastCalculator.MIN_CONTRAST_AA)
                assertTrue(result.meetsAA)
            }

            @Test
            @DisplayName("AAA 기준 대비율 7.0 달성")
            fun `AAA 기준 대비율 7점0을 달성한다`() {
                // given
                val baseColor = "#cccccc"
                val textColor = "#ffffff"
                val targetRatio = ColorContrastCalculator.MIN_CONTRAST_AAA

                // when
                val result = calculator.adjustColorForContrast(baseColor, textColor, targetRatio)

                // then
                assertTrue(result.finalRatio!! >= ColorContrastCalculator.MIN_CONTRAST_AAA)
                assertTrue(result.meetsAAA)
            }
        }

        @Nested
        @DisplayName("결과 데이터 검증")
        inner class ResultDataValidationTest {

            @Test
            @DisplayName("결과 객체의 모든 필드가 올바르게 설정됨")
            fun `결과 객체의 모든 필드가 올바르게 설정된다`() {
                // given
                val baseColor = "#808080"
                val textColor = "#909090"
                val targetRatio = 4.5

                // when
                val result = calculator.adjustColorForContrast(baseColor, textColor, targetRatio)

                // then
                assertEquals(baseColor, result.originalColor)
                assertEquals(textColor, result.textColor)
                assertEquals(targetRatio, result.targetRatio)
                assertNotNull(result.originalRatio)
                assertNotNull(result.adjustedColor)
                assertNotNull(result.finalRatio)
                assertTrue(result.originalRatio > 0)

                if (result.adjustmentNeeded) {
                    assertTrue(result.finalRatio!! >= targetRatio)
                }
            }

            @Test
            @DisplayName("원본 대비율이 올바르게 계산됨")
            fun `원본 대비율이 올바르게 계산된다`() {
                // given
                val baseColor = "#ffffff"
                val textColor = "#cccccc"
                val targetRatio = 4.5

                // when
                val result = calculator.adjustColorForContrast(baseColor, textColor, targetRatio)
                val manualRatio = calculator.calculateContrastRatio(
                    calculator.hexToRgb(baseColor),
                    calculator.hexToRgb(textColor)
                )

                // then
                assertEquals(manualRatio.roundTo(2), result.originalRatio, 0.01)
            }
        }
    }

    @Nested
    @DisplayName("극한 케이스 테스트")
    inner class EdgeCaseTest {

        @Test
        @DisplayName("매우 높은 대비율 요구사항")
        fun `매우 높은 대비율을 요구할 때 최선의 결과를 반환한다`() {
            // given
            val baseColor = "#808080"
            val textColor = "#909090"
            val targetRatio = 15.0

            // when
            val result = calculator.adjustColorForContrast(baseColor, textColor, targetRatio)

            // then
            assertTrue(result.adjustmentNeeded)
            assertNotNull(result.finalRatio)
            assertTrue(result.finalRatio!! > result.originalRatio)
        }

        @Test
        @DisplayName("유사한 색상으로 대비율 조정")
        fun `매우 유사한 색상들로 대비율을 조정한다`() {
            // given
            val baseColor = "#808080"
            val textColor = "#818181"
            val targetRatio = 4.5

            // when
            val result = calculator.adjustColorForContrast(baseColor, textColor, targetRatio)

            // then
            assertTrue(result.adjustmentNeeded)
            assertTrue(result.finalRatio!! >= targetRatio)
        }
    }

    @Nested
    @DisplayName("성능 테스트")
    inner class PerformanceTest {

        @Test
        @DisplayName("대량 색상 변환 성능")
        fun `대량의 색상 변환이 합리적인 시간 내에 완료된다`() {
            // given
            val colors = (0..1000).map { "#${String.format("%06x", it * 255)} " }
            val startTime = System.currentTimeMillis()

            // when
            colors.forEach { color ->
                calculator.hexToRgb(color.trim())
            }
            val endTime = System.currentTimeMillis()

            // then
            val duration = endTime - startTime
            assertTrue(duration < 1000, "1000개 색상 변환이 1초 이내에 완료되어야 함")
        }

        @Test
        @DisplayName("대량 대비율 계산 성능")
        fun `대량의 대비율 계산이 합리적인 시간 내에 완료된다`() {
            // given
            val baseColor = "#ffffff"
            val testColors = (0..100).map { "#${String.format("%06x", it * 65535)}" }
            val startTime = System.currentTimeMillis()

            // when
            testColors.forEach { color ->
                calculator.adjustColorForContrast(baseColor, color, 4.5)
            }
            val endTime = System.currentTimeMillis()

            // then
            val duration = endTime - startTime
            assertTrue(duration < 5000, "100개 대비율 조정이 5초 이내에 완료되어야 함")
        }
    }

    @Nested
    @DisplayName("유틸리티 함수 테스트")
    inner class UtilityFunctionTest {

        @Test
        @DisplayName("Double roundTo 확장 함수 테스트")
        fun `Double roundTo 확장 함수가 올바르게 반올림한다`() {
            // given
            val value = 3.14159

            // when & then
            assertEquals(3.14, value.roundTo(2))
            assertEquals(3.1, value.roundTo(1))
            assertEquals(3.0, value.roundTo(0))
        }

        @Test
        @DisplayName("상수값 검증")
        fun `클래스 상수값들이 올바르게 설정되어 있다`() {
            // then
            assertEquals(4.5, ColorContrastCalculator.MIN_CONTRAST_AA)
            assertEquals(7.0, ColorContrastCalculator.MIN_CONTRAST_AAA)
        }
    }

    @Nested
    @DisplayName("실제 웹 접근성 시나리오")
    inner class WebAccessibilityScenarioTest {

        @Test
        @DisplayName("일반적인 웹사이트 색상 조합")
        fun `일반적인 웹사이트 색상 조합을 테스트한다`() {
            // given
            val scenarios = listOf(
                "#3498db" to "#ffffff", // 파란색 배경에 흰색 텍스트
                "#e74c3c" to "#ffffff", // 빨간색 배경에 흰색 텍스트
                "#2ecc71" to "#ffffff", // 초록색 배경에 흰색 텍스트
                "#f39c12" to "#000000", // 주황색 배경에 검은색 텍스트
                "#9b59b6" to "#ffffff" // 보라색 배경에 흰색 텍스트
            )

            scenarios.forEach { (bgColor, textColor) ->
                // when
                val result = calculator.adjustColorForContrast(bgColor, textColor, 4.5)

                // then
                if (result.adjustmentNeeded) {
                    assertTrue(
                        result.finalRatio!! >= 4.5,
                        "색상 조합 $bgColor-$textColor 의 조정된 대비율이 4.5 이상이어야 함"
                    )
                } else {
                    assertTrue(
                        result.originalRatio >= 4.5,
                        "색상 조합 $bgColor-$textColor 의 원본 대비율이 4.5 이상이어야 함"
                    )
                }
            }
        }

        @Test
        @DisplayName("브랜드 색상 접근성 개선")
        fun `브랜드 색상의 접근성을 개선한다`() {
            // given
            val brandColor = "#FF6B35" // 브랜드 주황색
            val textColor = "#333333"

            // when
            val result = calculator.adjustColorForContrast(brandColor, textColor, 4.5)

            // then
            assertTrue(result.finalRatio!! >= 4.5)
            assertTrue(result.meetsAA)

            // 조정된 색상이 HEX 형식인지 확인
            assertTrue(result.adjustedColor.matches(Regex("^#[0-9a-fA-F]{6}$")))
        }
    }
}
