package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Paths

class FSFileIOTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "$projectRoot/build/xxx".toFile()
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
}
