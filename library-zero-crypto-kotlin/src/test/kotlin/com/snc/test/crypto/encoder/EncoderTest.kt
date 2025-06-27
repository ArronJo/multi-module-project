package com.snc.test.crypto.encoder

import com.snc.zero.crypto.encoder.Encoder
import com.snc.zero.crypto.encoder.Encoder.Algo
import com.snc.zero.crypto.encoder.base64.Base64
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class EncoderTest : BaseJUnit5Test() {

    @Test
    fun `Encode To Base62`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Encoder.with(Algo.BASE62).encode(data.toByteArray())
        // then
        logger.debug { "Base62 encoded: $v" }
        assertEquals("hbWbdDh6qZs9HKMROfLt7E3ZruZ08wTiKbvkwhrDXN96CoC3qPFfMep6fezRcZf", v)
    }

    @Test
    fun `Encode To Base64`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Encoder.with(Algo.BASE64).encode(data.toByteArray())
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
        val v = Encoder.with(Algo.BASE64).encode(data.toByteArray())
        val v2 = Base64.encode(data.toByteArray())
        // then
        logger.debug { "Base64 encoded 1: $v" } // "U3BlY2lhbCBjaGFyczogw7vDu8O_IGhlcmUu"
        logger.debug { "Base64 encoded 2: $v2" } // "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"
        assertNotEquals(v, v2)
    }

    @Test
    fun `Encode To URI`() {
        // given
        val data = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안"
        // when
        val v1 = Encoder.with(Algo.URIComponent).encodeURI(data)
        val v2 = Encoder.with(Algo.URI).encodeURI(data)
        // then
        logger.debug { "encodeURIComponent: $v1" }
        logger.debug { "encodeURI: $v2" }
        assertEquals("https%3A%2F%2Fconfluence.hanwhalife.com%2Fpages%2Fviewpage.action%3FpageId%3D68972232%EC%95%88", v1)
        assertEquals("https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232%EC%95%88", v2)
    }

    @Test
    fun `Encode 테스트 1-1`() {
        // given
        val data = "aaaaa"
        // when
        val v1 = Encoder.with(Algo.BASE64).encode(data.toByteArray())
        val v2 = String(java.util.Base64.getEncoder().encode(data.toByteArray()))
        // then
        assertEquals(v1, v2)
    }

    @Test
    fun `Encode 테스트 Exception - 2`() {
        val data = "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"

        assertThrows(Exception::class.java) {
            Encoder.with(Algo.URIComponent).encode(data.toByteArray())
        }
        assertThrows(Exception::class.java) {
            Encoder.with(Algo.URI).encode(data.toByteArray())
        }
        assertThrows(Exception::class.java) {
            Encoder.with(Algo.BASE62).encodeURI(data)
        }
        assertThrows(Exception::class.java) {
            Encoder.with(Algo.BASE64).encodeURI(data)
        }
    }

    @Test
    fun `Encode Enum 100프로 테스트 완료를 위한 entries 테스트`() {
        val e = Algo.entries.toTypedArray()
        assertEquals(Algo.BASE62, e[0])
        assertEquals(Algo.BASE64, e[1])
        assertEquals(Algo.URIComponent, e[2])
        assertEquals(Algo.URI, e[3])
    }
}
