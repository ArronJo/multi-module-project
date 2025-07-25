package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import java.nio.file.Paths

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class FSFileReadTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "$projectRoot/build/xxx".toFile()
        dir = "$parent/ttr".toFile()
    }

    @Test
    fun `FSFile read 1 - TOCTOU 대응 1`() {
        val file = File(dir, "read_test.txt")
        var data = ""
        (1..10 * 1024).forEach { i ->
            data += "svg width=\"70px\" height=\"70px\" viewBox=\"0 0 70 70\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\""
        }

        try {
            FSFile.create(file, overwrite = true)
            FSFile.write(file, data.toByteArray())

            var ba: ByteArray = byteArrayOf()
            assertDoesNotThrow {
                ba = FSFile.read(file)
            }
            logger.debug { "\n\nfile data : \n${String(ba)}\n-----E.O.D----\n\n" }
        } finally {
            FSFile.delete(file, ignore = true)
        }
    }

    @Test
    fun `FSFile read 1 - TOCTOU 대응 2`() {
        val file = File(dir, "read_test.txt")
        var data = ""
        (1..10 * 1024).forEach { i ->
            data += "svg width=\"70px\" height=\"70px\" viewBox=\"0 0 70 70\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\""
        }

        try {
            FSFile.create(file, overwrite = true)
            FSFile.write(file, data.toByteArray())

            var ba: ByteArray = byteArrayOf()
            assertDoesNotThrow {
                ba = FSFile.readWithNIO(file)
            }
            logger.debug { "\n\nfile data : \n${String(ba)}\n-----E.O.D----\n\n" }
        } finally {
            FSFile.delete(file, ignore = true)
        }
    }

    @Test
    fun `FSFile read 2`() {
        val file = File(dir, "read_test.txt")
        var data = ""
        (1..10 * 1024).forEach { i ->
            data += "svg width=\"70px\" height=\"70px\" viewBox=\"0 0 70 70\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\""
        }

        try {
            FSFile.create(file, overwrite = true)
            FSFile.write(file, data.toByteArray())

            var ba: ByteArray = byteArrayOf()
            assertDoesNotThrow {
                ba = FSFile.read(file, charset = "UTF-8")
            }
            logger.debug { "\n\nfile data : \n${String(ba)}\n-----E.O.D----\n\n" }
        } finally {
            FSFile.delete(file, ignore = true)
        }
    }
}
