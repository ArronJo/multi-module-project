package com.snc.test.crypto.encoder

import com.snc.zero.crypto.encoder.Encoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class EncoderTest : BaseJUnit5Test() {

    @Test
    fun `Encode To Base64`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Encoder.with(Encoder.Algo.BASE64).encode(data.toByteArray())
        val v2 = java.util.Base64.getEncoder().encodeToString(data.toByteArray())
        // then
        logger.debug { "Base64 encoded 1: $v" }
        logger.debug { "Base64 encoded 2: $v2" }
        assertEquals(v, v2)
    }

    @Test
    fun `Encode To Base64 (Safe URL)`() {
        // given
        val data = "Special chars: \u00fb\u00fb\u00ff here."
        // when
        val v = Encoder.with(Encoder.Algo.BASE64).encode(data.toByteArray())
        val v2 = String(java.util.Base64.getEncoder().encode(data.toByteArray()))
        // then
        logger.debug { "Base64 encoded 1: $v" }  // "U3BlY2lhbCBjaGFyczogw7vDu8O_IGhlcmUu"
        logger.debug { "Base64 encoded 2: $v2" } // "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"
        assertNotEquals(v, v2)
    }

    @Test
    fun `Encode 테스트 1-1`() {
        // given
        val data = "aaaaa"
        // when
        val v1 = Encoder.with().encode(data.toByteArray())
        val v2 = String(java.util.Base64.getEncoder().encode(data.toByteArray()))
        // then
        assertEquals(v1, v2)
    }
}