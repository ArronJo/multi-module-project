package com.snc.test.extension.format

import com.snc.zero.extension.format.formatDateTime
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date

private val logger = TLogging.logger { }

class DateFormatterExtTest : BaseJUnit5Test() {

    companion object {
        private const val DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss"
        private const val DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    }

    @Test
    fun `String dateFormat 1`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.formatDateTime(inputFormat = DATE_FORMAT_YYYYMMDDHHMMSS, outputFormat = DATE_FORMAT_YYYY_MM_DD_HH_MM)
        // then
        logger.debug { "String dateFormat 결과: $data -> $v1" }
        assertEquals("2024-04-02 17:59", v1)
    }

    @Test
    fun `String dateFormat 2`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.formatDateTime(inputFormat = DATE_FORMAT_YYYYMMDDHHMMSS)
        // then
        logger.debug { "String dateFormat 결과: $data -> $v1" }
        assertEquals("20240402175932", v1)
    }

    @Test
    fun `Calendar dateFormat 1`() {
        // given
        val data = Calendar.getInstance()
        // when
        val v1 = data.formatDateTime(outputFormat = DATE_FORMAT_YYYY_MM_DD_HH_MM)
        // then
        logger.debug { "Calendar dateFormat 결과: $data -> $v1" }
    }

    @Test
    fun `Calendar dateFormat 2`() {
        // given
        val data = Calendar.getInstance()
        // when
        val v1 = data.formatDateTime()
        // then
        logger.debug { "Calendar dateFormat 결과: $data -> $v1" }
    }

    @Test
    fun `Date dateFormat 1`() {
        // given
        val data = Date()
        // when
        val v1 = data.formatDateTime(outputFormat = DATE_FORMAT_YYYY_MM_DD_HH_MM)
        // then
        logger.debug { "Date dateFormat 결과: $data -> $v1" }
    }

    @Test
    fun `Date dateFormat 2`() {
        // given
        val data = Date()
        // when
        val v1 = data.formatDateTime()
        // then
        logger.debug { "Date dateFormat 결과: $data -> $v1" }
    }
}
