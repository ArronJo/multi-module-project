package com.snc.zero.validation.password

/**
 * 패스워드 검증기는 PwValidator.class 도 구현되어 있다.
 */
class PasswordValidator private constructor() {

    companion object {

        fun validate(str: String, isDigit: Boolean = false, isUpperCase: Boolean = false, isLowerCase: Boolean = false, isSpecialChars: Boolean = false): Int {
            val conditions = arrayListOf<Boolean>()

            if (isDigit) {
                conditions.add(str.any { it.isDigit() }) // 숫자 포함
            }
            if (isUpperCase) {
                conditions.add(str.any { it.isUpperCase() }) // 영문 대문자 포함
            }
            if (isLowerCase) {
                conditions.add(str.any { it.isLowerCase() }) // 영문 소문자 포함
            }
            if (isSpecialChars) {
                conditions.add(str.any { !it.isLetterOrDigit() }) // 특수문자 포함 (영문자와 숫자를 제외한 문자)
            }

            // 조건 중 true인 것의 개수를 세어 n 개 조건 이상인지 확인
            return conditions.count { it }
        }

        fun isValid(str: String, isDigit: Boolean = true, isUpperCase: Boolean = true, isLowerCase: Boolean = true, isSpecialChars: Boolean = true): Boolean {
            var rules = 0
            if (isDigit) { rules++ }
            if (isUpperCase) { rules++ }
            if (isLowerCase) { rules++ }
            if (isSpecialChars) { rules++ }

            val count = validate(str, isDigit = isDigit, isUpperCase = isUpperCase, isLowerCase = isLowerCase, isSpecialChars = isSpecialChars)
            return count >= rules
        }
    }
}
