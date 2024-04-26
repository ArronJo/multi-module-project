package com.snc.zero.validation.extensions.password

import com.snc.zero.validation.extensions.validation.validate

fun String.validatePassword(): Boolean {
    val validConditions = this.validate(isDigit = true, isUpperCase = true, isLowerCase = true, isSpecialChars = true)
    return validConditions >= 4
}
