package com.snc.test.core.date

import com.snc.zero.core.date.DateTimeParser
import com.snc.zero.extensions.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.Modifier
import java.util.*

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
@DisplayName("DateTimeParser 클래스 테스트")
class DateTimeParserTest : BaseJUnit5Test() {

    @Test
    fun `날짜 parseDate 테스트 1`() {
        // given
        val dateStr = "20170215"
        // when
        val v1 = DateTimeParser.parseDate(dateStr)
        // then
        logger.debug { "날짜 parseDate: $dateStr => ${v1.print()}" }
        assertEquals(v1.print(), "[2017, 2, 15]")
    }

    @Test
    fun `날짜 filter 테스트 1`() {
        // given
        val dateStr = "2017-02-15"
        // when
        val v1 = DateTimeParser.filter(dateStr)
        // then
        logger.debug { "날짜 filter: $dateStr => $v1" }
        assertEquals(v1, "20170215")
    }

    @Test
    fun `날짜 toString 테스트 1`() {
        // given
        val cal = Calendar.getInstance()
        // when
        val v1 = DateTimeParser.toString(cal)
        // then
        logger.debug { "날짜 filter: $cal => $v1" }
        //assertEquals(v1, "20170215")
    }

    @Test
    fun `날짜 today 테스트 1`() {
        // given
        // when
        val v1 = DateTimeParser.today()
        val v2 = DateTimeParser.today(format = "yyyyMMdd")
        val v3 = DateTimeParser.today2(format = "yyMMdd")
        val v4 = DateTimeParser.today2(format = "yyyyMMdd")
        val v5 = DateTimeParser.tomorrow()
        // then
        logger.debug { "날짜 today: $v1" }
        logger.debug { "날짜 today: $v2" }
        logger.debug { "날짜 today: $v3" }
        logger.debug { "날짜 today: $v4" }
        logger.debug { "날짜 tomorrow: $v5" }
    }

    @Test
    fun `날짜 lastDayOfMonth 테스트 1`() {
        // given
        val cal = Calendar.getInstance()
        // when
        val v1 = DateTimeParser.lastDayOfMonth(cal)
        // then
        logger.debug { "날짜 ${cal.get(Calendar.YEAR)}년 ${cal.get(Calendar.MONTH) + 1} 월은 $v1 가 마지막 날이다." }
        //assertEquals(v1, "20170215")
    }

    @Nested
    @DisplayName("클래스 구조 테스트")
    inner class ClassStructureTest {

        @Test
        @DisplayName("DateTimeParser는 private constructor를 가져야 함")
        fun `should have private constructor`() {
            // When & Then
            val constructors = DateTimeParser::class.java.declaredConstructors
            assertTrue(constructors.all { Modifier.isPrivate(it.modifiers) })
        }

        @Test
        @DisplayName("DateTimeParser는 인스턴스를 생성할 수 없어야 함")
        fun `should not be instantiable`() {
            // When & Then
            val constructor = DateTimeParser::class.java.getDeclaredConstructor()
            assertThrows<IllegalAccessException> {
                constructor.newInstance()
            }
        }
    }

    @Nested
    @DisplayName("parseDate 메서드 테스트")
    inner class ParseDateTest {

        @Test
        @DisplayName("정상적인 날짜 문자열을 파싱할 수 있어야 함")
        fun `should parse valid date string`() {
            // Given
            val dateString = "20241225"

            // When
            val result = DateTimeParser.parseDate(dateString)

            // Then
            assertEquals(arrayOf(2024, 12, 25).toList(), result.toList())
        }

        @Test
        @DisplayName("8자리 날짜 문자열을 올바르게 파싱해야 함")
        fun `should parse 8 digit date string correctly`() {
            // Given
            val dateString = "19990101"

            // When
            val result = DateTimeParser.parseDate(dateString)

            // Then
            assertEquals(1999, result[0]) // year
            assertEquals(1, result[1]) // month
            assertEquals(1, result[2]) // day
        }

        @Test
        @DisplayName("잘못된 길이의 문자열은 예외를 발생시켜야 함")
        fun `should throw exception for invalid length string`() {
            // Given
            val shortString = "2024"

            // When & Then
            assertThrows<StringIndexOutOfBoundsException> {
                DateTimeParser.parseDate(shortString)
            }
        }

        @Test
        @DisplayName("숫자가 아닌 문자는 예외를 발생시켜야 함")
        fun `should throw exception for non-numeric characters`() {
            // Given
            val invalidString = "202412ab"

            // When & Then
            assertThrows<NumberFormatException> {
                DateTimeParser.parseDate(invalidString)
            }
        }
    }

    @Nested
    @DisplayName("filter 메서드 테스트")
    inner class FilterTest {

        @Test
        @DisplayName("점, 하이픈, 콜론, 공백을 제거해야 함")
        fun `should remove dots, hyphens, colons and spaces`() {
            // Given
            val dateStr = "2024-12-25 10:30:45.123"

            // When
            val result = DateTimeParser.filter(dateStr)

            // Then
            assertEquals("20241225103045123", result)
        }

        @Test
        @DisplayName("연속된 구분자를 모두 제거해야 함")
        fun `should remove consecutive separators`() {
            // Given
            val dateStr = "2024--12..25  10::30"

            // When
            val result = DateTimeParser.filter(dateStr)

            // Then
            assertEquals("202412251030", result)
        }

        @Test
        @DisplayName("구분자가 없는 문자열은 그대로 반환해야 함")
        fun `should return unchanged string without separators`() {
            // Given
            val dateStr = "20241225103045"

            // When
            val result = DateTimeParser.filter(dateStr)

            // Then
            assertEquals("20241225103045", result)
        }

        @Test
        @DisplayName("빈 문자열은 빈 문자열을 반환해야 함")
        fun `should return empty string for empty input`() {
            // Given
            val dateStr = ""

            // When
            val result = DateTimeParser.filter(dateStr)

            // Then
            assertEquals("", result)
        }
    }

    @Nested
    @DisplayName("toString 메서드 테스트")
    inner class ToStringTest {

        @Test
        @DisplayName("Calendar 객체를 yyyyMMddHHmmss 형식으로 변환해야 함")
        fun `should convert calendar to yyyyMMddHHmmss format`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.DECEMBER, 25, 15, 30, 45)
            }

            // When
            val result = DateTimeParser.toString(calendar)

            // Then
            assertEquals("20241225153045", result)
        }

        @Test
        @DisplayName("한 자리 월과 일을 올바르게 포맷해야 함")
        fun `should format single digit month and day correctly`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 5, 9, 8, 7)
            }

            // When
            val result = DateTimeParser.toString(calendar)

            // Then
            assertEquals("20240105090807", result)
        }
    }

    @Nested
    @DisplayName("today 메서드 테스트")
    inner class TodayTest {

        @Test
        @DisplayName("지정된 형식으로 오늘 날짜를 반환해야 함")
        fun `should return today in specified format`() {
            // Given
            val format = "yyyy-MM-dd"

            // When
            val result = DateTimeParser.today(format)

            // Then
            assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        }

        @Test
        @DisplayName("Calendar 객체로 오늘을 반환해야 함")
        fun `should return today as calendar`() {
            // When
            val result = DateTimeParser.today()

            // Then
            val today = Calendar.getInstance()
            assertEquals(today.get(Calendar.YEAR), result.get(Calendar.YEAR))
            assertEquals(today.get(Calendar.MONTH), result.get(Calendar.MONTH))
            assertEquals(today.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH))
        }
    }

    @Nested
    @DisplayName("today2 메서드 테스트")
    inner class Today2Test {

        @Test
        @DisplayName("SimpleDateFormat을 사용하여 오늘 날짜를 반환해야 함")
        fun `should return today using SimpleDateFormat`() {
            // When
            val result = DateTimeParser.today2(format = "yyyyMMdd")

            // Then
            assertTrue(result.matches(Regex("\\d{8}")))
            assertEquals(8, result.length)
        }

        @Test
        @DisplayName("지정된 형식으로 오늘 날짜를 반환해야 함")
        fun `should return today in specified format using SimpleDateFormat`() {
            // Given
            val format = "yyyy/MM/dd"

            // When
            val result = DateTimeParser.today2(format)

            // Then
            assertTrue(result.matches(Regex("\\d{4}/\\d{2}/\\d{2}")))
        }

        @Test
        @DisplayName("today()와 today2()는 같은 날짜를 반환해야 함")
        fun `today and today2 should return same date`() {
            // When
            val result1 = DateTimeParser.today(format = "yyyyMMdd")
            val result2 = DateTimeParser.today2(format = "yyyyMMdd")

            // Then
            assertEquals(result1, result2)
        }
    }

    @Nested
    @DisplayName("tomorrow 메서드 테스트")
    inner class TomorrowTest {

        @Test
        @DisplayName("내일 날짜를 반환해야 함")
        fun `should return tomorrow date`() {
            // When
            val tomorrow = DateTimeParser.tomorrow()

            // Then
            val expectedTomorrow = Calendar.getInstance().apply {
                add(Calendar.DATE, 1)
            }

            assertEquals(expectedTomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.YEAR))
            assertEquals(expectedTomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.MONTH))
            assertEquals(expectedTomorrow.get(Calendar.DAY_OF_MONTH), tomorrow.get(Calendar.DAY_OF_MONTH))
        }

        @Test
        @DisplayName("오늘과 내일은 1일 차이여야 함")
        fun `today and tomorrow should have 1 day difference`() {
            // When
            val today = DateTimeParser.today()
            val tomorrow = DateTimeParser.tomorrow()

            // Then
            val diffInMillis = tomorrow.timeInMillis - today.timeInMillis
            val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)
            assertEquals(1L, diffInDays)
        }
    }

    @Nested
    @DisplayName("lastDayOfMonth 메서드 테스트")
    inner class LastDayOfMonthTest {

        @Test
        @DisplayName("현재 월의 마지막 날을 반환해야 함")
        fun `should return last day of current month`() {
            // When
            val result = DateTimeParser.lastDayOfMonth()

            // Then
            val calendar = Calendar.getInstance()
            val expected = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("2월의 마지막 날을 올바르게 반환해야 함 (평년)")
        fun `should return correct last day of February in non-leap year`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2023, Calendar.FEBRUARY, 1) // 2023년은 평년
            }

            // When
            val result = DateTimeParser.lastDayOfMonth(calendar)

            // Then
            assertEquals(28, result)
        }

        @Test
        @DisplayName("2월의 마지막 날을 올바르게 반환해야 함 (윤년)")
        fun `should return correct last day of February in leap year`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.FEBRUARY, 1) // 2024년은 윤년
            }

            // When
            val result = DateTimeParser.lastDayOfMonth(calendar)

            // Then
            assertEquals(29, result)
        }

        @Test
        @DisplayName("31일까지 있는 월의 마지막 날을 올바르게 반환해야 함")
        fun `should return 31 for months with 31 days`() {
            // Given
            val months = listOf(
                Calendar.JANUARY,
                Calendar.MARCH,
                Calendar.MAY,
                Calendar.JULY,
                Calendar.AUGUST,
                Calendar.OCTOBER,
                Calendar.DECEMBER
            )

            months.forEach { month ->
                // Given
                val calendar = Calendar.getInstance().apply {
                    set(2024, month, 1)
                }

                // When
                val result = DateTimeParser.lastDayOfMonth(calendar)

                // Then
                assertEquals(31, result, "Month $month should have 31 days")
            }
        }

        @Test
        @DisplayName("30일까지 있는 월의 마지막 날을 올바르게 반환해야 함")
        fun `should return 30 for months with 30 days`() {
            // Given
            val months = listOf(
                Calendar.APRIL,
                Calendar.JUNE,
                Calendar.SEPTEMBER,
                Calendar.NOVEMBER
            )

            months.forEach { month ->
                // Given
                val calendar = Calendar.getInstance().apply {
                    set(2024, month, 1)
                }

                // When
                val result = DateTimeParser.lastDayOfMonth(calendar)

                // Then
                assertEquals(30, result, "Month $month should have 30 days")
            }
        }
    }
}
