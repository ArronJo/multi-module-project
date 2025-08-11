package com.snc.zero.extensions.text

fun String.safeSubstring(startIndex: Int, endIndex: Int = this.length): String {
    val safeStart = maxOf(0, minOf(startIndex, this.length))
    val safeEnd = maxOf(safeStart, minOf(endIndex, this.length))
    return this.substring(safeStart, safeEnd)
}

fun String.removeAllWhitespace(): String = this.replace(Regex("\\s+"), "")
