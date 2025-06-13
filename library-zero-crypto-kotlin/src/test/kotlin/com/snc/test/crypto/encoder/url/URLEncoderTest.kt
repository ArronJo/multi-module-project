package com.snc.test.crypto.encoder.url

import com.snc.zero.crypto.encoder.url.URLEncoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URI

private val logger = TLogging.logger { }

class URLEncoderTest : BaseJUnit5Test() {

    @Test
    fun `Encode To URI`() {
        // given
        val data = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안"
        // when
        val v1 = URLEncoder.encodeURIComponent(data)
        val v2 = URLEncoder.encodeURI(data)
        val v3 = URLEncoder.encodeURIPath(URI(data))
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

    @Test
    fun `encodeURIComponent should encode reserved characters`() {
        val input = "param=value&another=value"
        val expected = "param%3Dvalue%26another%3Dvalue"
        val actual = URLEncoder.encodeURIComponent(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `encodeURIComponent should encode unicode characters`() {
        val input = "안녕하세요"
        val expected = "%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94"
        val actual = URLEncoder.encodeURIComponent(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `encodeURI should not encode reserved URI characters`() {
        val input = "https://example.com/page?id=123&name=test"
        val expected = "https://example.com/page?id=123&name=test"
        val actual = URLEncoder.encodeURI(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `encodeURI should encode unsafe characters`() {
        val input = "https://example.com/안녕"
        val expected = "https://example.com/%EC%95%88%EB%85%95"
        val actual = URLEncoder.encodeURI(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `encodeURIComponent with special characters`() {
        val input = "!@#\$%^&*()_+-={}[]|\\:;'<>?,./"
        val expected = URLEncoder.encodeURIComponent(input)
        val actual = java.net.URLEncoder.encode(input, "utf-8")
        assertEquals(expected, actual)
    }

    @Test
    fun `encodeURI should pass through ASCII and safe characters`() {
        val input = "abc123-_.!~*'()"
        val actual = URLEncoder.encodeURI(input)
        assertEquals(input, actual)
    }

    @Test
    fun `encodeURI with mix of safe and unsafe characters`() {
        val input = "https://example.com/경로?파라미터=값"
        val actual = URLEncoder.encodeURI(input)
        assert(actual.contains("%EC%9D%84") || actual.contains("%EA%B2%BD"))
    }

    @Test
    fun `encodeURIPath should handle URI without query`() {
        val uri = URI("https://example.com/경로")
        val encoded = URLEncoder.encodeURIPath(uri)
        assert(encoded.contains("%EC%9D%84") || encoded.contains("%EA%B2%BD"))
    }

    @Test
    fun `encodeURIPath should handle URI with port`() {
        val uri = URI("https://example.com:8080/경로")
        val encoded = URLEncoder.encodeURIPath(uri)
        assert(encoded.contains(":8080"))
    }

    @Test
    fun `encodeURIComponent should encode Korean and special characters`() {
        val input = "안녕 hello+world@#"
        val encoded = URLEncoder.encodeURIComponent(input)
        println(encoded)
        assert(encoded.contains("%EC%95%88%EB%85%95"))
        assert(encoded.contains("%20hello%252Bworld%40%23") || encoded.contains("+hello%2Bworld%40%23")) // 둘 중 하나 가능
    }

    @Test
    fun `encodeURI should preserve URI structure characters`() {
        val input = "https://example.com/path/to/page?query=안녕&key=value"
        val encoded = URLEncoder.encodeURI(input)
        println(encoded)
        assertTrue(encoded.contains("https://example.com/path/to/page"))
        assertTrue(encoded.contains("query=%EC%95%88%EB%85%95"))
    }

    @Test
    fun `encodeURIPath should encode path and query`() {
        val uri = URI("https://example.com/pages/viewpage.action?pageId=12345&title=안녕")
        val result = URLEncoder.encodeURIPath(uri)
        println(result)
        assertTrue(result.contains("%2Fpages%2Fviewpage.action"))
        assertTrue(result.contains("title%3D%EC%95%88%EB%85%95"))
    }
}
