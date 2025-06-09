package com.snc.test.extension.io

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean
import com.snc.zero.extension.io.closeQuietly

class IOExtTest : BaseJUnit5Test() {

    @Test
    fun `InputStream should close without exception`() {
        val closed = AtomicBoolean(false)

        val input = object : InputStream() {
            override fun read(): Int = -1
            override fun close() {
                closed.set(true)
                super.close()
            }
        }

        input.closeQuietly()

        assertTrue(closed.get())
    }

    @Test
    fun `OutputStream should flush and close without exception`() {
        val flushed = AtomicBoolean(false)
        val closed = AtomicBoolean(false)

        val output = object : OutputStream() {
            override fun write(b: Int) {}
            override fun flush() {
                flushed.set(true)
                super.flush()
            }

            override fun close() {
                closed.set(true)
                super.close()
            }
        }

        output.closeQuietly()

        assertTrue(flushed.get())
        assertTrue(closed.get())
    }

    @Test
    fun `Closeable should close without throwing`() {
        val closed = AtomicBoolean(false)

        val closeable = object : Closeable {
            override fun close() {
                closed.set(true)
            }
        }

        closeable.closeQuietly()

        assertTrue(closed.get())
    }

    @Test
    fun `InputStream should swallow exception`() {
        val input = object : InputStream() {
            override fun read(): Int = -1
            override fun close() {
                throw IOException("Simulated close error")
            }
        }

        // no exception should be thrown
        input.closeQuietly()
    }

    @Test
    fun `OutputStream should swallow flush and close exceptions`() {
        val output = object : OutputStream() {
            override fun write(b: Int) {}

            override fun flush() {
                throw IOException("Simulated flush error")
            }

            override fun close() {
                throw IOException("Simulated close error")
            }
        }

        output.closeQuietly() // should not throw
    }

    @Test
    fun `Closeable should swallow exception`() {
        val closeable = object : Closeable {
            override fun close() {
                throw IOException("Simulated close error")
            }
        }

        closeable.closeQuietly() // should not throw
    }
}
