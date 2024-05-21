package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.FSInfo
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File

private val logger = TLogging.logger { }

class FSFileReadTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        parent = "/Users/jwjo/Downloads/zzz".toFile()
        dir = "$parent/ttt".toFile()
    }

    @Test
    fun `FSFile read`() {
        //val file = File(dir, "write_test.js")
        val file = File(dir, "android.svg")
        if (!file.exists()) {
            logger.debug { "$file not exist" }
            return
        }
        val ba = FSFile.read(file)
        logger.debug { "file size : ${FSInfo.getReadableFileSize(ba.size.toLong())}" }
        logger.debug { "\n\nfile data : \n${String(ba)}\n-----E.O.D----\n\n" }
    }
}