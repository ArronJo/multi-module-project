package com.snc.test.passgen

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.passgen.PwValidator
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    fun `Invalid password - 1`() {
        val result = PwValidator.validate("Abdef1@")
        assertFalse(result.success)
        assertEquals("Password must be 8 or more characters in length.", result.reason)
    }

    @Test
    fun `Invalid password - 2`() {
        val result = PwValidator.validate("Abdefgh1@")
        assertFalse(result.success)
        assertEquals("Password contains the illegal alphabetical sequence 'defgh'.", result.reason)
    }

    @Test
    fun `should pass when password meets all rules`() {
        val result = PwValidator.validate("Abㅌdef1@")
        assertTrue(result.success)
        assertEquals("", result.reason)
    }

    @Test
    fun `should fail when password is too short and trigger error message`() {
        val result = PwValidator.validate("A1@b") // too short
        assertFalse(result.success)
        assertTrue(result.reason.isNotBlank(), "에러 메시지가 있어야 함")
    }

    @Test
    fun `should fail when password has illegal sequence to cover message loop`() {
        val result = PwValidator.validate("abcdeF1@") // 'abcde' 시퀀스 포함
        assertFalse(result.success)
        assertTrue(result.reason.contains("alphabetical"), "시퀀스 관련 에러 포함해야 함")
    }
}
