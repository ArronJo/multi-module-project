package com.snc.test.color.palette

import com.snc.zero.color.palette.Color
import com.snc.zero.color.palette.ColorPaletteGenerator
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

@Suppress("NonAsciiCharacters")
class ColorPaletteGeneratorTest : BaseJUnit5Test() {
    private val generator = ColorPaletteGenerator()

    @Test
    fun `ColorPalette Generator - 파란색`() {
        // 예시 1: 파란색 기준 (Material Blue 500)
        println("=== 파란색 팔레트 ===")
        val blueBase = Color("#2196F3")
        val bluePalette = generator.generatePalette(blueBase)
        generator.printPalette(bluePalette)
        generator.printPaletteBlocks(bluePalette)

        // 특정 단계 색상 접근
        println("\n=== 특정 색상 접근 ===")
        println("파란색 300: ${bluePalette[300]?.toHex()}")
        println("파란색 700: ${bluePalette[700]?.toHex()}")
    }

    @Test
    fun `ColorPalette Generator - 빨간색`() {
        println("\n=== 빨간색 팔레트 ===")
        val redBase = Color("#F44336")
        val redPalette = generator.generatePalette(redBase)
        generator.printPalette(redPalette)
        generator.printPaletteBlocks(redPalette)
    }

    @Test
    fun `ColorPalette Generator - 녹색`() {
        println("\n=== 녹색 팔레트 ===")
        val greenBase = Color("#4CAF50")
        val greenPalette = generator.generatePalette(greenBase)
        generator.printPalette(greenPalette)
        generator.printPaletteBlocks(greenPalette)
    }

    @Test
    fun `ColorPalette Generator - 단일 색상 미리보기`() {
        // 단일 색상 미리보기 예시
        println("\n=== 단일 색상 미리보기 ===")
        val testColor = Color("#FF5722")
        val testPalette = generator.generatePalette(testColor)
        println("오렌지 팔레트:")
        generator.printPaletteBlocks(testPalette)
    }

    @Test
    fun `hex to RGB and back conversion should be consistent`() {
        val hex = "#1A2B3C"
        val color = Color(hex)
        assertEquals(26, color.r)
        assertEquals(43, color.g)
        assertEquals(60, color.b)
        assertEquals(hex.uppercase(), color.toHex())
    }

    @Test
    fun `RGB to HSL and back should be roughly reversible`() {
        val original = Color(26, 43, 60)
        val hsl = original.toHsl()
        val converted = hsl.toColor()

        // RGB 변환 오차는 반올림 기준 허용
        assertTrue(abs(original.r - converted.r) <= 1)
        assertTrue(abs(original.g - converted.g) <= 1)
        assertTrue(abs(original.b - converted.b) <= 1)
    }

    @Test
    fun `generatePalette should contain all 100 to 900 steps`() {
        val generator = ColorPaletteGenerator()
        val palette = generator.generatePalette(Color("#2196F3")) // blue

        for (step in 100..900 step 100) {
            assertTrue(palette.containsKey(step), "Missing step: $step")
        }
    }

    @Test
    fun `500 step in palette should be same as base color`() {
        val base = Color("#FF5722") // Deep Orange
        val generator = ColorPaletteGenerator()
        val palette = generator.generatePalette(base)

        val color500 = palette[500]
        assertNotNull(color500)
        assertEquals(base.r, color500?.r)
        assertEquals(base.g, color500?.g)
        assertEquals(base.b, color500?.b)
    }

    @Test
    fun `100 step should be significantly lighter than 500`() {
        val base = Color("#4CAF50") // Green
        val generator = ColorPaletteGenerator()
        val palette = generator.generatePalette(base)

        val color100 = palette[100]!!
        val color500 = palette[500]!!

        val lightness100 = color100.toHsl().l
        val lightness500 = color500.toHsl().l

        assertTrue(lightness100 > lightness500, "100 should be lighter than 500")
    }

    @Test
    fun `900 step should be significantly darker than 500`() {
        val base = Color("#03A9F4") // Light Blue
        val generator = ColorPaletteGenerator()
        val palette = generator.generatePalette(base)

        val color900 = palette[900]!!
        val color500 = palette[500]!!

        val lightness900 = color900.toHsl().l
        val lightness500 = color500.toHsl().l

        assertTrue(lightness900 < lightness500, "900 should be darker than 500")
    }

    @Test
    fun `isLightColor should return true for light colors and false for dark`() {
        val generator = ColorPaletteGenerator()
        val light = Color(240, 240, 240)
        val dark = Color(20, 20, 20)

        val isLight1 = generator.run {
            val method = ColorPaletteGenerator::class.java.getDeclaredMethod("isLightColor", Color::class.java)
            method.isAccessible = true
            method.invoke(this, light) as Boolean
        }

        val isLight2 = generator.run {
            val method = ColorPaletteGenerator::class.java.getDeclaredMethod("isLightColor", Color::class.java)
            method.isAccessible = true
            method.invoke(this, dark) as Boolean
        }

        assertTrue(isLight1)
        assertFalse(isLight2)
    }
}
