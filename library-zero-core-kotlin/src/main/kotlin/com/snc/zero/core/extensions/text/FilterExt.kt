package com.snc.zero.core.extensions.text

fun String.filter(
    number: Boolean = false,
    letter: Boolean = false,
    lowercase: Boolean = false,
    uppercase: Boolean = false,
    korean: Boolean = false,
    special: Boolean = false
): String {
    val arr = mutableListOf<String>()
    if (number) {
        arr.add("0-9")
    }
    if (lowercase || letter) {
        arr.add("a-z")
    }
    if (uppercase || letter) {
        arr.add("A-Z")
    }
    if (korean) {
        arr.add("\\u1100-\\u11FFㄱ-ㅎㅏ-ㅣ\\u3165-\\u318E\\uD7B0-\\uD7FB\\uA960-\\uA97C가-힣\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D")
    }
    if (special) {
        arr.add("!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?`~")
    }
    if (arr.isEmpty()) {
        return this
    }
    val filter = arr.joinToString(prefix = "[^", separator = "", postfix = "]")
    return this.filter(regex = filter.toRegex())
}

fun String.filter(regex: Regex): String {
    return this.replace(regex = regex, replacement = "")
}
