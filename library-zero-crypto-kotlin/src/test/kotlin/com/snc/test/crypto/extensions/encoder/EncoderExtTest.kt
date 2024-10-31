package com.snc.test.crypto.extensions.encoder

import com.snc.zero.crypto.extensions.encoder.encodeBase64
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class EncoderExtTest : BaseJUnit5Test() {

    @Test
    fun `Encode To Base64`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = data.encodeBase64()
        // then
        logger.debug { "encoded: $v" }
        assertEquals("YXNkc25jeW5oIDI5ODR5aGQ4OWB5dTg5ODkxODl1OWpxZmRhc2pnZnVpYXNnZHM=", v)
    }
}
