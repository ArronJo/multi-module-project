package com.snc.test.extension.calendar

import com.snc.zero.extensions.calendar.addDay
import com.snc.zero.extensions.calendar.addHour
import com.snc.zero.extensions.calendar.addMillisecond
import com.snc.zero.extensions.calendar.addMinute
import com.snc.zero.extensions.calendar.addMonth
import com.snc.zero.extensions.calendar.addSecond
import com.snc.zero.extensions.calendar.addYear
import com.snc.zero.extensions.calendar.diff
import com.snc.zero.extensions.calendar.endOfDay
import com.snc.zero.extensions.calendar.endOfMonth
import com.snc.zero.extensions.calendar.getDay
import com.snc.zero.extensions.calendar.getHour
import com.snc.zero.extensions.calendar.getLastDayOfMonth
import com.snc.zero.extensions.calendar.getMillisecond
import com.snc.zero.extensions.calendar.getMinute
import com.snc.zero.extensions.calendar.getMonth
import com.snc.zero.extensions.calendar.getSecond
import com.snc.zero.extensions.calendar.getYear
import com.snc.zero.extensions.calendar.setDay
import com.snc.zero.extensions.calendar.setHour
import com.snc.zero.extensions.calendar.setMillisecond
import com.snc.zero.extensions.calendar.setMinute
import com.snc.zero.extensions.calendar.setMonth
import com.snc.zero.extensions.calendar.setSecond
import com.snc.zero.extensions.calendar.setYear
import com.snc.zero.extensions.calendar.startOfMonth
import com.snc.zero.extensions.calendar.toCalendar
import com.snc.zero.extensions.format.formatDateTime
import com.snc.zero.extensions.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date

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
    }

    @Test
    fun `Calendar 연산 1`() {
        val c = Calendar.getInstance()
            .setYear(2024)
            .setMonth(2)
            .setDay(14)
        logger.debug { "${c.getDay()}, ${c.getLastDayOfMonth()}" }

        c.endOfMonth()
        logger.debug { "${c.getDay()}, ${c.getLastDayOfMonth()}" }
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
        logger.debug { "Calendar 날짜 표현: ${cal.print()} -> $v1" }
        logger.debug { "Calendar 날짜 표현: ${cal.print()} -> $v2" }
        logger.debug { "Calendar 날짜 표현: ${cal.print()} -> $v3" }
    }

    @Test
    fun `Calendar 날짜 비교 1`() {
        // given
        val cal = Calendar.getInstance()
        val cal2 = cal.clone() as Calendar
        // when
        cal2.addMonth(1)
        // then
        logger.debug { "Calendar diff 결과: ${cal.diff(cal2)}" }
    }

    @Test
    fun `Calendar 날짜 비교 2`() {
        // given
        val cal = Calendar.getInstance()
        val cal2 = cal.clone() as Calendar
        // when
        cal2.addMonth(-1)
        // then
        logger.debug { "Calendar diff 결과: ${cal.diff(cal2)}" }
    }

    @Test
    fun `Calendar start day of month`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.startOfMonth()
        // then
        logger.debug { "Calendar start day of month 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
    }

    @Test
    fun `Calendar last day of month`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.endOfMonth()
        // then
        logger.debug { "Calendar end day of month 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
    }

    @Test
    fun `Calendar last time of day`() {
        // given
        val cal = Calendar.getInstance()
        // when
        cal.endOfDay()
        // then
        logger.debug { "Calendar last time of day 결과: ${cal.getYear()}-${cal.getMonth()}-${cal.getDay()} ${cal.getHour()}:${cal.getMinute()}:${cal.getSecond()}.${cal.getMillisecond()}" }
    }

    @Test
    fun `String toCalendar 테스트 1`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.toCalendar()
        // then
        logger.debug { "String toCalendar 결과: $data -> ${v1.print()}" }
    }

    @Test
    fun `String toCalendar 테스트 2`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.toCalendar("yyyyMMddHHmm")
        // then
        logger.debug { "String toCalendar 결과: $data -> ${v1.print()}" }
    }

    @Test
    fun `String toCalendar 테스트 3`() {
        // given
        val data = "20240402175932"
        // when
        val v1 = data.toCalendar("yyyyMMddHH")
        // then
        logger.debug { "String toCalendar 결과: $data -> ${v1.print()}" }
    }

    @Test
    fun `String toCalendar 예외 1`() {
        // given
        val data = "2024"
        // when
        val v1 = data.toCalendar("yyyyMMddHH")
        // then
        logger.debug { "String toCalendar 결과: $data -> ${v1.print()}" }
    }

    @Test
    fun `Date toCalendar 테스트`() {
        // given
        val data = Date()
        // when
        val v1 = data.toCalendar()
        // then
        logger.debug { "Date toCalendar 결과: $data -> ${v1.print()}" }
    }
}
