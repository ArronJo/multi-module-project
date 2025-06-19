package com.snc.zero.passgen

import com.snc.zero.passgen.result.ValidateResult
import org.passay.*

/**
 * 비밀번호 검증기
 * https://www.passay.org/reference/
 */
class PwValidator private constructor() {

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
                CharacterRule(EnglishCharacterData.Special, 1),
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
                // Invalid password
                for (msg in validator.getMessages(result)) {
                    return ValidateResult(false, msg)
                }
            }
            // Password is valid
            return ValidateResult(true, "")
        }
    }
}
