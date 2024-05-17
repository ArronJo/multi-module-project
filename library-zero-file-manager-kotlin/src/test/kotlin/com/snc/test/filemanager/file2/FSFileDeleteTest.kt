package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File

private val logger = TLogging.logger { }

class FSFileDeleteTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        parent = "/Users/jwjo/Downloads/zzz".toFile()
        dir = "$parent/ttt".toFile()
    }

    @Test
    fun `FSFile delete`() {
        val file = File(dir, "create.png")
        FSFile.delete(file, verbose = true)
        if (file.exists()) {
            logger.debug { "$file exist" }
        } else {
            logger.debug { "$file not exist" }
        }
    }
}