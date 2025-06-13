package com.snc.test.crypto.decoder

import com.snc.zero.crypto.encoder.Decoder
import com.snc.zero.crypto.encoder.Decoder.Algo
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DecoderTest : BaseJUnit5Test() {

    @Test
    fun `Decode To Base62`() {
        // given
        val data = "hbWbdDh6qZs9HKMROfLt7E3ZruZ08wTiKbvkwhrDXN96CoC3qPFfMep6fezRcZf"
        // when
        val v = Decoder.with(Algo.BASE62).decode(data)
        // then
        logger.debug { "Base62 decoded: $v" }
        assertEquals(String(v), "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds")
    }

    @Test
    fun `Decode To Base64`() {
        // given
        val data = "YXNkc25jeW5oIDI5ODR5aGQ4OWB5dTg5ODkxODl1OWpxZmRhc2pnZnVpYXNnZHM="
        // when
        val v1 = Decoder.with(Algo.BASE64).decode(data)
        val v2 = java.util.Base64.getDecoder().decode(data)
        // then
        logger.debug { "Base64 decoded v1: $v1" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(String(v1), String(v2))
    }

    @Test
    fun `Decode To Base64 (Safe URL)`() {
        // given
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu" // -> "Special chars: ûÿ here."
        // when
        val v1 = Decoder.with(Algo.BASE64).decode(data)
        val v2 = java.util.Base64.getDecoder().decode(data)
        // then
        logger.debug { "Base64 decoded v1: $v1" }
        logger.debug { "Base64 decoded v2: $v2" }
        assertEquals(String(v1), String(v2))
    }

    @Test
    fun `Decode To URI`() {
        // given
        val data1 = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232%EC%95%88"
        // when
        val v1 = Decoder.with(Algo.URIComponent).decodeURI(data1)
        val v2 = Decoder.with(Algo.URI).decodeURI(data1)
        // then
        logger.debug { "URI decodeURIComponent: $v1" }
        logger.debug { "URI decodeURI: $v2" }
        assertEquals("https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안", v1)
        assertEquals("https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안", v2)
    }

    @Test
    fun `Decode 테스트 1-1`() {
        // given
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"
        // when
        val v1 = Decoder.with(Algo.BASE64).decode(data)
        val v2 = java.util.Base64.getDecoder().decode(data.toByteArray())
        // then
        assertEquals(String(v1), String(v2))
    }

    @Test
    fun `Decode 테스트 Exception - 1`() {
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"

        assertThrows(Exception::class.java) {
            Decoder.with(Algo.BASE62).decodeURI(data)
        }
        assertThrows(Exception::class.java) {
            Decoder.with(Algo.BASE64).decodeURI(data)
        }
    }

    @Test
    fun `Decode 테스트 Exception - 2`() {
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"

        assertThrows(Exception::class.java) {
            Decoder.with(Algo.URIComponent).decode(data)
        }
        assertThrows(Exception::class.java) {
            Decoder.with(Algo.URI).decode(data)
        }
    }
}
