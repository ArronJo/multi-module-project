package com.snc.test.color.contrast

import com.snc.zero.color.contrast.ColorContrastCalculator
import com.snc.zero.color.contrast.roundTo
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

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
            println("택스트 색상: ${result.textColor}  ${getColorPreview(result.textColor)}")
            println("원본 색상: ${result.originalColor}  ${getColorPreview(result.originalColor, result.textColor)}")
            println("조정된 색상: ${result.adjustedColor}  ${getColorPreview(result.adjustedColor, result.textColor)}")
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
                println("원본 색상: $baseColor  ${getColorPreview(baseColor)}")

                // When
                val result = calculator.adjustColorForContrast(baseColor)

                // Then
                assertNotNull(result, "조정 결과가 null이면 안됩니다")
                assertNotNull(result.adjustedColor, "조정된 색상이 null이면 안됩니다")
                assertTrue(result.originalRatio > 0, "원본 대비율은 0보다 커야 합니다")
                assertTrue(result.finalRatio != null, "최종 대비율이 설정되어야 합니다")

                println("조정된 색상: ${result.adjustedColor}  ${getColorPreview(result.adjustedColor)}")
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
}
