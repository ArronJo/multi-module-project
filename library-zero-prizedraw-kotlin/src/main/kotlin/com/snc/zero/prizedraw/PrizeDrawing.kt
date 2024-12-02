package com.snc.zero.prizedraw

import kotlin.random.Random

data class Prize(
    val name: String,
    var quantity: Int,
    val weight: Double
)

class PrizeDrawing {
    private val prizes = mutableListOf<Prize>()

    fun addPrize(name: String, quantity: Int, weight: Double) {
        prizes.add(Prize(name, quantity, weight))
    }

    fun drawPrize(): String? {
        val totalWeight = prizes.sumOf { it.weight }
        if (totalWeight == 0.0) { return null }

        val randomValue = Random.nextDouble() * totalWeight
        var accumulatedWeight = 0.0

        for (prize in prizes) {
            accumulatedWeight += prize.weight
            if (randomValue < accumulatedWeight && prize.quantity > 0) {
                prize.quantity--
                return prize.name
            }
        }

        return null
    }

    fun getRemainingPrizes(): List<Prize> {
        return prizes.filter { it.quantity > 0 }
    }
}
