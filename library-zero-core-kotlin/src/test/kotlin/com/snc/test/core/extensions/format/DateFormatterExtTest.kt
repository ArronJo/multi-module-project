package com.snc.test.core.extensions.format

import com.snc.zero.core.extensions.format.formatDateTime
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date

private val logger = TLogging.logger { }

class DateFormatterExtTest : BaseJUnit5Test() {

    @Test
    fun `String dateFormat 1`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.formatDateTime(inputFormat = "yyyyMMddHHmmss", outputFormat = "yyyy-MM-dd HH:mm")
        // then
        logger.debug { "String dateFormat 결과: $data -> $v1" }
        assertEquals(v1, "2024-04-02 17:59")
    }

    @Test
    fun `String dateFormat 2`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.formatDateTime(inputFormat = "yyyyMMddHHmmss")
        // then
        logger.debug { "String dateFormat 결과: $data -> $v1" }
        assertEquals(v1, "20240402175932")
    }

    @Test
    fun `Calendar dateFormat 1`() {
        // given
        val data = Calendar.getInstance()
        // when
        val v1 = data.formatDateTime(outputFormat = "yyyy-MM-dd HH:mm")
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
        val v1 = data.formatDateTime(outputFormat = "yyyy-MM-dd HH:mm")
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