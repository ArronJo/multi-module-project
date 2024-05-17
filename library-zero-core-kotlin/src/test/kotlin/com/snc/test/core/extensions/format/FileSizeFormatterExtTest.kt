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
        assertEquals(v1, "250 bytes")
    }

    @Test
    fun `파일 단위 표기 테스트 2`() {
        // given
        val data = 2500L
        // when
        val v1 = data.formatFileSize()
        // then
        logger.debug { "파일 단위 2: $v1" }
        assertEquals(v1, "2 KB")
    }

    @Test
    fun `파일 단위 표기 테스트 3`() {
        // given
        val data = 25000L
        // when
        val v1 = data.formatFileSize()
        // then
        logger.debug { "파일 단위 3: $v1" }
        assertEquals(v1, "24 KB")
    }

    @Test
    fun `파일 단위 표기 테스트 4`() {
        // given
        val data = 250000L
        // when
        val v1 = data.formatFileSize()
        // then
        logger.debug { "파일 단위 4: $v1" }
        assertEquals(v1, "244 KB")
    }

    @Test
    fun `파일 단위 표기 테스트 5`() {
        // given
        val data = 2500000L
        // when
        val v1 = data.formatFileSize()
        // then
        logger.debug { "파일 단위 5: $v1" }
        assertEquals(v1, "2 MB")
    }

    @Test
    fun `파일 단위 표기 테스트 6`() {
        // given
        val data = 25000000L
        // when
        val v1 = data.formatFileSize()
        // then
        logger.debug { "파일 단위 6: $v1" }
        assertEquals(v1, "24 MB")
    }
}