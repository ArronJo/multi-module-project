package com.snc.test.core.io

import com.snc.zero.core.io.IOUtil
import com.snc.zero.core.io.IOUtil.Companion.closeQuietly
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean

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

    @Test
    fun `should close InputStream without exception`() {
        val closed = AtomicBoolean(false)

        val input = object : InputStream() {
            override fun read(): Int = -1
            override fun close() {
                closed.set(true)
            }
        }

        IOUtil.closeQuietly(input)
        assertTrue(closed.get())
    }

    @Test
    fun `should handle exception during InputStream close`() {
        val input = object : InputStream() {
            override fun read(): Int = -1
            override fun close() {
                throw IOException("Simulated close error")
            }
        }

        assertDoesNotThrow { IOUtil.closeQuietly(input) }
    }

    @Test
    fun `should flush and close OutputStream without exception`() {
        val flushed = AtomicBoolean(false)
        val closed = AtomicBoolean(false)

        val output = object : OutputStream() {
            override fun write(b: Int) {}
            override fun flush() {
                flushed.set(true)
            }
            override fun close() {
                closed.set(true)
            }
        }

        IOUtil.closeQuietly(output)
        assertTrue(flushed.get())
        assertTrue(closed.get())
    }

    @Test
    fun `should handle exception during OutputStream flush and close`() {
        val output = object : OutputStream() {
            override fun write(b: Int) {}
            override fun flush() {
                throw IOException("Flush failed")
            }

            override fun close() {
                throw IOException("Close failed")
            }
        }

        assertDoesNotThrow { IOUtil.closeQuietly(output) }
    }

    @Test
    fun `should close Closeable without exception`() {
        val closed = AtomicBoolean(false)

        val closeable = object : Closeable {
            override fun close() {
                closed.set(true)
            }
        }

        closeQuietly(closeable)
        assertTrue(closed.get())
    }

    @Test
    fun `should handle exception during Closeable close`() {
        val closeable = object : Closeable {
            override fun close() {
                throw IOException("Closeable error")
            }
        }

        assertDoesNotThrow { closeQuietly(closeable) }
    }

    @Test
    fun `should call System gc without throwing exception`() {
        assertDoesNotThrow { IOUtil.gc() }
    }
}
