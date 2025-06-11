package com.snc.test.crypto.extensions.encoder

import com.snc.zero.crypto.extensions.encoder.encodeBase62
import com.snc.zero.crypto.extensions.encoder.encodeBase64
import com.snc.zero.crypto.extensions.encoder.encodeURI
import com.snc.zero.crypto.extensions.encoder.encodeURIComponent
import com.snc.zero.crypto.extensions.encoder.toUrlEncoding
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class EncoderExtTest : BaseJUnit5Test() {

    @Test
    fun `Encode To Base62`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = data.encodeBase62()
        // then
        logger.debug { "encoded: $v" }
        assertEquals("hbWbdDh6qZs9HKMROfLt7E3ZruZ08wTiKbvkwhrDXN96CoC3qPFfMep6fezRcZf", v)
    }

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

    @Test
    fun `Encode To URI`() {
        // given
        val data = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안"
        // when
        val v1 = data.encodeURIComponent()
        val v2 = data.encodeURI()
        // then
        //logger.debug { "encodeURIComponent: $v1" }
        assertEquals("https%3A%2F%2Fconfluence.hanwhalife.com%2Fpages%2Fviewpage.action%3FpageId%3D68972232%EC%95%88", v1)
        //logger.debug { "encodeURI: $v2" }
        assertEquals("https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232%EC%95%88", v2)
    }


    @Test
    fun `String 값이 URL 인코딩되어야 한다`() {
        val original = hashMapOf(
            "query" to "kotlin test",
            "lang" to "ko"
        )

        val encoded = original.toUrlEncoding()

        assertEquals("kotlin+test", encoded["query"])
        assertEquals("ko", encoded["lang"])
    }

    @Test
    fun `String 이 아닌 값은 그대로 유지되어야 한다`() {
        val original = hashMapOf<String, Any?>(
            "count" to 42,
            "isActive" to true,
            "name" to "John Doe"
        )

        val encoded = original.toUrlEncoding()

        assertEquals(42, encoded["count"])
        assertEquals(true, encoded["isActive"])
        assertEquals("John+Doe", encoded["name"]) // URL-encoded
    }

    @Test
    fun `빈 맵은 그대로 반환되어야 한다`() {
        val original = hashMapOf<String, Any?>()

        val encoded = original.toUrlEncoding()

        assertEquals(0, encoded.size)
    }

    @Test
    fun `null 값은 무시되거나 예외 발생 없이 처리되어야 한다`() {
        val original = hashMapOf<String, Any?>(
            "key1" to null,
            "key2" to "normal"
        )

        val encoded = original.toUrlEncoding()

        assertEquals(null, encoded["key1"]) // 또는 예외가 발생하지 않으면 성공
        assertEquals("normal", encoded["key2"])
    }
}
