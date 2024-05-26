package com.snc.zero.core.extensions.random

import java.security.SecureRandom
import java.util.*
import kotlin.math.ln

fun <T> List<T>.getRandomItem(): T {
    val index = if (this.size > 1) {
        val max = size - 1
        val min = 0
        SecureRandom().nextInt(max - min + 1) + min
    } else 0
    return get(index)
}

fun <T> MutableMap<T, Double>.getWeightedRandom(): T? {
    var result: T? = null
    var bestValue = Double.MAX_VALUE
    val rand = SecureRandom()
    for (element in this.keys) {
        val value = -ln(rand.nextDouble()) / (this[element] ?: 0.0)
        if (value < bestValue) {
            bestValue = value
            result = element
        }
    }
    return result
}
