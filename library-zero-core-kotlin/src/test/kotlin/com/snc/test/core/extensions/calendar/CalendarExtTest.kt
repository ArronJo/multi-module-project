package com.snc.test.core.extensions.calendar

import com.snc.zero.core.extensions.calendar.*
import com.snc.zero.core.extensions.format.formatDateTime
import com.snc.zero.core.extensions.text.toStrings
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.*

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class CalendarExtTest : BaseJUnit5Test() {

    @Test
    fun `Calendar Set 1`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.setYear(2024)
        cal.setMonth(2)
        cal.setDay(2)
        cal.setHour(9)
        cal.setMinute(13)
        cal.setSecond(22)
        cal.setMillisecond(123)
        // then
        logger.debug { "Calendar 연산 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Calendar Add 1`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.addYear(1)
        cal.addMonth(1)
        cal.addDay(1)
        cal.addHour(1)
        cal.addMinute(1)
        cal.addSecond(1)
        cal.addMillisecond(1)
        // then
        logger.debug { "Calendar 연산 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Calendar 날짜 표현`() {
        // given
        val cal = Calendar.getInstance()
        // when
        val v1 = cal.formatDateTime("yy-MM-dd")
        val v2 = cal.formatDateTime("yyyy-MM-dd")
        val v3 = cal.formatDateTime("yyyy-MM-dd HH:mm:ss")
        // then
        logger.debug { "Calendar 날짜 표현: ${cal.toStrings()} -> $v1" }
        logger.debug { "Calendar 날짜 표현: ${cal.toStrings()} -> $v2" }
        logger.debug { "Calendar 날짜 표현: ${cal.toStrings()} -> $v3" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Calendar 날짜 비교`() {
        // given
        val cal = Calendar.getInstance()
        val cal2 = cal.clone() as Calendar
        // when
        cal2.addMonth(1)
        // then
        logger.debug { "Calendar diff 결과: ${cal.diff(cal2)}" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Calendar start day of month`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.startOfMonth()
        // then
        logger.debug { "Calendar start day of month 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Calendar last day of month`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.endOfMonth()
        // then
        logger.debug { "Calendar end day of month 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Calendar last time of day`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.endOfDay()
        // then
        logger.debug { "Calendar last time of day 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
        //assertEquals(v1, false)
    }

    @Test
    fun `String toCalendar 테스트`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.toCalendar("yyyyMMddHHmmss")
        // then
        logger.debug { "String toCalendar 결과: $data -> ${v1.toStrings()}" }
        //assertEquals(v1, false)
    }
}