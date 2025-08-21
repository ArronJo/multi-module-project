package com.snc.zero.extensions.password

import com.snc.zero.extensions.validation.validate

/**
 * library-zero-password-generator 에도 비밀번호 검증 클래스가 있다.
 */
fun String.validatePassword(): Boolean {
    // 조건 중 true인 것의 개수를 세어 n 개 조건 이상인지 확인
    val validConditions = this.validate(isDigit = true, isUpperCase = true, isLowerCase = true, isSpecialChars = true)
    return validConditions >= 4
}
