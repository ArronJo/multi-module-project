package com.snc.test.crypto.extensions.decoder

import com.snc.zero.crypto.extensions.decoder.decodeBase64
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class DecoderExtTest : BaseJUnit5Test() {

    @Test
    fun `Decode To Base64`() {
        // given
        val data = "YXNkc25jeW5oIDI5ODR5aGQ4OWB5dTg5ODkxODl1OWpxZmRhc2pnZnVpYXNnZHM="
        // when
        val v = String(data.decodeBase64())
        // then
        logger.debug { "decoded: $v" }
        assertEquals("asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds", v)
    }
}