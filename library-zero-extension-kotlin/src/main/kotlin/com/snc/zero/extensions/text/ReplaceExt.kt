package com.snc.zero.extensions.text

/**
 * `\n` (Line Feed)와 `\r\n` (Carriage Return + Line Feed) 치환
 */
fun String.removeNewLine(): String {
    return this.replace(Regex("\\r?\\n"), "")
}

fun String.remove(regex: Regex): String {
    return this.replace(regex, "")
}

fun String.removePrefixSuffix(prefix: String, suffix: String): String {
    return this.removeSurrounding(prefix, suffix)
}
