package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import java.io.IOException
import java.nio.file.Paths

private val logger = TLogging.logger { }

class FSFileDeleteTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "$projectRoot/build/xxx".toFile()
        dir = "$parent/ttd".toFile()
    }

    @Test
    fun `FSFile delete 1`() {
        val file = File(dir, "delete.png")
        val e = assertThrows(
            IOException::class.java
        ) {
            FSFile.delete(file)
        }
        logger.debug { "${e.message}" }
        assertNotEquals(e.message, "")
    }

    @Test
    fun `FSFile delete 2`() {
        val file = File(dir, "delete.png")
        assertDoesNotThrow {
            FSFile.delete(file, ignore = true)
        }
        if (file.exists()) {
            logger.debug { "$file exist" }
        } else {
            logger.debug { "$file not exist" }
        }
    }
}
