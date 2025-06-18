package com.snc.test.crypto.encoder.base62

import com.snc.zero.crypto.encoder.base62.Base62
import com.snc.zero.crypto.encoder.base62.Base62.CharacterSets
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class Base62Test : BaseJUnit5Test() {

    @Test
    fun `Encode To Base62 - 1`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Base62.encode(data.toByteArray())
        // then
        logger.debug { "Base62 encoded: $v" }
        assertEquals("hbWbdDh6qZs9HKMROfLt7E3ZruZ08wTiKbvkwhrDXN96CoC3qPFfMep6fezRcZf", String(v))
    }

    @Test
    fun `Encode To Base62 - 2`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Base62.encode(data.toByteArray(), CharacterSets.INVERTED)
        // then
        logger.debug { "Base62 encoded: $v" }
        assertEquals("HBwBDdH6QzS9hkmroFlT7e3zRUz08WtIkBVKWHRdxn96cOc3QpfFmEP6FEZrCzF", String(v))
    }

    @Test
    fun `null input should return false`() {
        assertFalse(Base62.isBase62Encoding(null))
    }

    @Test
    fun `empty byte array should return true`() {
        assertTrue(Base62.isBase62Encoding(byteArrayOf()))
    }

    @Test
    fun `only digits 0-9 should return true`() {
        val digits = ('0'..'9').joinToString("").toByteArray()
        assertTrue(Base62.isBase62Encoding(digits))
    }

    @Test
    fun `only lowercase a-z should return true`() {
        val lowercase = ('a'..'z').joinToString("").toByteArray()
        assertTrue(Base62.isBase62Encoding(lowercase))
    }

    @Test
    fun `only uppercase A-Z should return true`() {
        val uppercase = ('A'..'Z').joinToString("").toByteArray()
        assertTrue(Base62.isBase62Encoding(uppercase))
    }

    @Test
    fun `full valid base62 character set should return true`() {
        val fullSet = ('0'..'9') + ('A'..'Z') + ('a'..'z')
        val bytes = fullSet.joinToString("").toByteArray()
        assertTrue(Base62.isBase62Encoding(bytes))
    }

    @Test
    fun `string with invalid characters should return false`() {
        val invalid = "abcABC123!@#".toByteArray()
        assertFalse(Base62.isBase62Encoding(invalid))
    }

    @Test
    fun `string with only invalid characters should return false`() {
        val invalid = "!@#$%^&*()".toByteArray()
        assertFalse(Base62.isBase62Encoding(invalid))
    }

    @Test
    fun `string with whitespace should return false`() {
        val invalid = "abc ABC".toByteArray()
        assertFalse(Base62.isBase62Encoding(invalid))
    }

    @Test
    fun `string with boundary characters should return true`() {
        val boundaries = "09azAZ".toByteArray()
        assertTrue(Base62.isBase62Encoding(boundaries))
    }

    @Test
    fun `non-ascii characters should return false`() {
        val nonAscii = "한글テスト".toByteArray(Charsets.UTF_8)
        assertFalse(Base62.isBase62Encoding(nonAscii))
    }

    @Test
    fun `extended ascii characters should return false`() {
        val extendedAscii = byteArrayOf(127, 128.toByte(), 255.toByte())
        assertFalse(Base62.isBase62Encoding(extendedAscii))
    }

    @Test
    fun `mixed valid and invalid characters should return false`() {
        val mixed = "abcXYZ1~".toByteArray()
        assertFalse(Base62.isBase62Encoding(mixed))
    }
}
