package com.snc.zero.core.extensions.random

import java.util.*
import kotlin.jvm.Throws
import kotlin.math.ln

@Throws(IllegalFormatException::class)
fun <T> List<T>.getRandomItem(): T {
    val max = size - 1
    val min = 0
    val index = SplittableRandom().nextInt(max - min + 1) + min
    return get(index)
}

fun <T> MutableMap<T, Double>.getWeightedRandom(): T? {
    var result: T? = null
    var bestValue = Double.MAX_VALUE
    val rand = SplittableRandom()
    for (element in this.keys) {
        val value = -ln(rand.nextDouble()) / this.getOrDefault(element, 0.0)
        if (value < bestValue) {
            bestValue = value
            result = element
        }
    }
    return result
}
