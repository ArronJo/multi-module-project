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

class FSFileCopyTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "${projectRoot}/build/xxx".toFile()
        dir = "$parent/ccc".toFile()
    }

    @Test
    fun `FSFile copy 1`() {
        val src = File(dir, "copy1.txt")
        val dst = File(dir, "copy1-1.txt")
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
    fun `FSFile copy 2`() {
        val src = File("/")
        val dst = File(dir, "copy2-2.txt")
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
    fun `FSFile copy 3`() {
        val src = File(dir, "copy3.txt")
        val dst = File("/")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.create(src, overwrite = true)
            FSFile.copy(src, dst, overwrite = true)
        }
        FSFile.delete(src)
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }
}