package com.snc.zero.extensions.validation

fun String.isLetter(): Boolean {
    return this.matches("^[a-zA-Z]+$".toRegex())
}

fun String.isUpperCase(): Boolean {
    return this.matches("^[A-Z]+$".toRegex())
}

fun String.isLowerCase(): Boolean {
    return this.matches("^[a-z]+$".toRegex())
}

fun String.isNumber(): Boolean {
    return this.matches("^\\d+$".toRegex())
}

fun String.isHangul(): Boolean {
    val regex = "\\u1100-\\u11FF" + // "ㄱ-ᇹ"
        "\\u3131-\\u318E" + // "ㄱ-ㆎ"
        "\\uA960-\\uA97C" + // Hangul Jamo Extended-A (한글 자모 확장-A)
        "\\uAC00-\\uD7A3" + // "가-힣"
        "\\uD7B0-\\uD7FB" + // Hangul Jamo Extended-B (한글 자모 확장-B)
        "\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D" // 아래아
    return this.matches("^[$regex]+$".toRegex())
}
