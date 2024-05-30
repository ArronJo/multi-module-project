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
    fun `String dateFormat`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.formatDateTime(inputFormat = "yyyyMMddHHmmss", outputFormat = "yyyy-MM-dd HH:mm")
        // then
        logger.debug { "String dateFormat 결과: $data -> $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Calendar dateFormat`() {
        // given
        val data = Calendar.getInstance()
        // when
        val v1 = data.formatDateTime(outputFormat = "yyyy-MM-dd HH:mm")
        // then
        logger.debug { "Calendar dateFormat 결과: $data -> $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Date dateFormat`() {
        // given
        val data = Date()
        // when
        val v1 = data.formatDateTime(outputFormat = "yyyy-MM-dd HH:mm")
        // then
        logger.debug { "Date dateFormat 결과: $data -> $v1" }
        //assertEquals(v1, false)
    }
}