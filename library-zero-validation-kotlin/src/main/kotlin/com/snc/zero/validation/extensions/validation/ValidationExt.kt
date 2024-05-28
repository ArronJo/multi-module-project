package com.snc.zero.validation.extensions.validation

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
    return this.matches(Regex("^[\\u1100-\\u11FFㄱ-ㅎㅏ-ㅣ\\u3165-\\u318E\\uD7B0-\\uD7FB\\uA960-\\uA97C가-힣\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D]+$"))
}

fun String.validate(isDigit: Boolean = false, isUpperCase: Boolean = false, isLowerCase: Boolean = false, isSpecialChars: Boolean = false): Int {
    val conditions = arrayListOf<Boolean>()

    if (isDigit) {
        conditions.add(this.any { it.isDigit() })
    }
    if (isUpperCase) {
        conditions.add(this.any { it.isUpperCase() })
    }
    if (isLowerCase) {
        conditions.add(this.any { it.isLowerCase() })
    }
    if (isSpecialChars) {
        conditions.add(this.any { !it.isLetterOrDigit() })
    }

    return conditions.count { it }
}
