package com.snc.test.passgen

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.passgen.PwValidator
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class PwValidatorTest : BaseJUnit5Test() {

    @Test
    fun testPasswordValidator() {
        val r1 = PwValidator.validate("123456")
        logger.debug { r1 }

        val r2 = PwValidator.validate("12a3456123456")
        logger.debug { r2 }

        val r3 = PwValidator.validate("1234A56123456")
        logger.debug { r3 }

        val r4 = PwValidator.validate("1a2V3456123456")
        logger.debug { r4 }

        val r5 = PwValidator.validate("1a2V3456123@456")
        logger.debug { r5 }
    }
}
