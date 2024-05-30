package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
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
        parent = "${projectRoot}/build/zzz".toFile()
        dir = "$parent/ttt".toFile()
    }

    @Test
    fun `FSFile create or overwrite 1`() {
        val file = File(dir, "create.txt")
        FSFile.create(file, overwrite = true)
        if (file.exists()) {
            logger.debug { "$file exist" }
        } else {
            logger.debug { "$file not exist" }
        }
    }

    @Test
    fun `FSFile create or overwrite 2`() {
        val file = File(dir, "create.txt")
        FSFile.create(file, overwrite = true)
        FSFile.create(file, overwrite = false)
        if (file.exists()) {
            logger.debug { "$file exist" }
        } else {
            logger.debug { "$file not exist" }
        }
    }

    @Test
    fun `FSFile create or overwrite 3`() {
        val file = File("/", "create.txt")
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            FSFile.create(file, overwrite = true)
        }
        if (file.exists()) {
            logger.debug { "$file exist" }
        } else {
            logger.debug { "$file not exist" }
        }
    }
}