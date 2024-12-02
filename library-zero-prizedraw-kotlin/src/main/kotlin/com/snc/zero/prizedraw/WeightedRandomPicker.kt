package com.snc.zero.prizedraw

import kotlin.random.Random

data class Item(
    val name: String,
    val weight: Double
)

class WeightedRandomPicker {
    private val items = mutableListOf<Item>()

    fun addItem(name: String, weight: Double) {
        items.add(Item(name, weight))
    }

    fun pickItem(): String? {
        if (items.isEmpty()) { return null }

        val totalWeight = items.sumOf { it.weight }
        val randomValue = Random.nextDouble() * totalWeight
        var accumulatedWeight = 0.0

        for (item in items) {
            accumulatedWeight += item.weight
            if (randomValue < accumulatedWeight) {
                return item.name
            }
        }

        // 이 부분에 도달할 가능성은 매우 낮지만, 부동소수점 오차로 인해 발생할 수 있습니다.
        return items.last().name
    }

    fun getItems(): List<Item> {
        return items.toList()
    }
}
