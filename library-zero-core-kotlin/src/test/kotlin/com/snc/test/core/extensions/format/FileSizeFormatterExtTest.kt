package com.snc.test.core.extensions.format

import com.snc.zero.core.extensions.format.formatFileSize
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class FileSizeFormatterExtTest : BaseJUnit5Test() {

    @Test
    fun `파일 단위 표기 테스트 1`() {
        // given
        val data = 250L
        // when
        val v1 = data.formatFileSize()
        // then
        logger.debug { "파일 단위 1: $v1" }
        assertEquals("250 bytes", v1)
    }

    @Test
    fun `파일 단위 표기 테스트 2`() {
        // given
        val data = 2500L
        // when
        val v1 = data.formatFileSize(true)
        // then
        logger.debug { "파일 단위 2: $v1" }
        assertEquals("2.44 KB", v1)
    }

    @Test
    fun `파일 단위 표기 테스트 3`() {
        // given
        val data = 25000L
        // when
        val v1 = data.formatFileSize(false)
        // then
        logger.debug { "파일 단위 3: $v1" }
        assertEquals("24 KB", v1)
    }

    @Test
    fun `파일 단위 표기 테스트 4`() {
        // given
        val data = 250000L
        // when
        val v1 = data.formatFileSize(displayFloat = true)
        // then
        logger.debug { "파일 단위 4: $v1" }
        assertEquals("244.14 KB", v1)
    }

    @Test
    fun `파일 단위 표기 테스트 5`() {
        // given
        val data = 2500000L
        // when
        val v1 = data.formatFileSize(displayFloat = false)
        // then
        logger.debug { "파일 단위 5: $v1" }
        assertEquals("2 MB", v1)
    }

    @Test
    fun `파일 단위 표기 테스트 6`() {
        // given
        val data = 25000000L
        // when
        val v1 = data.formatFileSize()
        // then
        logger.debug { "파일 단위 6: $v1" }
        assertEquals("24 MB", v1)
    }

    @Test
    fun `파일 단위 표기 테스트 all`() {
        logger.debug { "all: ${1L.formatFileSize()}" }
        logger.debug { "all: ${1000L.formatFileSize()}" }
        logger.debug { "all: ${1030L.formatFileSize()}" }
        logger.debug { "all: ${2048L.formatFileSize()}" }
        logger.debug { "all: ${1023023024L.formatFileSize()}" }
        logger.debug { "all: ${922337203685L.formatFileSize()}" }
        logger.debug { "all: ${922337203685476L.formatFileSize()}" }
        logger.debug { "all: ${92233720368547758L.formatFileSize()}" }
        logger.debug { "all: ${Long.MAX_VALUE.formatFileSize()}" }
    }
}