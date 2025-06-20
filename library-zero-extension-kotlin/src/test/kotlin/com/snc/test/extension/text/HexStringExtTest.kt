package com.snc.test.extension.text

import com.snc.zero.extension.text.toHexString
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HexStringExtTest : BaseJUnit5Test() {

    @Test
    fun `ToHexString 테스트 - String 1`() {
        // given
        val data = "abcdefABCDEF"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 1: data -> $v" }
        assertEquals("616263646566414243444546", v)
    }

    @Test
    fun `ToHexString 테스트 - String 2`() {
        // given
        val data = "1234567890"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 2: data -> $v" }
        assertEquals("31323334353637383930", v)
    }

    @Test
    fun `ToHexString 테스트 - String 3`() {
        // given
        val data = " !@#$%^&*()"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 3: data -> $v" }
        assertEquals("2021402324255e262a2829", v)
    }

    @Test
    fun `ToHexString 테스트 - Char 1`() {
        val v = 0x60.toChar().toHexString()
        assertEquals("60", v)
    }

    @Test
    fun `ToHexString 테스트 - Char 2`() {
        val v = 0xAC00.toChar().toHexString()
        assertEquals("ac00", v)
    }
}
