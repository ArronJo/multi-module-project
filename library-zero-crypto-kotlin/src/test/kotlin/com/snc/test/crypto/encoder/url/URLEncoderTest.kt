package com.snc.test.crypto.encoder.url

import com.snc.zero.crypto.encoder.url.URLEncoder
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI

private val logger = TLogging.logger { }

@DisplayName("URLEncoder 클래스 테스트")
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
        assertTrue(result.contains("%2Fpages%2Fviewpage.action"))
        assertTrue(result.contains("title%3D%EC%95%88%EB%85%95"))
    }

    @Test
    fun `encodeURIPath should encode path and query 2`() {
        val uri = URI("https://example.com/pages/viewpage.action")
        val result = URLEncoder.encodeURIPath(uri)
        assertTrue(result.contains("%2Fpages%2Fviewpage.action"))
    }

    @Nested
    @DisplayName("encodeURI 메소드 테스트")
    inner class EncodeURITest {

        @Test
        @DisplayName("영문자, 숫자, 특수문자가 포함된 URL을 올바르게 인코딩해야 한다")
        fun shouldEncodeURIWithAlphanumericAndSpecialCharacters() {
            // Given
            val input = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232안"

            // When
            val result = URLEncoder.encodeURI(input)

            // Then
            assertTrue(result.contains("https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232"))
            assertTrue(result.contains("%EC%95%88")) // '안'이 인코딩됨
            assertFalse(result.contains("안")) // 원본 한글은 없어야 함
        }

        @Test
        @DisplayName("인코딩하지 않는 문자들은 그대로 유지되어야 한다")
        fun shouldKeepUnreservedCharacters() {
            // Given
            val input = "https://example.com:8080/path?param=value&other=123#anchor"

            // When
            val result = URLEncoder.encodeURI(input)

            // Then
            assertEquals(input, result) // 모든 문자가 예약문자이므로 변경없음
        }
    }

    @Nested
    @DisplayName("encodeURIComponent 메소드 테스트")
    inner class EncodeURIComponentTest {

        @Test
        @DisplayName("URI 구성요소를 완전히 인코딩해야 한다")
        fun shouldFullyEncodeURIComponent() {
            // Given
            val input = "https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232"

            // When
            val result = URLEncoder.encodeURIComponent(input)

            // Then
            assertEquals("https%3A%2F%2Fconfluence.hanwhalife.com%2Fpages%2Fviewpage.action%3FpageId%3D68972232", result)
        }

        @Test
        @DisplayName("한글 문자를 올바르게 인코딩해야 한다")
        fun shouldEncodeKoreanCharacters() {
            // Given
            val input = "안녕하세요"

            // When
            val result = URLEncoder.encodeURIComponent(input)

            // Then
            assertEquals("%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94", result)
        }
    }

    @Nested
    @DisplayName("encodeURIPath 메소드 테스트 - 조건문 커버리지")
    inner class EncodeURIPathTest {

        @Test
        @DisplayName("query가 null인 경우 query 부분이 추가되지 않아야 한다")
        fun shouldNotAddQueryWhenQueryIsNull() {
            // Given
            val uri = URI("https://example.com:8080/path")

            // When
            val result = URLEncoder.encodeURIPath(uri)

            // Then
            assertEquals("https://example.com:8080%2Fpath", result)
            assertFalse(result.contains("?"))
            assertFalse(result.contains("%3F"))
        }

        @Test
        @DisplayName("query가 empty string인 경우 query 부분이 추가되지 않아야 한다")
        fun shouldNotAddQueryWhenQueryIsEmpty() {
            // Given
            val uri = URI("https://example.com:8080/path?")

            // When
            val result = URLEncoder.encodeURIPath(uri)

            // Then
            assertEquals("https://example.com:8080%2Fpath", result)
            assertFalse(result.contains("%3F"))
        }

        @Test
        @DisplayName("query가 non-empty string인 경우 인코딩된 query가 추가되어야 한다")
        fun shouldAddEncodedQueryWhenQueryIsNonEmpty() {
            // Given
            val uri = URI("https://example.com:8080/path?param=value&other=123")

            // When
            val result = URLEncoder.encodeURIPath(uri)

            // Then
            assertEquals("https://example.com:8080%2Fpath%3Fparam%3Dvalue%26other%3D123", result)
            assertTrue(result.contains("%3F")) // '?'가 인코딩됨
            assertTrue(result.contains("%3D")) // '='가 인코딩됨
            assertTrue(result.contains("%26")) // '&'가 인코딩됨
        }

        @Test
        @DisplayName("query에 특수문자와 한글이 포함된 경우 올바르게 인코딩되어야 한다")
        fun shouldEncodeSpecialCharactersAndKoreanInQuery() {
            // Given
            val uri = URI.create("https://example.com/path?name=홍길동&age=30")

            // When
            val result = URLEncoder.encodeURIPath(uri)

            // Then
            assertTrue(result.startsWith("https://example.com%2Fpath%3F"))
            assertTrue(result.contains("%ED%99%8D%EA%B8%B8%EB%8F%99")) // '홍길동' 인코딩
            assertTrue(result.contains("name%3D")) // 'name=' 인코딩
            assertTrue(result.contains("%26age%3D30")) // '&age=30' 인코딩
        }

        @Test
        @DisplayName("포트 번호가 없는 경우 포트 번호가 추가되지 않아야 한다")
        fun shouldNotAddPortWhenPortIsNotSpecified() {
            // Given
            val uri = URI("https://example.com/path?param=value")

            // When
            val result = URLEncoder.encodeURIPath(uri)

            // Then
            assertEquals("https://example.com%2Fpath%3Fparam%3Dvalue", result)
            // scheme의 ':' 이후에 추가적인 포트 번호가 없는지 확인
            assertFalse(result.matches(Regex("https://[^/]+:\\d+.*")))
        }

        @Test
        @DisplayName("포트 번호가 있는 경우 포트 번호가 포함되어야 한다")
        fun shouldIncludePortWhenPortIsSpecified() {
            // Given
            val uri = URI("https://example.com:9090/path?param=value")

            // When
            val result = URLEncoder.encodeURIPath(uri)

            // Then
            assertEquals("https://example.com:9090%2Fpath%3Fparam%3Dvalue", result)
            // 명시적으로 포트 번호가 포함되어 있는지 확인
            assertTrue(result.matches(Regex("https://[^/]+:\\d+.*")))
            assertTrue(result.contains("example.com:9090"))
        }

        @Test
        @DisplayName("복잡한 URI 구조에서 모든 구성요소가 올바르게 처리되어야 한다")
        fun shouldHandleComplexURIStructure() {
            // Given
            val uri = URI.create("https://subdomain.example.com:8443/api/v1/users?filter=active&sort=name&한글=테스트")

            // When
            val result = URLEncoder.encodeURIPath(uri)

            // Then
            assertTrue(result.startsWith("https://subdomain.example.com:8443"))
            assertTrue(result.contains("%2Fapi%2Fv1%2Fusers")) // path 인코딩
            assertTrue(result.contains("%3Ffilter%3Dactive")) // query 시작 및 첫 파라미터
            assertTrue(result.contains("%26sort%3Dname")) // 파라미터 구분자 및 두 번째 파라미터
            assertTrue(result.contains("%ED%95%9C%EA%B8%80")) // 한글 키 인코딩
            assertTrue(result.contains("%ED%85%8C%EC%8A%A4%ED%8A%B8")) // 한글 값 인코딩
        }
    }
}
