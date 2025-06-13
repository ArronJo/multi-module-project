package com.snc.test.core.calendar

import com.snc.zero.core.calendar.CalendarCompat
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class CalendarCompatTest : BaseJUnit5Test() {

    @Test
    fun `CalendarBuilder 메소드 체이닝 (Method Chaining) 패턴`() {
        // given
        // when
        val v1 = CalendarCompat.with()
            .setYear(2024)
            .setMonth(2)
            .setDay(29)
            .getCalendar()
        // then
        logger.debug { "Calendar Builder 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `CalendarBuilder ToDateString`() {
        // given
        // when
        val v1 = CalendarCompat.with()
            .setYear(2024)
            .setMonth(2)
            .setDay(29)
            .toDateString()
        // then
        logger.debug { "Calendar Builder 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `default initialization should be 1900-01-01 00-00-00`() {
        val calendar = CalendarCompat.with()

        assertEquals(1900, calendar.getYear())
        assertEquals(1, calendar.getMonth())
        assertEquals(1, calendar.getDay())
        assertEquals(0, calendar.getHour())
        assertEquals(0, calendar.getMinute())
        assertEquals(0, calendar.getSecond())
    }

    @Test
    fun `setYear, setMonth, setDay should work correctly`() {
        val calendar = CalendarCompat.with()
            .setYear(2025)
            .setMonth(6)
            .setDay(12)

        assertEquals(2025, calendar.getYear())
        assertEquals(6, calendar.getMonth())
        assertEquals(12, calendar.getDay())
    }

    @Test
    fun `addYear and addMonth and addDay should modify date correctly`() {
        val calendar = CalendarCompat.with()
            .setYear(2025).setMonth(6).setDay(12)
            .addYear(1).addMonth(2).addDay(3)

        assertEquals(2026, calendar.getYear())
        assertEquals(8, calendar.getMonth())
        assertEquals(15, calendar.getDay())
    }

    @Test
    fun `startOfDay should reset time`() {
        val calendar = CalendarCompat.with()
            .setHour(15).setMinute(45).setSecond(30)
            .startOfDay()

        assertEquals(0, calendar.getHour())
        assertEquals(0, calendar.getMinute())
        assertEquals(0, calendar.getSecond())
        assertEquals(0, calendar.getMillisecond())
    }

    @Test
    fun `endOfDay should set time to 23_59_59_999`() {
        val calendar = CalendarCompat.with().endOfDay()

        assertEquals(23, calendar.getHour())
        assertEquals(59, calendar.getMinute())
        assertEquals(59, calendar.getSecond())
        assertEquals(999, calendar.getMillisecond())
    }

    @Test
    fun `getLastDayOfMonth should return correct last day`() {
        val calendar = CalendarCompat.with().setYear(2024).setMonth(2) // leap year

        assertEquals(29, calendar.getLastDayOfMonth())

        calendar.setYear(2023)
        assertEquals(28, calendar.getLastDayOfMonth())
    }

    @Test
    fun `toDateString should format date properly`() {
        val calendar = CalendarCompat.with().setYear(2023).setMonth(12).setDay(31)
        val str = calendar.toDateString("yyyy-MM-dd")

        assertTrue(str.startsWith("2023-12-31"))
    }

    @Test
    fun `startOfMonth should set day to 1 and time to 00_00_00`() {
        val calendar = CalendarCompat.with().setYear(2023).setMonth(5).setDay(10)
            .startOfMonth()

        assertEquals(1, calendar.getDay())
        assertEquals(0, calendar.getHour())
        assertEquals(0, calendar.getMinute())
        assertEquals(0, calendar.getSecond())
    }

    @Test
    fun `endOfMonth should set date to last day and time to end of day`() {
        val calendar = CalendarCompat.with().setYear(2023).setMonth(5).setDay(1)
            .endOfMonth()

        assertEquals(31, calendar.getDay())
        assertEquals(23, calendar.getHour())
        assertEquals(59, calendar.getMinute())
        assertEquals(59, calendar.getSecond())
        assertEquals(999, calendar.getMillisecond())
    }

    @Test
    fun `addTimeInMillis should increment time`() {
        val calendar = CalendarCompat.with()
        val original = calendar.getTimeInMillis()
        calendar.addTimeInMillis(1000)

        assertEquals(original + 1000, calendar.getTimeInMillis())
    }

    @Test
    fun `setCalendar should parse date string correctly`() {
        val calendar = CalendarCompat.with()
            .setCalendar("20240612") // yyyyMMdd 형식 가정

        assertEquals(2024, calendar.getYear())
        assertEquals(6, calendar.getMonth())
        assertEquals(12, calendar.getDay())
    }

    @Test
    fun `setTimeInMillis should update calendar time`() {
        val calendar = CalendarCompat.with()
        val now = System.currentTimeMillis()
        calendar.setTimeInMillis(now.toInt())

        assertEquals(now.toInt().toLong(), calendar.getTimeInMillis())
    }

    @Test
    fun `addHour should increase hour correctly`() {
        val calendar = CalendarCompat.with().setHour(10)
        calendar.addHour(5)

        assertEquals(15, calendar.getHour())
    }

    @Test
    fun `addMinute should increase minute correctly`() {
        val calendar = CalendarCompat.with().setMinute(30)
        calendar.addMinute(15)

        assertEquals(45, calendar.getMinute())
    }

    @Test
    fun `addSecond should increase second correctly`() {
        val calendar = CalendarCompat.with()
            .setMinute(10)
            .setSecond(20)

        calendar.addSecond(40)

        assertEquals(0, calendar.getSecond()) // 60초 → 0초
        assertEquals(11, calendar.getMinute()) // 10분 + 1분
    }

    @Test
    fun `addMillisecond should increase millisecond correctly`() {
        val calendar = CalendarCompat.with().setMillisecond(500)
        calendar.addMillisecond(250)

        assertEquals(750, calendar.getMillisecond())
    }
}
