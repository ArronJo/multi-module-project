package com.snc.test.filemanager.file2

import com.snc.zero.extensions.text.print
import com.snc.zero.filemanager.file2.FSFile
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class FSFileToObjectTest : BaseJUnit5Test() {

    @Test
    fun `toByteArray Test 1`() {
        val ca = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')
        val ba = FSFile.toBytes(ca)
        val ba2 = String(ca).toByteArray(Charsets.UTF_8)

        logger.debug { "char array: ${ca.print()}" }
        logger.debug { "byte array: ${ba.print()}" }
        logger.debug { "byte array2: ${ba2.print()}" }
    }
}
