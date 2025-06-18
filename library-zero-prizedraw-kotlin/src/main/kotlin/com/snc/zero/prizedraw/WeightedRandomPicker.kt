package com.snc.zero.prizedraw

import kotlin.random.Random

open class WeightedRandomPicker {
    private val items = mutableListOf<Item>()

    fun addItem(name: String, weight: Double) {
        items.add(Item(name, weight))
    }

    open fun getRandom(): Double {
        return Random.nextDouble()
    }

    fun pickItem(): String? {
        if (items.isEmpty()) return null

        val totalWeight = items.sumOf { it.weight }
        val randomValue = getRandom() * totalWeight
        var accumulatedWeight = 0.0
        for (item in items) {
            accumulatedWeight += item.weight
            if (randomValue < accumulatedWeight) {
                return item.name
            }
        }
        // 매우 낮은 확률로 여기 도달할 수 있음 (부동소수점 오차 등)
        return items.last().name
    }

    fun getItems(): List<Item> {
        return items.toList()
    }
}

data class Item(val name: String, val weight: Double)
