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
    //return this.all { char -> char.isDigit() }
    //return this.matches("-?[0-9]+(\\.[0-9]+)?".toRegex())
    return this.matches("^\\d+$".toRegex())
}

fun String.isHangul(): Boolean {
    val regex = "\\u1100-\\u11FF" +
        "ㄱ-ㅎㅏ-ㅣ" +
        "\\u3165-\\u318E" +
        "\\uD7B0-\\uD7FB" +
        "\\uA960-\\uA97C" +
        "가-힣" +
        "\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D"
    return this.matches("^[$regex]+$".toRegex())
}

fun String.validate(isDigit: Boolean = false, isUpperCase: Boolean = false, isLowerCase: Boolean = false, isSpecialChars: Boolean = false): Int {
    val conditions = arrayListOf<Boolean>()

    if (isDigit) {
        conditions.add(this.any { it.isDigit() }) // 숫자 포함
    }
    if (isUpperCase) {
        conditions.add(this.any { it.isUpperCase() }) // 영문 대문자 포함
    }
    if (isLowerCase) {
        conditions.add(this.any { it.isLowerCase() }) // 영문 소문자 포함
    }
    if (isSpecialChars) {
        conditions.add(this.any { !it.isLetterOrDigit() }) // 특수문자 포함 (영문자와 숫자를 제외한 문자)
    }

    // 조건 중 true인 것의 개수를 세어 n 개 조건 이상인지 확인
    return conditions.count { it }
}
