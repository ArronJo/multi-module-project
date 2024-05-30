package com.snc.test.crypto.decoder

import com.snc.zero.crypto.encoder.Decoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class DecoderTest : BaseJUnit5Test() {

    @Test
    fun `Decode To Base64`() {
        // given
        val data = "YXNkc25jeW5oIDI5ODR5aGQ4OWB5dTg5ODkxODl1OWpxZmRhc2pnZnVpYXNnZHM="
        // when
        val v = Decoder.with(Decoder.Algo.BASE64).decode(data)
        val v2 = String(java.util.Base64.getDecoder().decode(data))
        // then
        logger.debug { "Base64 decoded v1: $v" }
        logger.debug { "Base64 decoded v2: $v2" }
        //assertEquals(v, "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds")
        assertEquals(String(v), v2)
    }

    @Test
    fun `Decode To Base64 (Safe URL)`() {
        // given
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"   // -> "Special chars: ûÿ here."
        // when
        val v = Decoder.with(Decoder.Algo.BASE64).decode(data)
        val v2 = String(java.util.Base64.getDecoder().decode(data))
        // then
        logger.debug { "Base64 decoded v1: $v" }
        logger.debug { "Base64 decoded v2: $v2" }
        //assertEquals(v, "Special chars: ûûÿ here.")
        assertEquals(String(v), v2)
    }
}