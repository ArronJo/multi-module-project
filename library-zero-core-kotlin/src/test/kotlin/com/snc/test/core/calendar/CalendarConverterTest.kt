package com.snc.test.core.calendar

import com.snc.zero.core.calendar.CalendarConverter
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class CalendarConverterTest : BaseJUnit5Test() {

    @Test
    fun `CalendarConverter toCalendar 변환 1`() {
        // given
        // when
        val v1 = CalendarConverter.Companion.toCalendar("20240229")
        // then
        logger.debug { "CalendarConverter toCalendar 1 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `CalendarConverter toCalendar 변환 2`() {
        // given
        // when
        val v1 = CalendarConverter.Companion.toCalendar(2024, 2, 29)
        // then
        logger.debug { "CalendarConverter toCalendar 2 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `CalendarConverter toCalendar 변환 3`() {
        // given
        // when
        val v1 = CalendarConverter.Companion.toCalendar("2024", "02", "29")
        //val v2 = java.util.Calendar.getInstance().set(2024, 2, 29, 0, 0, 0)
        // then
        logger.debug { "CalendarConverter toCalendar 3 결과: $v1" }
        //assertEquals(v1, v2)
    }

    @Test
    fun `CalendarConverter toDateString 변환 1`() {
        // given
        val cal = java.util.Calendar.getInstance()
        // when
        val v1 = CalendarConverter.Companion.toDateString(cal, "yyyyMMddHHmmss")
        // then
        logger.debug { "CalendarConverter toDateString 1 결과: $v1" }
        //assertEquals(v1, v2)
    }
}
