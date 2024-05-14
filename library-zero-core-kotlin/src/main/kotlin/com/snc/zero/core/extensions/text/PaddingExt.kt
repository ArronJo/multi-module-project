package com.snc.zero.core.extensions.text

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