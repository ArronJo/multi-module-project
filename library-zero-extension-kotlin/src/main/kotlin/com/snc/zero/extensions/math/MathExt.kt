package com.snc.zero.extensions.math

import kotlin.math.pow
import kotlin.math.round

// v: 반올림할 숫자
// decimalPlaces: 반올림할 소수점 자리수
fun Double.roundToDecimalPlaces(decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces)
    return round(this * factor) / factor
}
