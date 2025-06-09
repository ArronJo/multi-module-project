package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths

private val logger = TLogging.logger { }

class FSFileCopyTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "$projectRoot/build/xxx".toFile()
        dir = "$parent/tcc".toFile()
    }

    @Test
    fun `FSFile copy 1`() {
        val src = File(dir, "copy1-1.txt")
        val dst = File(dir, "copy1-2.txt")
        //val e = assertThrows(
        //    IOException::class.java
        //) {
        FSFile.create(src, overwrite = true)
        FSFile.copy(src, dst, overwrite = true)
        //}
        //logger.debug { "${e.message}" }
        //assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile copy - exception 1`() {
        val src = File("/")
        val dst = File(dir, "exception1.txt")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(src, overwrite = true)
            FSFile.copy(src, dst, overwrite = true)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile copy - exception 2`() {
        val src = File(dir, "exception2.txt")
        val dst = File("/")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(src, overwrite = true)
            FSFile.copy(src, dst)
        }
        logger.debug { "file length = ${FSFile.getLength(dst)}" }
        FSFile.delete(src)
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile copy - exception 3`() {
        val src = File("/usr/bin", "exception3.txt")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(src)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `copy should transfer data correctly`() {
        val data = "Hello, Kotlin TDD!".toByteArray()
        val input = ByteArrayInputStream(data)
        val output = ByteArrayOutputStream()

        val copiedBytes = FSFile.copy(input, output)

        assertEquals(data.size.toLong(), copiedBytes)
        assertArrayEquals(data, output.toByteArray())
    }

    @Test
    fun `copy should handle empty input`() {
        val input = ByteArrayInputStream(ByteArray(0))
        val output = ByteArrayOutputStream()

        val copiedBytes = FSFile.copy(input, output)

        assertEquals(0L, copiedBytes)
        assertArrayEquals(ByteArray(0), output.toByteArray())
    }

    @Test
    fun `copy should handle large input`() {
        val data = ByteArray(100_000) { (it % 256).toByte() } // 100KB test data
        val input = ByteArrayInputStream(data)
        val output = ByteArrayOutputStream()

        val copiedBytes = FSFile.copy(input, output)

        assertEquals(data.size.toLong(), copiedBytes)
        assertArrayEquals(data, output.toByteArray())
    }

    @Test
    fun `copy should propagate IOException`() {
        val input = object : InputStream() {
            override fun read(): Int {
                throw IOException("Simulated error")
            }
        }
        val output = ByteArrayOutputStream()

        val exception = assertThrows<IOException> {
            FSFile.copy(input, output)
        }

        assertEquals("Simulated error", exception.message)
    }
}
