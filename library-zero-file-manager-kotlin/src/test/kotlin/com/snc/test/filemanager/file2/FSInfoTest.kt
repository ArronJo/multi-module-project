package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSInfo.Companion.getReadableFileSize
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class FSInfoTest : BaseJUnit5Test() {

    @Test
    fun `Readable FileSize`() {
        logger.debug { "size = ${getReadableFileSize(0)}" }
        logger.debug { "size = ${getReadableFileSize(1)}" }
        logger.debug { "size = ${getReadableFileSize(2)}" }
        logger.debug { "size = ${getReadableFileSize(1000)}" }
        logger.debug { "size = ${getReadableFileSize(1030)}" }
        logger.debug { "size = ${getReadableFileSize(2048)}" }
        logger.debug { "size = ${getReadableFileSize(1023023024L)}" }
    }
}