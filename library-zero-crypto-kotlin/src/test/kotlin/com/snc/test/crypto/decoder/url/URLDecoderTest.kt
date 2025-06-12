package com.snc.test.crypto.decoder.url

import com.snc.zero.crypto.encoder.url.URLDecoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.sun.toolkit.Uri
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class URLDecoderTest : BaseJUnit5Test() {

    @Test
    fun `Decode To URI`() {
        // given
        val data1 = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232%EC%95%88"
        // when
        val v1 = URLDecoder.decodeURIComponent(data1)
        val v2 = URLDecoder.decodeURI(data1)
        val v3 = URLDecoder.decodeURIPath(Uri(data1))
        // then
        logger.debug { "URI decodeURIComponent: $v1" }
        logger.debug { "URI decodeURI: $v2" }
        logger.debug { "URI decodeURIPath: $v3" }
        assertEquals(
            v1,
            "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안"
        )
        assertEquals(
            v1,
            "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안"
        )
    }

    @Test
    fun `Decode To URI 2`() {
        // given
        val data1 = "https://confluence.hanwhalife.com/pages/viewpage.action"
        // when
        val v1 = URLDecoder.decodeURIComponent(data1)
        val v2 = URLDecoder.decodeURI(data1)
        val v3 = URLDecoder.decodeURIPath(Uri(data1))
        // then
        logger.debug { "URI decodeURIComponent: $v1" }
        logger.debug { "URI decodeURI: $v2" }
        logger.debug { "URI decodeURIPath: $v3" }
        assertEquals(
            v1,
            "https://confluence.hanwhalife.com/pages/viewpage.action"
        )
        assertEquals(
            v1,
            "https://confluence.hanwhalife.com/pages/viewpage.action"
        )
    }
}
