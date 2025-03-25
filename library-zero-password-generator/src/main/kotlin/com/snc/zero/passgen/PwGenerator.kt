package com.snc.zero.passgen

import org.passay.*
import java.security.SecureRandom

/**
 * 비밀번호 생성시
 * https://www.passay.org/reference/
 */
class PwGenerator {
    companion object {
        private val gen = PasswordGenerator(SecureRandom())

        fun generate(length: Int): String {
            return gen.generatePassword(
                length,
                listOf(
                    CharacterRule(EnglishCharacterData.UpperCase, 1),
                    CharacterRule(EnglishCharacterData.LowerCase, 1),
                    CharacterRule(EnglishCharacterData.Digit, 1),
                    CharacterRule(EnglishCharacterData.SpecialAscii, 1),
                    WhitespaceRule()
                )
            )
        }

        fun <T : Rule?> generate(length: Int, vararg rules: T): String {
            return gen.generatePassword(length, *rules)
        }
    }
}
