package com.snc.test.crypto.decoder.base64

import com.snc.zero.crypto.encoder.base64.Base64
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class Base64Test : BaseJUnit5Test() {

    @Test
    fun `Decode To Base64 변환 1`() {
        // given
        val data = "TWF+"
        // when
        val v = String(Base64.decode(data))
        val v2 = String(java.util.Base64.getDecoder().decode(data))
        // then
        logger.debug { "Base64 decoded v1: $v" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(v, v2)
    }

    @Test
    fun `Decode To Base64 변환 2`() {
        // given
        val data = "TWF/"
        // when
        val v = String(Base64.decode(data))
        val v2 = String(java.util.Base64.getDecoder().decode(data))
        // then
        logger.debug { "Base64 decoded v1: $v" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(v, v2)
    }

    @Test
    fun `Decode To Base64 변환 3`() {
        // given
        val data = "YXNkc25jeW5oIDI5ODR5aGQ4OWB5dTg5ODkxODl1OWpxZmRhc2pnZnVpYXNnZHM="
        // when
        val v = String(Base64.decode(data))
        val v2 = String(java.util.Base64.getDecoder().decode(data))
        // then
        logger.debug { "Base64 decoded v1: $v" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(v, v2)
    }

    @Test
    fun `Decode To Base64 (Safe URL) 변환`() {
        // given
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"   // -> "Special chars: ûÿ here."
        // when
        val v = String(Base64.decode(data))
        val v2 = String(java.util.Base64.getDecoder().decode(data))
        // then
        logger.debug { "Base64 decoded v1: $v" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(v, v2)
    }
}