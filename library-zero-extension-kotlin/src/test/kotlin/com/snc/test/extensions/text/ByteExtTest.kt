package com.snc.test.extensions.text

import com.snc.zero.extensions.text.toBytes
import com.snc.zero.extensions.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class ByteExtTest : BaseJUnit5Test() {

    @Test
    fun `toBytes 테스트 1`() {
        // given
        val data = "616263646566414243444546"
        // when
        val b = data.toBytes()
        val v = String(b)
        // then
        logger.debug { "toBytes 1: data -> ${b.print()} -> $v" }
        assertEquals("abcdefABCDEF", v)
    }

    @Test
    fun `toBytes 테스트 2`() {
        // given
        val data = "31323334353637383930"
        // when
        val b = data.toBytes()
        val v = String(b)
        // then
        logger.debug { "toBytes 2: data -> ${b.print()} -> $v" }
        assertEquals("1234567890", v)
    }

    @Test
    fun `toBytes 테스트 3`() {
        // given
        val data = "2021402324255e262a2829"
        // when
        val b = data.toBytes()
        val v = String(b)
        // then
        logger.debug { "toBytes 3: data -> ${b.print()} -> $v" }
        assertEquals(" !@#\$%^&*()", v)
    }
}
