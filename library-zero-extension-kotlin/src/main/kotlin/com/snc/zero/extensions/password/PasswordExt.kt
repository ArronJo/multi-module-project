package com.snc.zero.extensions.password

/**
 * library-zero-password-generator 에도 비밀번호 검증 클래스가 있다.
 */
fun String.validatePassword(): Boolean {
    // 조건 중 true인 것의 개수를 세어 n 개 조건 이상인지 확인
    val validConditions = arrayListOf<Boolean>()
    validConditions.add(this.any { it.isDigit() }) // 숫자 포함
    validConditions.add(this.any { it.isUpperCase() }) // 영문 대문자 포함
    validConditions.add(this.any { it.isLowerCase() }) // 영문 소문자 포함
    validConditions.add(this.any { !it.isLetterOrDigit() }) // 특수문자 포함 (영문자와 숫자를 제외한 문자)
    return validConditions.count { it } >= 4
}
