package com.snc.zero.validation.extensions.password

import com.snc.zero.validation.extensions.validation.validate

fun String.validatePassword(): Boolean {
    // 조건 중 true인 것의 개수를 세어 n 개 조건 이상인지 확인
    val validConditions = this.validate(isDigit = true, isUpperCase = true, isLowerCase = true, isSpecialChars = true)
    return validConditions >= 4
}
