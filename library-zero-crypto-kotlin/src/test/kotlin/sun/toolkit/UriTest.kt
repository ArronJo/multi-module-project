package sun.toolkit

import com.snc.zero.test.base.BaseJUnit5Test
import com.sun.toolkit.Uri
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

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
}
