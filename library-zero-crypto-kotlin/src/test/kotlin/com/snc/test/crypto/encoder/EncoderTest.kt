package com.snc.test.crypto.encoder

import com.snc.zero.crypto.encoder.base64.Base64
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class EncoderTest : BaseTest() {

    @Test
    fun `Encode To Base64`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Base64.encode(data.toByteArray())
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
        val v = Base64.encode(data.toByteArray())
        val v2 = String(java.util.Base64.getEncoder().encode(data.toByteArray()))
        // then
        logger.debug { "Base64 encoded 1: $v" }
        logger.debug { "Base64 encoded 2: $v2" }
        assertNotEquals(v, v2)
    }
}