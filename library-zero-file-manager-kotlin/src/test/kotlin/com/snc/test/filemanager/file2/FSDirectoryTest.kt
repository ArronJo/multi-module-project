package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSDirectory
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File

private val logger = TLogging.logger { }

class FSDirectoryTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        parent = "/Users/jwjo/Downloads/zzz".toFile()
        dir = "$parent/aaa/bbb/ccc/ddd/eee/fff/ggg".toFile()
    }

    @Test
    fun `FSDirectory create`() {
        FSDirectory.create(dir, overwrite = true)
        if (dir.exists()) {
            logger.debug { "create: $dir exist" }
        } else {
            logger.debug { "create: $dir not exist" }
        }
    }

    @Test
    fun `FSDirectory delete`() {
        FSDirectory.delete(parent, verbose = true)
        if (parent.exists()) {
            logger.debug { "delete: $parent exist" }
        } else {
            logger.debug { "delete: $parent not exist" }
        }
    }
}