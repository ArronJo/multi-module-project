package com.snc.test.color.palette

import com.snc.zero.color.palette.Color
import com.snc.zero.color.palette.ColorPaletteGenerator
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

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
}
