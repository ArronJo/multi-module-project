package com.snc.test.core.date

import com.snc.zero.core.date.DateTimeFormat
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DateTimeFormatTest : BaseJUnit5Test() {

    @Test
    fun `날짜 format 테스트 1`() {
        // given
        val dateStr = "20170215"
        // when
        val v1 = DateTimeFormat.format(dateStr, "yyyyMMdd", "yyyyMMddHHmmss")
        // then
        logger.debug { "날짜 format: $dateStr => $v1" }
        //assertEquals(v1, true)
    }

    @Test
    fun `날짜 format 테스트 2`() {
        // given
        val cal = Calendar.getInstance()
        // when
        val v1 = DateTimeFormat.format(cal, "yyyyMMddHHmmss")
        // then
        logger.debug { "날짜 format: $cal => $v1" }
        //assertEquals(v1, true)
    }

    @Test
    fun `날짜 format 테스트 3`() {
        // given
        val date = Date()
        // when
        val v1 = DateTimeFormat.format(date, "yyyyMMddHHmmss")
        // then
        logger.debug { "날짜 format: $date => $v1" }
        //assertEquals(v1, true)
    }
}
