package com.snc.zero.extensions.text

/**
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/pad-start.html
 *
 * length 길이가 전체 길이를 초과할 때 초과 영역에 대해 문자열 padding 처리 한다.
 */

fun Long.padStart(length: Int, padChar: Char = '0'): String {
    return this.toString().padStart(length = length, padChar = padChar)
}

fun Int.padStart(length: Int, padChar: Char = '0'): String {
    return this.toString().padStart(length = length, padChar = padChar)
}

fun Long.padEnd(length: Int, padChar: Char = '0'): String {
    return this.toString().padEnd(length = length, padChar = padChar)
}

fun Int.padEnd(length: Int, padChar: Char = '0'): String {
    return this.toString().padEnd(length = length, padChar = padChar)
}
