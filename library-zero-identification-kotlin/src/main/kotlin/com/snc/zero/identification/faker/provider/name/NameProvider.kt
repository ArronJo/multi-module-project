package com.snc.zero.identification.faker.provider.name

import java.util.*
import kotlin.math.ln

open class NameProvider {

    protected val lastNames = mutableMapOf<String, Double>()
    protected val firstNamesFemale = mutableListOf<String>()
    protected val firstNamesMale = mutableListOf<String>()

    fun generateLastName(): String {
        return getWeightedRandom(lastNames)
    }

    fun generateFemaleName(): String {
        val target = firstNamesFemale
        return target[getRandomInt(max = target.size - 1)]
    }

    fun generateMaleName(): String {
        val target = firstNamesMale
        return target[getRandomInt(max = target.size - 1)]
    }


    private val random = SplittableRandom()

    private fun getRandomInt(min: Int = 0, max: Int): Int {
        return random.nextInt(max - min + 1) + min
    }

    private fun getWeightedRandom(map: MutableMap<String, Double>): String {
        var result = ""
        var bestValue = Double.MAX_VALUE
        for (element in map.keys) {
            val value = -ln(random.nextDouble()) / map.getOrDefault(element, 0.0)
            if (value < bestValue) {
                bestValue = value
                result = element
            }
        }
        return result
    }
}