package com.snc.test.filemanager.file2

import com.snc.zero.filemanager.file2.FSInfo
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class FSInfoTest : BaseJUnit5Test() {

    @Test
    fun `Readable FileSize`() {
        logger.debug { "size 1 = ${FSInfo.getReadableFileSize(0)}" }
        logger.debug { "size 2 = ${FSInfo.getReadableFileSize(1)}" }
        logger.debug { "size 3 = ${FSInfo.getReadableFileSize(2)}" }
        logger.debug { "size 4 = ${FSInfo.getReadableFileSize(1000)}" }
        logger.debug { "size 5 = ${FSInfo.getReadableFileSize(1030)}" }
        logger.debug { "size 6 = ${FSInfo.getReadableFileSize(2048)}" }
        logger.debug { "size 7 = ${FSInfo.getReadableFileSize(1023023024L)}" }
        logger.debug { "size 8 = ${FSInfo.getReadableFileSize(922337203685L)}" }
        logger.debug { "size 9 = ${FSInfo.getReadableFileSize(922337203685476L)}" }
        logger.debug { "size 10 = ${FSInfo.getReadableFileSize(92233720368547758L)}" }
        logger.debug { "size 11 = ${FSInfo.getReadableFileSize(Long.MAX_VALUE)}" }

        logger.debug { "size float = ${FSInfo.getReadableFileSize(1234, displayFloat = true)}" }
        logger.debug { "size float = ${FSInfo.getReadableFileSize(1234, displayFloat = false)}" }
        logger.debug { "size float = ${FSInfo.getReadableFileSize(1234, true)}" }
        logger.debug { "size float = ${FSInfo.getReadableFileSize(1234, false)}" }
    }
}
