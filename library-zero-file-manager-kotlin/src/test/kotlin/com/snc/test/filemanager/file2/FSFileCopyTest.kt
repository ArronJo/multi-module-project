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

class FSFileCopyTest : BaseJUnit5Test() {

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
    fun `FSFile copy`() {
        val src = File(dir, "write_test.js")
        val dst = File(dir, "copy_test.js")
        assertDoesNotThrow {
            FSFile.create(src, overwrite = true)
            FSFile.copy(src, dst, overwrite = true)
        }
        if (!dst.exists()) {
            logger.debug { "$dst not exist" }
        }
    }
}