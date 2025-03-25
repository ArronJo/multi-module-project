package com.snc.test.passgen

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.passgen.PwGenerator
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.WhitespaceRule

private val logger = TLogging.logger { }

class PwGeneratorTest : BaseJUnit5Test() {

    @Test
    fun testPasswordGenerator1() {
        val r1 = PwGenerator.generate(4)
        logger.debug { "PwGenerator: $r1" }

        val r2 = PwGenerator.generate(8)
        logger.debug { "PwGenerator: $r2" }

        val r3 = PwGenerator.generate(12)
        logger.debug { "PwGenerator: $r3" }

        val r4 = PwGenerator.generate(16)
        logger.debug { "PwGenerator: $r4" }

        val r5 = PwGenerator.generate(20)
        logger.debug { "PwGenerator: $r5" }
    }

    @Test
    fun testPasswordGenerator2() {
        val r1 = PwGenerator.generate(
            20,
            CharacterRule(EnglishCharacterData.Alphabetical, 1),
            CharacterRule(EnglishCharacterData.Digit, 1),
            CharacterRule(EnglishCharacterData.SpecialUnicode, 1),
            WhitespaceRule()
        )
        logger.debug { "PwGenerator: $r1" }
    }
}
