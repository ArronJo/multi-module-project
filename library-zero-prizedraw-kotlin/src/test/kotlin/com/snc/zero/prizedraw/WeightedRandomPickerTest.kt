package com.snc.zero.prizedraw

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

class WeightedRandomPickerTest : BaseJUnit5Test() {

    @Test
    fun `PrizeDrawing - 1`() {
        val picker = WeightedRandomPicker()

        // 아이템 추가
        picker.addItem("상품A", 10.0)
        picker.addItem("상품B", 20.0)
        picker.addItem("상품C", 30.0)
        picker.addItem("상품D", 40.0)

        // 추첨 실행
        val pickCount = 1000
        val results = mutableMapOf<String, Int>()

        repeat(pickCount) {
            val result = picker.pickItem()
            results[result ?: "없음"] = results.getOrDefault(result, 0) + 1
        }

        // 결과 출력
        println("추첨 결과 ($pickCount 회):")
        results.forEach { (item, count) ->
            val percentage = count.toDouble() / pickCount * 100
            println("$item: $count 회 (${String.format("%.2f", percentage)}%)")
        }

        // 설정된 가중치 출력
        println("\n설정된 가중치:")
        picker.getItems().forEach { item ->
            println("${item.name}: ${item.weight}")
        }
    }
}
