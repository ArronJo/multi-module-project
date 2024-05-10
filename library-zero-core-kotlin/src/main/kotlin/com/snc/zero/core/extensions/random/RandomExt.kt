package com.snc.zero.core.extensions.random

import java.util.*

fun randomInt(min: Int = 0, bound: Int): Int {
    val random = SplittableRandom()
    return random.nextInt(bound) + min
}

fun randomString(
    length: Int,
    isDigit: Boolean = false,
    isUpperCase: Boolean = false,
    isLowerCase: Boolean = false,
    isLetter: Boolean = false,
    isHangul: Boolean = false
): String {
    val lowercase = ('a'..'z')
    val uppercase = ('A'..'Z')
    val digit = ('0'..'9')
    val hangul = ('가'..'힣')

    val charsets = mutableListOf<Char>()
    if (isLowerCase || isLetter) {
        charsets.addAll(lowercase)
    }
    if (isUpperCase || isLetter) {
        charsets.addAll(uppercase)
    }
    if (isDigit) {
        charsets.addAll(digit)
    }
    if (isHangul) {
        charsets.addAll(hangul)
    }
    if (charsets.isEmpty()) {
        charsets.addAll(lowercase)
        charsets.addAll(uppercase)
        charsets.addAll(digit)
    }
    return (1..length)
        .map { charsets.random() }
        .joinToString("")
}
