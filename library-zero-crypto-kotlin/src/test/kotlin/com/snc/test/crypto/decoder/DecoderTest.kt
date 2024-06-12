package com.snc.test.crypto.decoder

import com.snc.zero.crypto.encoder.Decoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DecoderTest : BaseJUnit5Test() {

    @Test
    fun `Decode To Base64`() {
        // given
        val data = "YXNkc25jeW5oIDI5ODR5aGQ4OWB5dTg5ODkxODl1OWpxZmRhc2pnZnVpYXNnZHM="
        // when
        val v1 = Decoder.with(Decoder.Algo.BASE64).decode(data)
        val v2 = java.util.Base64.getDecoder().decode(data)
        // then
        logger.debug { "Base64 decoded v1: $v1" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(String(v1), String(v2))
    }

    @Test
    fun `Decode To Base64 (Safe URL)`() {
        // given
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"   // -> "Special chars: ûÿ here."
        // when
        val v1 = Decoder.with(Decoder.Algo.BASE64).decode(data)
        val v2 = java.util.Base64.getDecoder().decode(data)
        // then
        logger.debug { "Base64 decoded v1: $v1" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(String(v1), String(v2))
    }

    @Test
    fun `Decode 테스트 1-1`() {
        // given
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"
        // when
        val v1 = Decoder.with().decode(data)
        val v2 = java.util.Base64.getDecoder().decode(data.toByteArray())
        // then
        assertEquals(String(v1), String(v2))
    }
}