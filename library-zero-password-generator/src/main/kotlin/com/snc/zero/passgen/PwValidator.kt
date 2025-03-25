package com.snc.zero.passgen

import com.snc.zero.passgen.result.ValidateResult
import org.passay.*

/**
 * 비밀번호 검증기
 * https://www.passay.org/reference/
 */
class PwValidator {
    /*
    // ConstraintValidator : Spring 클래스
    class PasswordConstraintsValidator : ConstraintValidator<Password?, String?> {
        override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
            val passwordValidator = PasswordValidator(
                listOf( // 비밀번호 길이가 8~20 사이여야 한다.
                    LengthRule(8, 20), // 적어도 하나의 대문자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.UpperCase, 1), // 적어도 하나의 소문자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.LowerCase, 1), // 적어도 하나의 숫자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.Digit, 1),  // 적어도 하나의 특수문자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.Special, 1), // 공백문자는 허용하지 않는다.
                    WhitespaceRule()
                )
            )
            val result = passwordValidator.validate(PasswordData(password))
            if (result.isValid) {
                return true
            }

            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                java.lang.String.join(
                    ",",
                    passwordValidator.getMessages(result)
                )
            )
                .addConstraintViolation()
            return false
        }
    }
     */
    companion object {
        fun validate(password: String): ValidateResult {
            val validator = PasswordValidator(
                // length between 8 and 16 characters
                LengthRule(8, 16),
                // at least one upper-case character
                CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                CharacterRule(EnglishCharacterData.Digit, 1),
                // at least one symbol (special character)
                CharacterRule(
                    EnglishCharacterData.Special,
                    1
                ),
                // define some illegal sequences that will fail when >= 5 chars long
                // alphabetical is of the form 'abcde', numerical is '34567', qwery is 'asdfg'
                // the false parameter indicates that wrapped sequences are allowed; e.g. 'xyzabc'
                IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                // no whitespace
                WhitespaceRule()
            )

            val result = validator.validate(PasswordData(password))
            if (!result.isValid) {
                println("Invalid password:")
                for (msg in validator.getMessages(result)) {
                    println(msg)
                    return ValidateResult(false, msg)
                }
            }
            println("Password is valid")
            return ValidateResult(true, "")
        }
    }
}
