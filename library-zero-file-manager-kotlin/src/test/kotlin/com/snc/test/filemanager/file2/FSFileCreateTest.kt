package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import java.io.IOException
import java.nio.file.Paths

private val logger = TLogging.logger { }

class FSFileCreateTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "${projectRoot}/build/xxx".toFile()
        dir = "$parent/ttc".toFile()
    }

    @Test
    fun `FSFile create or overwrite 1`() {
        val file = File(dir, "create1.txt")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
            FSFile.create(file, overwrite = false)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 2`() {
        val file = File(dir, "create2.txt")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
            FSFile.create(file, overwrite = false)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 3`() {
        val file = File("/", "create3.txt")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 4`() {
        val file = File("/usr/bin", "create4.txt")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 5-1`() {
        val file = File("/usr/bin", "aaa")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = false)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 5-2`() {
        val file = File("/usr/bin", "sleep")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 5-3`() {
        val file = File("/usr/zzz", "bbb")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 5-4`() {
        val file = File("/")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile create or overwrite 5-5`() {
        val file = File("/", "w5-5.txt")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(file, overwrite = true)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }
}