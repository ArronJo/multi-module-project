package com.snc.test.core.io

import com.snc.zero.core.io.IOUtil
import com.snc.zero.core.io.IOUtil.Companion.closeQuietly
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

@Suppress("NonAsciiCharacters")
class IOUtilTest : BaseJUnit5Test() {

    @Test
    fun `closeQuietly InputStream should close stream without exception`() {
        val inputStream = object : ByteArrayInputStream("test".toByteArray()) {
            var closed = false
            override fun close() {
                super.close()
                closed = true
            }
        }

        assertDoesNotThrow {
            IOUtil.closeQuietly(inputStream)
        }
        assertTrue(inputStream.closed)
    }

    @Test
    fun `closeQuietly OutputStream should flush and close without exception`() {
        val outputStream = object : ByteArrayOutputStream() {
            var closed = false
            override fun close() {
                super.close()
                closed = true
            }

            override fun flush() {
                super.flush()
                // Optional: Add logging/flag here if needed
            }
        }

        assertDoesNotThrow {
            IOUtil.closeQuietly(outputStream)
        }
        assertTrue(outputStream.closed)
    }

    @Test
    fun `closeQuietly Closeable should close resource without exception`() {
        var closed = false
        val closeable = Closeable {
            closed = true
        }

        assertDoesNotThrow {
            closeQuietly(closeable)
        }
        assertTrue(closed)
    }

    @Test
    fun `gc should not throw exception`() {
        assertDoesNotThrow {
            IOUtil.gc()
        }
    }

    @Test
    fun `closeQuietly null 테스트`() {
        val `is`: InputStream? = null
        val os: OutputStream? = null
        val c: Closeable? = null

        closeQuietly(`is`)
        closeQuietly(os)
        closeQuietly(c)
    }
}
