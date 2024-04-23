package com.snc.test.core.extensions.text

import com.snc.zero.core.extensions.text.padEnd
import com.snc.zero.core.extensions.text.padStart
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class PaddingExtTest : BaseTest() {

    @Test
    fun `Padding Left - Int`() {
        // given
        val data = 7
        // when
        val v1 = data.padStart(length = 2, padChar = '0')
        // then
        logger.debug { "padStart - Int 결과: $data -> $v1" }
        assertEquals(v1, "07")
    }

    @Test
    fun `Padding Left - Long`() {
        // given
        val data = 7L
        // when
        val v1 = data.padStart(length = 2, padChar = '0')
        // then
        logger.debug { "padStart - Long 결과: $data -> $v1" }
        assertEquals(v1, "07")
    }

    @Test
    fun `Padding Left - String`() {
        // given
        val data = "abc123abc"
        // when
        val v1 = data.padStart(length = 13, padChar = '_')
        // then
        logger.debug { "padStart - String 결과: $data -> $v1" }
        assertEquals(v1, "____abc123abc")
    }

    @Test
    fun `Padding Right - Int`() {
        // given
        val data = 7
        // when
        val v1 = data.padEnd(length = 2, padChar = '0')
        // then
        logger.debug { "padEnd - Int 결과: $data -> $v1" }
        assertEquals(v1, "70")
    }

    @Test
    fun `Padding Right - Long`() {
        // given
        val data = 7L
        // when
        val v1 = data.padEnd(length = 2, padChar = '0')
        // then
        logger.debug { "padEnd - Long 결과: $data -> $v1" }
        assertEquals(v1, "70")
    }

    @Test
    fun `Padding Right - String`() {
        // given
        val data = "abc123abc"
        // when
        val v1 = data.padEnd(length = 13, padChar = '_')
        // then
        logger.debug { "padEnd - String 결과: $data -> $v1" }
        assertEquals(v1, "abc123abc____")
    }
}