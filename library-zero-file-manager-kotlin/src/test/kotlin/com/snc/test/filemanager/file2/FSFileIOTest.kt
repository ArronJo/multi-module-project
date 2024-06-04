package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths

private val logger = TLogging.logger { }

class FSFileIOTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "${projectRoot}/build/xxx".toFile()
        dir = "$parent/tio".toFile()
    }

    @Test
    fun `FSFile closeQuietly 1`() {
        val fis: FileInputStream? = null
        FSFile.closeQuietly(fis)
    }

    @Test
    fun `FSFile closeQuietly 2`() {
        val fos: FileOutputStream? = null
        FSFile.closeQuietly(fos)
    }

    @Test
    fun `FSFile closeQuietly Exception 1`() {
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.closeQuietly(FileOutputStream(File("")))
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile closeQuietly Exception 2`() {
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.closeQuietly(FileInputStream(File("")))
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }
}