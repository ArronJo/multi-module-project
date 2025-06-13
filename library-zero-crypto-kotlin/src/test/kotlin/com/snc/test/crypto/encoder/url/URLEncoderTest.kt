package com.snc.test.crypto.encoder.url

import com.snc.zero.crypto.encoder.url.URLEncoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.sun.toolkit.Uri
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class URLEncoderTest : BaseJUnit5Test() {

    @Test
    fun `Encode To URI`() {
        // given
        val data = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안"
        // when
        val v1 = URLEncoder.encodeURIComponent(data)
        val v2 = URLEncoder.encodeURI(data)
        val v3 = URLEncoder.encodeURIPath(Uri(data))
        // then
        logger.debug { "encodeURIComponent: $v1" }
        logger.debug { "encodeURI: $v2" }
        logger.debug { "encodeURIPath: $v3" }
        assertEquals(
            v1,
            "https%3A%2F%2Fconfluence.hanwhalife.com%2Fpages%2Fviewpage.action%3FpageId%3D68972232%EC%95%88"
        )
        assertEquals(
            v2,
            "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232%EC%95%88"
        )
    }
}
