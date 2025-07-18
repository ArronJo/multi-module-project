package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSDirectory
import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.filemanager.file2.extensions.toFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import java.nio.file.Paths

private val logger = TLogging.logger { }

class FSDirectoryTest : BaseJUnit5Test() {

    private lateinit var parent: File
    private lateinit var dir: File

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        val projectRoot = Paths.get("").toAbsolutePath()
        println("Project Root Directory: $projectRoot")
        parent = "$projectRoot/build/zzz".toFile()
        dir = "$parent/aaa/bbb/ccc/ddd/eee/fff/ggg".toFile()
    }

    @Test
    fun `FSDirectory create 1`() {
        assertDoesNotThrow {
            FSDirectory.create(dir, overwrite = true)
        }
        if (dir.exists()) {
            logger.debug { "create: $dir exist" }
        } else {
            logger.debug { "create: $dir not exist" }
        }
    }

    @Test
    fun `FSDirectory create 2`() {
        assertDoesNotThrow {
            FSDirectory.create(dir, overwrite = true)
            FSDirectory.create(dir, overwrite = false)
        }
        if (dir.exists()) {
            logger.debug { "create: $dir exist" }
        } else {
            logger.debug { "create: $dir not exist" }
        }
    }

    @Test
    fun `FSDirectory create 3`() {
        assertDoesNotThrow {
            FSDirectory.create(dir)
        }
        if (dir.exists()) {
            logger.debug { "create: $dir exist" }
        } else {
            logger.debug { "create: $dir not exist" }
        }
    }

    @Test
    fun `FSDirectory delete 1`() {
        assertDoesNotThrow {
            FSDirectory.create(dir, overwrite = true)
            FSDirectory.delete(parent)
        }
        if (parent.exists()) {
            logger.debug { "delete: $parent exist" }
        } else {
            logger.debug { "delete: $parent not exist" }
        }
    }

    @Test
    fun `FSDirectory delete Recursively 1`() {
        assertDoesNotThrow {
            FSDirectory.create(dir, overwrite = true)
            FSDirectory.deleteRecursively(parent)
        }
        if (parent.exists()) {
            logger.debug { "delete: $parent exist" }
        } else {
            logger.debug { "delete: $parent not exist" }
        }
    }

    @Test
    fun `FSDirectory delete 2`() {
        val ret = FSDirectory.delete(File(parent, "notexist"))
        assertEquals(false, ret)
    }

    @Test
    fun `FSDirectory delete 3`() {
        val f = File(dir, "ddd3.txt")
        FSFile.create(f, true)
        val ret = FSDirectory.delete(f)
        assertEquals(true, ret)
    }
}
