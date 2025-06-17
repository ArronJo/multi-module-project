package com.snc.zero.prizedraw

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class PrizeDrawingTest : BaseJUnit5Test() {

    @Test
    fun `PrizeDrawing - 1`() {
        val drawing = PrizeDrawing()

        // 경품 추가
        drawing.addPrize("스마트폰", 1, 5.0)
        drawing.addPrize("태블릿", 2, 10.0)
        drawing.addPrize("이어폰", 5, 20.0)
        drawing.addPrize("상품권", 10, 40.0)
        drawing.addPrize("알사탕", 1000, 90.0)

        // 추첨 실행
        repeat(20) {
            val result = drawing.drawPrize()
            println("추첨 결과: ${result ?: "꽝"}")
        }

        // 남은 경품 확인
        println("\n남은 경품:")
        drawing.getRemainingPrizes().forEach { prize ->
            println("${prize.name}: ${prize.quantity}개")
        }
    }

    private lateinit var prizeDrawing: PrizeDrawing

    @BeforeEach
    fun setUp() {
        prizeDrawing = PrizeDrawing()
    }

    @Test
    fun `addPrize should store prize correctly`() {
        prizeDrawing.addPrize("Gold", 5, 10.0)

        val remaining = prizeDrawing.getRemainingPrizes()
        assertEquals(1, remaining.size)
        assertEquals("Gold", remaining[0].name)
        assertEquals(5, remaining[0].quantity)
        assertEquals(10.0, remaining[0].weight)
    }

    @Test
    fun `drawPrize should return null if no prizes`() {
        assertNull(prizeDrawing.drawPrize())
    }

    @Test
    fun `drawPrize should return null if all prizes have zero weight`() {
        prizeDrawing.addPrize("Silver", 2, 0.0)
        prizeDrawing.addPrize("Bronze", 1, 0.0)

        assertNull(prizeDrawing.drawPrize())
    }

    @Test
    fun `drawPrize should reduce quantity when prize drawn`() {
        prizeDrawing.addPrize("Gold", 1, 100.0)

        val result = prizeDrawing.drawPrize()
        assertEquals("Gold", result)

        val remaining = prizeDrawing.getRemainingPrizes()
        assertTrue(remaining.isEmpty())
    }

    @RepeatedTest(100)
    fun `drawPrize should respect weights probabilistically`() {
        prizeDrawing.addPrize("A", 100, 90.0)
        prizeDrawing.addPrize("B", 100, 10.0)

        var countA = 0
        var countB = 0

        repeat(100) {
            when (prizeDrawing.drawPrize()) {
                "A" -> countA++
                "B" -> countB++
            }
        }

        // In 100 draws, "A" should appear significantly more due to weight
        assertTrue(countA > countB)
        assertEquals(100, countA + countB)
    }

    @Test
    fun `drawPrize should not return prize with 0 quantity`() {
        prizeDrawing.addPrize("A", 0, 100.0)
        assertNull(prizeDrawing.drawPrize())
    }

    @Test
    fun `getRemainingPrizes should return only prizes with quantity greater than 0`() {
        prizeDrawing.addPrize("A", 1, 50.0)
        prizeDrawing.addPrize("B", 0, 50.0)

        val remaining = prizeDrawing.getRemainingPrizes()
        assertEquals(1, remaining.size)
        assertEquals("A", remaining[0].name)
    }
}
