package com.snc.test.core.extensions.text

import com.snc.zero.core.extensions.text.toHexString
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HexStringExtTest : BaseJUnit5Test() {

    @Test
    fun `ToHexString 테스트 1`() {
        // given
        val data = "abcdefABCDEF"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 1: data -> $v" }
        assertEquals("616263646566414243444546", v)
    }

    @Test
    fun `ToHexString 테스트 2`() {
        // given
        val data = "1234567890"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 2: data -> $v" }
        assertEquals("31323334353637383930", v)
    }

    @Test
    fun `ToHexString 테스트 3`() {
        // given
        val data = " !@#$%^&*()"
        // when
        val v = data.toHexString()
        // then
        logger.debug { "ToHexString 3: data -> $v" }
        assertEquals("2021402324255e262a2829", v)
    }
}
