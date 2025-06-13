package com.snc.test.crypto.extensions.decoder

import com.snc.zero.crypto.extensions.decoder.decodeBase62
import com.snc.zero.crypto.extensions.decoder.decodeBase64
import com.snc.zero.crypto.extensions.decoder.decodeURI
import com.snc.zero.crypto.extensions.decoder.decodeURIComponent
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class DecoderExtTest : BaseJUnit5Test() {

    @Test
    fun `Decode To Base62`() {
        // given
        val data = "hbWbdDh6qZs9HKMROfLt7E3ZruZ08wTiKbvkwhrDXN96CoC3qPFfMep6fezRcZf"
        // when
        val v = String(data.decodeBase62())
        // then
        logger.debug { "decoded: $v" }
        assertEquals("asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds", v)
    }

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

    @Test
    fun `Decode To URI`() {
        // given
        val data = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232%EC%95%88"
        // when
        val v1 = data.decodeURIComponent()
        val v2 = data.decodeURI()
        // then
        //logger.debug { "decoded: $v1" }
        assertEquals("https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안", v1)
        assertEquals("https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안", v2)
    }
}
