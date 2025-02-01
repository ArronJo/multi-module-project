package com.snc.zero.prizedraw

import com.snc.zero.test.base.BaseJUnit5Test
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
}
