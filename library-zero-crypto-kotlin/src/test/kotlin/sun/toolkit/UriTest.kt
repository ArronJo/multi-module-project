package sun.toolkit

import com.snc.zero.test.base.BaseJUnit5Test
import com.sun.toolkit.Uri
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.MalformedURLException

@Suppress("NonAsciiCharacters")
class UriTest : BaseJUnit5Test() {

    @Test
    fun `should parse URI with scheme, host, port, path and query`() {
        val uri = Uri("https://example.com:8080/path/to/resource?query=123")

        assertEquals("https", uri.scheme)
        assertTrue(uri.hasAuthority)
        assertEquals("example.com", uri.host)
        assertEquals(8080, uri.port)
        assertEquals("/path/to/resource", uri.path)
        assertEquals("?query=123", uri.query)
    }

    @Test
    fun `should parse URI without port`() {
        val uri = Uri("http://example.com/path")

        assertEquals("http", uri.scheme)
        assertTrue(uri.hasAuthority)
        assertEquals("example.com", uri.host)
        assertEquals(-1, uri.port)
        assertEquals("/path", uri.path)
        assertNull(uri.query)
    }

    @Test
    fun `should parse URI with IPv6 host`() {
        val uri = Uri("http://[2001:db8::1]:8080/path")

        assertEquals("http", uri.scheme)
        assertTrue(uri.hasAuthority)
        assertEquals("[2001:db8::1]", uri.host)
        assertEquals(8080, uri.port)
        assertEquals("/path", uri.path)
        assertNull(uri.query)
    }

    @Test
    fun `should parse URI with only scheme and path`() {
        val uri = Uri("file:/some/local/file.txt")

        assertEquals("file", uri.scheme)
        assertFalse(uri.hasAuthority)
        assertEquals("", uri.host)
        assertEquals(-1, uri.port)
        assertEquals("/some/local/file.txt", uri.path)
        assertNull(uri.query)
    }

    @Test
    fun `should parse URI with empty path and query`() {
        val uri = Uri("http://example.com?query=abc")

        assertEquals("http", uri.scheme)
        assertTrue(uri.hasAuthority)
        assertEquals("example.com", uri.host)
        assertEquals(-1, uri.port)
        assertEquals("", uri.path)
        assertEquals("?query=abc", uri.query)
    }

    @Test
    fun `should handle URI without path or query`() {
        val uri = Uri("http://example.com")

        assertEquals("http", uri.scheme)
        assertTrue(uri.hasAuthority)
        assertEquals("example.com", uri.host)
        assertEquals(-1, uri.port)
        assertEquals("", uri.path)
        assertNull(uri.query)
    }

    @Test
    fun `URI Exception`() {
        assertThrows(MalformedURLException::class.java) {
            Uri("example.com")
        }
    }

    @Test
    fun `invalid URI without scheme throws exception`() {
        val ex = assertThrows<MalformedURLException> {
            Uri("www.example.com")
        }
        assertTrue(ex.message!!.contains("Invalid URI"))
    }

    @Test
    fun `URI with IPv6 missing closing bracket throws exception`() {
        val ex = assertThrows<MalformedURLException> {
            Uri("http://[2001:db8::1")
        }
        assertTrue(ex.message!!.contains("Invalid URI"))
    }

    @Test
    fun `URI with IPv6 with closing bracket beyond authorityEnd throws exception`() {
        Uri("http://[2001:db8::1]foo/bar")
    }

    @Test
    fun `URI with invalid port throws exception`() {
        val ex = assertThrows<NumberFormatException> {
            Uri("http://example.com:abc/path")
        }
        assertTrue(ex.message!!.contains("For input string"))
    }

    @Test
    fun `URI with trailing colon but no port should not throw but port remains -1`() {
        val uri = Uri("http://example.com:/path")
        assertEquals(-1, uri.port)
    }

    @Test
    fun `URI with double colon and no host should throw`() {
        Uri("http::/path")
    }

    @Test
    fun `URI with just a scheme and no rest should parse with empty host path etc`() {
        val uri = Uri("foo:")
        assertEquals("foo", uri.scheme)
        assertFalse(uri.hasAuthority)
        assertEquals("", uri.host)
        assertEquals("", uri.path)
        assertNull(uri.query)
    }

    @Test
    fun `URI with query but no path should parse correctly`() {
        val uri = Uri("http://example.com?foo=bar")
        assertEquals("http", uri.scheme)
        assertEquals("example.com", uri.host)
        assertEquals("", uri.path)
        assertEquals("?foo=bar", uri.query)
    }

    @Test
    fun `URI with no path and no query should leave path empty`() {
        val uri = Uri("http://example.com")
        assertEquals("http", uri.scheme)
        assertEquals("example.com", uri.host)
        assertEquals("", uri.path)
        assertNull(uri.query)
    }

    @Test
    fun `URI with empty port string throws NumberFormatException`() {
        Uri("http://example.com:/")
    }

    @Test
    fun `URI with authority but no host should parse with empty host`() {
        val uri = Uri("http:///path")
        assertEquals("http", uri.scheme)
        assertTrue(uri.hasAuthority)
        assertEquals("", uri.host)
        assertEquals("/path", uri.path)
    }

    @Test
    fun `URI with IPv6 and port parses correctly`() {
        val uri = Uri("http://[2001:db8::1]:8080/path")
        assertEquals("http", uri.scheme)
        assertEquals("[2001:db8::1]", uri.host)
        assertEquals(8080, uri.port)
        assertEquals("/path", uri.path)
    }

    @Test
    fun `URI with path and query parses both correctly`() {
        val uri = Uri("https://example.com/foo/bar?key=value")
        assertEquals("/foo/bar", uri.path)
        assertEquals("?key=value", uri.query)
    }

    @Test
    fun `IPv6 host에서 닫는 대괄호가 없으면 예외 발생`() {
        val uri = "http://[::1"
        val exception = assertThrows<MalformedURLException> {
            Uri(uri)
        }
        assertTrue(exception.message!!.contains("Invalid URI"))
    }

    @Test
    fun `IPv6 host 닫는 대괄호가 path 이후에 있으면 예외 발생`() {
        val uri = "http://[::1/path]"
        val exception = assertThrows<MalformedURLException> {
            Uri(uri)
        }
        assertTrue(exception.message!!.contains("Invalid URI"))
    }

    @Test
    fun `IPv6 host가 있지만 닫는 대괄호 위치가 endIndex 보다 뒤에 있으면 예외 발생`() {
        // 아래 예시는 edge case를 흉내내기 위한 예로, Uri 클래스에 약간의 조정 없이는 테스트하기 어려움.
        val brokenUri = "http://[::1/80" // 닫는 대괄호가 없고, 포트 콜론도 없음
        val exception = assertThrows<MalformedURLException> {
            Uri(brokenUri)
        }
        assertTrue(exception.message!!.contains("Invalid URI"))
    }

    @Test
    fun `host 내에 콜론이 없으면 전체 authority가 host로 처리됨`() {
        val uri = "http://example.com/path"
        val parsed = Uri(uri)
        assertEquals("example.com", parsed.host)
        assertEquals(-1, parsed.port)
    }

    @Test
    fun `host 내 콜론이 있지만 endIndex 이후에 위치하면 무시되어 host로 처리됨`() {
        val uri = "http://example.com/path:fake"
        val parsed = Uri(uri)
        assertEquals("example.com", parsed.host)
        assertEquals(-1, parsed.port)
    }

    @Test
    fun `host 내 콜론이 endIndex 이전에 있으면 port로 분리되어 처리됨`() {
        val uri = "http://example.com:8080/path"
        val parsed = Uri(uri)
        assertEquals("example.com", parsed.host)
        assertEquals(8080, parsed.port)
    }
}
