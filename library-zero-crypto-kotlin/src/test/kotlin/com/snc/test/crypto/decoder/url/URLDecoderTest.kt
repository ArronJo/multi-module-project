package com.snc.test.crypto.decoder.url

import com.snc.zero.crypto.encoder.url.URLDecoder
import com.snc.zero.crypto.encoder.url.URLEncoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
        val v3 = URLDecoder.decodeURIPath(java.net.URI(data1))
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
        val v3 = URLDecoder.decodeURIPath(java.net.URI(data1))
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

    @Test
    fun `decodeURI should decode encoded URL correctly`() {
        val input = "https%3A%2F%2Fexample.com%2Fpath%3Fquery%3Dvalue"
        val expected = "https://example.com/path?query=value"
        assertEquals(expected, URLDecoder.decodeURI(input))
    }

    @Test
    fun `decodeURI should return original string when no encoding present`() {
        val input = "https://example.com/path"
        assertEquals(input, URLDecoder.decodeURI(input))
    }

    @Test
    fun `decodeURIComponent should decode encoded components correctly`() {
        val input = "Hello%20World%21%20%26%20Kotlin"
        val expected = "Hello World! & Kotlin"
        assertEquals(expected, URLDecoder.decodeURIComponent(input))
    }

    @Test
    fun `decodeURIComponent should handle special characters`() {
        val original = "~`!@#\$%^&*()_+-={}|[]\\:\";'<>?,./"
        val encoded = URLEncoder.encodeURIComponent(original, "UTF-8")
        assertEquals(original, URLDecoder.decodeURIComponent(encoded))
    }

    @Test
    fun `decodeURIPath should decode full URI with port and query`() {
        val uri = java.net.URI("https://example.com:8080/path%20with%20space?param%3Dvalue")
        val result = URLDecoder.decodeURIPath(uri)
        val expected = "https://example.com:8080/path with spaceparam=value"
        assertEquals(expected, result)
    }

    @Test
    fun `decodeURIPath should decode URI without port`() {
        val uri = java.net.URI("https://example.com/path%2Fsegment")
        val result = URLDecoder.decodeURIPath(uri)
        val expected = "https://example.com/path/segment"
        assertEquals(expected, result)
    }

    @Test
    fun `decodeURIPath should handle URI with no query`() {
        val uri = java.net.URI("https://example.com/path%2Dtest")
        val result = URLDecoder.decodeURIPath(uri)
        val expected = "https://example.com/path-test"
        assertEquals(expected, result)
    }

    @Test
    fun `decodeURIPath should handle empty path and query`() {
        val uri = java.net.URI("https://example.com")
        val result = URLDecoder.decodeURIPath(uri)
        val expected = "https://example.com" // Because `uri.path` is null → decode(null) == "null"
        assertEquals(expected, result)
    }

    @Test
    fun `decodeURI should handle empty string`() {
        assertEquals("", URLDecoder.decodeURI(""))
    }

    @Test
    fun `decodeURIComponent should throw exception on malformed input`() {
        val malformed = "%E0%A4%A" // incomplete UTF-8 sequence
        assertThrows(IllegalArgumentException::class.java) {
            URLDecoder.decodeURIComponent(malformed)
        }
    }

    @Test
    fun `decodeURIComponent should respect given charset`() {
        val original = "こんにちは" // Japanese
        val encoded = URLEncoder.encodeURIComponent(original, "UTF-8")
        assertEquals(original, URLDecoder.decodeURIComponent(encoded, "UTF-8"))
    }
}
