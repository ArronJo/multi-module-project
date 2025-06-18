package com.snc.zero.prizedraw

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WeightedRandomPickerTest : BaseJUnit5Test() {

    @Test
    fun `WeightedRandomPicker - 1`() {
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

    @Test
    fun `WeightedRandomPicker - Empty`() {
        val picker = WeightedRandomPicker()
        println("pickItem : ${picker.pickItem()}")
    }

    private lateinit var picker: WeightedRandomPicker

    @BeforeEach
    fun setUp() {
        picker = WeightedRandomPicker()
    }

    @Test
    fun `pickItem returns null when no items are added`() {
        assertNull(picker.pickItem())
    }

    @Test
    fun `pickItem returns the only item if one is added`() {
        picker.addItem("A", 1.0)
        assertEquals("A", picker.pickItem())
    }

    @Test
    fun `pickItem returns items based on weight distribution`() {
        picker.addItem("A", 0.0)
        picker.addItem("B", 0.0)
        picker.addItem("C", 1.0)

        // C가 유일하게 weight > 0이므로 항상 선택되어야 함
        repeat(10) {
            assertEquals("C", picker.pickItem())
        }
    }

    @Test
    fun `pickItem returns item based on exact thresholds`() {
        picker = WeightedRandomPickerWithControlledRandom(0.6) // 중간값 지정
        picker.addItem("A", 0.5)
        picker.addItem("B", 0.5)
        picker.addItem("C", 0.5)

        // A(0.5), B(1.0), C(1.5) → 0.6은 B에 해당
        assertEquals("B", picker.pickItem())
    }

    @Test
    fun `pickItem handles floating point edge case returning last item`() {
        // totalWeight = 1.0, randomValue = 1.0 (경계값) → 부동소수점으로 인한 마지막 item 반환
        picker = WeightedRandomPickerWithControlledRandom(1.0)
        picker.addItem("A", 0.3)
        picker.addItem("B", 0.3)
        picker.addItem("C", 0.4)

        assertEquals("C", picker.pickItem()) // fallback 경로 테스트
    }

    // 유틸: Random 값을 고정하기 위한 테스트용 서브클래스
    class WeightedRandomPickerWithControlledRandom(private val controlledRandomValue: Double) : WeightedRandomPicker() {
        override fun getRandom(): Double {
            return controlledRandomValue
        }
    }
}
