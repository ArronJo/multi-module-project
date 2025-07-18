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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
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

    @Nested
    @DisplayName("정상적인 날짜 문자열 파싱")
    inner class ValidDateStringParsing {

        @Test
        @DisplayName("기본 패턴으로 정상 날짜 파싱")
        fun `should parse valid date string with default pattern`() {
            // Given
            val dateString = "20240315143000"

            // When
            val result = dateString.toCalendar()

            // Then
            assertEquals(2024, result.get(Calendar.YEAR))
            assertEquals(Calendar.MARCH, result.get(Calendar.MONTH))
            assertEquals(15, result.get(Calendar.DAY_OF_MONTH))
            assertEquals(14, result.get(Calendar.HOUR_OF_DAY))
            assertEquals(30, result.get(Calendar.MINUTE))
            assertEquals(0, result.get(Calendar.SECOND))
        }

        @Test
        @DisplayName("커스텀 패턴으로 정상 날짜 파싱")
        fun `should parse valid date string with custom pattern`() {
            // Given
            val dateString = "2024-03-15"
            val pattern = "yyyy-MM-dd"

            // When
            val result = dateString.toCalendar(pattern)

            // Then
            assertEquals(2024, result.get(Calendar.YEAR))
            assertEquals(Calendar.MARCH, result.get(Calendar.MONTH))
            assertEquals(15, result.get(Calendar.DAY_OF_MONTH))
        }
    }

    @Nested
    @DisplayName("비정상적인 날짜 문자열 파싱")
    inner class InvalidDateStringParsing {

        @Test
        @DisplayName("완전히 잘못된 형식의 날짜 문자열")
        fun `should return current calendar when date string is completely invalid`() {
            // Given
            val invalidDateString = "invalid-date-string"
            val beforeTest = Calendar.getInstance()

            // When
            val result = invalidDateString.toCalendar()
            val afterTest = Calendar.getInstance()

            // Then
            // ParseException이 발생하면 현재 시간의 Calendar가 반환되어야 함
            assertTrue(result.timeInMillis >= beforeTest.timeInMillis)
            assertTrue(result.timeInMillis <= afterTest.timeInMillis)
        }

        @Test
        @DisplayName("부분적으로 잘못된 형식의 날짜 문자열")
        fun `should return current calendar when date string is partially invalid`() {
            // Given
            val partiallyInvalidDateString = "2024aa15143000"
            val beforeTest = Calendar.getInstance()

            // When
            val result = partiallyInvalidDateString.toCalendar()
            val afterTest = Calendar.getInstance()

            // Then
            assertTrue(result.timeInMillis >= beforeTest.timeInMillis)
            assertTrue(result.timeInMillis <= afterTest.timeInMillis)
        }

        @Test
        @DisplayName("존재하지 않는 날짜는 자동으로 조정됨")
        fun `should auto-adjust non-existent date`() {
            // Given
            val nonExistentDate = "20240230143000" // 2월 30일은 존재하지 않음

            // When
            val result = nonExistentDate.toCalendar()

            // Then
            // SimpleDateFormat은 2월 30일을 3월 1일로 자동 조정함
            assertEquals(2024, result.get(Calendar.YEAR))
            assertEquals(Calendar.MARCH, result.get(Calendar.MONTH))
            assertEquals(1, result.get(Calendar.DAY_OF_MONTH))
            assertEquals(14, result.get(Calendar.HOUR_OF_DAY))
            assertEquals(30, result.get(Calendar.MINUTE))
            assertEquals(0, result.get(Calendar.SECOND))
        }
    }

    @Nested
    @DisplayName("엣지 케이스 테스트")
    inner class EdgeCaseTests {

        @Test
        @DisplayName("빈 문자열")
        fun `should return current calendar when string is empty`() {
            // Given
            val emptyString = ""
            val beforeTest = Calendar.getInstance()

            // When
            val result = emptyString.toCalendar()
            val afterTest = Calendar.getInstance()

            // Then
            assertTrue(result.timeInMillis >= beforeTest.timeInMillis)
            assertTrue(result.timeInMillis <= afterTest.timeInMillis)
        }

        @Test
        @DisplayName("패턴보다 짧은 문자열")
        fun `should handle string shorter than pattern`() {
            // Given
            val shortString = "2024"
            val beforeTest = Calendar.getInstance()

            // When
            val result = shortString.toCalendar()
            val afterTest = Calendar.getInstance()

            // Then
            assertTrue(result.timeInMillis >= beforeTest.timeInMillis)
            assertTrue(result.timeInMillis <= afterTest.timeInMillis)
        }

        @Test
        @DisplayName("패턴보다 긴 문자열")
        fun `should handle string longer than pattern`() {
            // Given
            val longString = "20240315143000123456789"

            // When
            val result = longString.toCalendar()

            // Then
            assertEquals(2024, result.get(Calendar.YEAR))
            assertEquals(Calendar.MARCH, result.get(Calendar.MONTH))
            assertEquals(15, result.get(Calendar.DAY_OF_MONTH))
            assertEquals(14, result.get(Calendar.HOUR_OF_DAY))
            assertEquals(30, result.get(Calendar.MINUTE))
            assertEquals(0, result.get(Calendar.SECOND))
        }
    }

    @Nested
    @DisplayName("Null 반환 시나리오 테스트")
    inner class NullReturnScenarioTests {

        @Test
        @DisplayName("SimpleDateFormat.parse()가 null을 반환하는 경우")
        fun `should return current calendar when SimpleDateFormat parse returns null`() {
            // Given
            val dateString = "invalid"
            val beforeTest = Calendar.getInstance()

            // When
            val result = dateString.toCalendar()
            val afterTest = Calendar.getInstance()

            // Then
            // parse()가 null을 반환하면 date?.let 블록이 실행되지 않고
            // 현재 시간의 Calendar가 반환되어야 함
            assertNotNull(result)
            assertTrue(result.timeInMillis >= beforeTest.timeInMillis)
            assertTrue(result.timeInMillis <= afterTest.timeInMillis)
        }

        @Test
        @DisplayName("다양한 잘못된 형식에 대한 null 반환 처리")
        fun `should handle various invalid formats that return null`() {
            // Given
            val invalidFormats = listOf(
                "not-a-date",
                "2024-13-32", // 잘못된 월/일
                "abcd1234efgh",
                "2024/03/15", // 패턴과 다른 구분자
                "24-03-15" // 2자리 연도
            )

            invalidFormats.forEach { invalidFormat ->
                // When
                val beforeTest = Calendar.getInstance()
                val result = invalidFormat.toCalendar()
                val afterTest = Calendar.getInstance()

                // Then
                assertNotNull(result, "Result should not be null for input: $invalidFormat")
                assertTrue(
                    result.timeInMillis >= beforeTest.timeInMillis,
                    "Result should be current time for input: $invalidFormat"
                )
                assertTrue(
                    result.timeInMillis <= afterTest.timeInMillis,
                    "Result should be current time for input: $invalidFormat"
                )
            }
        }
    }
}
