package com.snc.test.extension.text

import com.snc.zero.extension.text.padEnd
import com.snc.zero.extension.text.padStart
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class PaddingExtTest : BaseJUnit5Test() {

    @Test
    fun `Padding Left - Int`() {
        // given
        val data = 7
        // when
        val v1 = data.padStart(length = 2, padChar = '0')
        // then
        logger.debug { "padStart - Int 결과: $data -> $v1" }
        assertEquals("07", v1)
    }

    @Test
    fun `Padding Left - Long`() {
        // given
        val data = 7L
        // when
        val v1 = data.padStart(length = 2, padChar = '0')
        // then
        logger.debug { "padStart - Long 결과: $data -> $v1" }
        assertEquals("07", v1)
    }

    @Test
    fun `Padding Left - String`() {
        // given
        val data = "abc123abc"
        // when
        val v1 = data.padStart(length = 13, padChar = '_')
        // then
        logger.debug { "padStart - String 결과: $data -> $v1" }
        assertEquals("____abc123abc", v1)
    }

    @Test
    fun `Padding Right - Int`() {
        // given
        val data = 7
        // when
        val v1 = data.padEnd(length = 2, padChar = '0')
        // then
        logger.debug { "padEnd - Int 결과: $data -> $v1" }
        assertEquals("70", v1)
    }

    @Test
    fun `Padding Right - Long`() {
        // given
        val data = 7L
        // when
        val v1 = data.padEnd(length = 2, padChar = '0')
        // then
        logger.debug { "padEnd - Long 결과: $data -> $v1" }
        assertEquals("70", v1)
    }

    @Test
    fun `Padding Right - String`() {
        // given
        val data = "abc123abc"
        // when
        val v1 = data.padEnd(length = 13, padChar = '_')
        // then
        logger.debug { "padEnd - String 결과: $data -> $v1" }
        assertEquals("abc123abc____", v1)
    }

    @Test
    fun `Padding All`() {
        logger.debug { "all: ${1.padStart(2)}" }
        logger.debug { "all: ${10L.padStart(2)}" }
        logger.debug { "all: ${1.padEnd(2)}" }
        logger.debug { "all: ${10L.padEnd(2)}" }
    }
}
