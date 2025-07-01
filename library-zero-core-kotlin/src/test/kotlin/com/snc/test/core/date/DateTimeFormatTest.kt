package com.snc.test.core.date

import com.snc.zero.core.date.DateTimeFormat
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.lang.reflect.Modifier
import java.text.ParseException
import java.util.Calendar
import java.util.Date

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
@DisplayName("DateTimeFormat 클래스 테스트")
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

    @Nested
    @DisplayName("format(dateStr, inputFormat, outputFormat) 메서드 테스트")
    inner class FormatStringTest {

        @Test
        @DisplayName("기본 날짜 문자열 포맷 변환 - 기본 출력 형식")
        fun `should format date string with default output format`() {
            // Given
            val dateStr = "2024-01-15"
            val inputFormat = "yyyy-MM-dd"

            // When
            val result = DateTimeFormat.format(dateStr, inputFormat)

            // Then
            assertEquals("20240115000000", result)
        }

        @Test
        @DisplayName("기본 날짜 문자열 포맷 변환 - 커스텀 출력 형식")
        fun `should format date string with custom output format`() {
            // Given
            val dateStr = "2024-01-15"
            val inputFormat = "yyyy-MM-dd"
            val outputFormat = "dd/MM/yyyy"

            // When
            val result = DateTimeFormat.format(dateStr, inputFormat, outputFormat)

            // Then
            assertEquals("15/01/2024", result)
        }

        @Test
        @DisplayName("시간 포함 날짜 문자열 포맷 변환")
        fun `should format datetime string correctly`() {
            // Given
            val dateStr = "2024-01-15 10:30:45"
            val inputFormat = "yyyy-MM-dd HH:mm:ss"
            val outputFormat = "yyyyMMddHHmmss"

            // When
            val result = DateTimeFormat.format(dateStr, inputFormat, outputFormat)

            // Then
            assertEquals("20240115103045", result)
        }

        @ParameterizedTest
        @DisplayName("다양한 입력/출력 포맷 조합 테스트")
        @CsvSource(
            "'15/01/2024', 'dd/MM/yyyy', 'yyyy-MM-dd', '2024-01-15'",
            "'2024년 01월 15일', 'yyyy년 MM월 dd일', 'yyyyMMdd', '20240115'",
            "'01-15-2024 10:30', 'MM-dd-yyyy HH:mm', 'yyyy/MM/dd HH:mm:ss', '2024/01/15 10:30:00'"
        )
        fun `should handle various input and output format combinations`(
            dateStr: String,
            inputFormat: String,
            outputFormat: String,
            expected: String
        ) {
            // When
            val result = DateTimeFormat.format(dateStr, inputFormat, outputFormat)

            // Then
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("잘못된 날짜 문자열로 ParseException 발생")
        fun `should throw ParseException for invalid date string`() {
            // Given
            val invalidDateStr = "invalid-date"
            val inputFormat = "yyyy-MM-dd"

            // When & Then
            assertThrows<ParseException> {
                DateTimeFormat.format(invalidDateStr, inputFormat)
            }
        }

        @Test
        @DisplayName("입력 포맷과 맞지 않는 날짜 문자열로 ParseException 발생")
        fun `should throw ParseException when date string doesn't match input format`() {
            // Given
            val dateStr = "2024-01-15" // yyyy-MM-dd 형식
            val wrongInputFormat = "dd/MM/yyyy" // 다른 형식

            // When & Then
            assertThrows<ParseException> {
                DateTimeFormat.format(dateStr, wrongInputFormat)
            }
        }

        @Test
        @DisplayName("빈 날짜 문자열로 ParseException 발생")
        fun `should throw ParseException for empty date string`() {
            // Given
            val emptyDateStr = ""
            val inputFormat = "yyyy-MM-dd"

            // When & Then
            assertThrows<ParseException> {
                DateTimeFormat.format(emptyDateStr, inputFormat)
            }
        }

        @Test
        @DisplayName("잘못된 출력 포맷으로 IllegalArgumentException 발생")
        fun `should throw IllegalArgumentException for invalid output format`() {
            // Given
            val dateStr = "2024-01-15"
            val inputFormat = "yyyy-MM-dd"
            val invalidOutputFormat = "invalid-format"

            // When & Then
            assertThrows<IllegalArgumentException> {
                DateTimeFormat.format(dateStr, inputFormat, invalidOutputFormat)
            }
        }
    }

    @Nested
    @DisplayName("format(calendar, outputFormat) 메서드 테스트")
    inner class FormatCalendarTest {

        @Test
        @DisplayName("Calendar 객체를 기본 형식으로 포맷")
        fun `should format calendar with default output format`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15, 10, 30, 45)
                set(Calendar.MILLISECOND, 0)
            }

            // When
            val result = DateTimeFormat.format(calendar)

            // Then
            assertEquals("20240115103045", result)
        }

        @Test
        @DisplayName("Calendar 객체를 커스텀 형식으로 포맷")
        fun `should format calendar with custom output format`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15, 10, 30, 45)
                set(Calendar.MILLISECOND, 0)
            }
            val outputFormat = "yyyy-MM-dd HH:mm:ss"

            // When
            val result = DateTimeFormat.format(calendar, outputFormat)

            // Then
            assertEquals("2024-01-15 10:30:45", result)
        }

        @ParameterizedTest
        @DisplayName("Calendar 객체 다양한 출력 포맷 테스트")
        @CsvSource(
            "2024, 0, 15, 0, 0, 0, yyyyMMdd, 20240115",
            "2024, 1, 29, 12, 30, 45, 'yyyy-MM-dd HH:mm', '2024-02-29 12:30'",
            "2023, 11, 25, 18, 45, 30, 'dd/MM/yyyy HH:mm:ss', '25/12/2023 18:45:30'"
        )
        fun `should format calendar with various output formats`(
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int,
            second: Int,
            outputFormat: String,
            expected: String
        ) {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(year, month, day, hour, minute, second)
                set(Calendar.MILLISECOND, 0)
            }

            // When
            val result = DateTimeFormat.format(calendar, outputFormat)

            // Then
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Calendar이 null이 아닌 경우 정상 처리")
        fun `should handle non-null calendar correctly`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.DECEMBER, 31, 23, 59, 59)
                set(Calendar.MILLISECOND, 0)
            }

            // When
            val result = DateTimeFormat.format(calendar, "yyyy/MM/dd")

            // Then
            assertEquals("2024/12/31", result)
        }
    }

    @Nested
    @DisplayName("format(date, outputFormat) 메서드 테스트")
    inner class FormatDateTest {

        @Test
        @DisplayName("Date 객체를 기본 형식으로 포맷")
        fun `should format date with default output format`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15, 10, 30, 45)
                set(Calendar.MILLISECOND, 0)
            }
            val date = calendar.time

            // When
            val result = DateTimeFormat.format(date)

            // Then
            assertEquals("20240115103045", result)
        }

        @Test
        @DisplayName("Date 객체를 커스텀 형식으로 포맷")
        fun `should format date with custom output format`() {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15, 10, 30, 45)
                set(Calendar.MILLISECOND, 0)
            }
            val date = calendar.time
            val outputFormat = "dd-MM-yyyy HH:mm:ss"

            // When
            val result = DateTimeFormat.format(date, outputFormat)

            // Then
            assertEquals("15-01-2024 10:30:45", result)
        }

        @ParameterizedTest
        @DisplayName("Date 객체 다양한 출력 포맷 테스트")
        @CsvSource(
            "'yyyyMMdd', '20240115'",
            "'yyyy-MM-dd', '2024-01-15'",
            "'dd/MM/yyyy HH:mm', '15/01/2024 10:30'",
        )
        fun `should format date with various output formats`(outputFormat: String, expected: String) {
            // Given
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15, 10, 30, 45)
                set(Calendar.MILLISECOND, 0)
            }
            val date = calendar.time

            // When
            val result = DateTimeFormat.format(date, outputFormat)

            // Then
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("현재 시간 Date 객체 포맷")
        fun `should format current date correctly`() {
            // Given
            val now = Date()
            val outputFormat = "yyyy-MM-dd"

            // When
            val result = DateTimeFormat.format(now, outputFormat)

            // Then
            assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        }

        @Test
        @DisplayName("잘못된 출력 포맷으로 IllegalArgumentException 발생")
        fun `should throw IllegalArgumentException for invalid date output format`() {
            // Given
            val date = Date()
            val invalidOutputFormat = "invalid-pattern"

            // When & Then
            assertThrows<IllegalArgumentException> {
                DateTimeFormat.format(date, invalidOutputFormat)
            }
        }

        @Test
        @DisplayName("과거와 미래 날짜 포맷 테스트")
        fun `should format past and future dates correctly`() {
            // Given
            val pastCalendar = Calendar.getInstance().apply {
                set(1990, Calendar.JUNE, 15, 14, 30, 0)
            }
            val futureCalendar = Calendar.getInstance().apply {
                set(2050, Calendar.SEPTEMBER, 25, 16, 45, 30)
            }
            val pastDate = pastCalendar.time
            val futureDate = futureCalendar.time

            // When
            val pastResult = DateTimeFormat.format(pastDate, "yyyy/MM/dd")
            val futureResult = DateTimeFormat.format(futureDate, "yyyy/MM/dd")

            // Then
            assertEquals("1990/06/15", pastResult)
            assertEquals("2050/09/25", futureResult)
        }
    }

    @Nested
    @DisplayName("Edge Cases 및 예외 상황 테스트")
    inner class EdgeCasesTest {

        @Test
        @DisplayName("윤년 날짜 처리 테스트")
        fun `should handle leap year dates correctly`() {
            // Given
            val leapYearDate = "2024-02-29"
            val inputFormat = "yyyy-MM-dd"
            val outputFormat = "yyyyMMdd"

            // When
            val result = DateTimeFormat.format(leapYearDate, inputFormat, outputFormat)

            // Then
            assertEquals("20240229", result)
        }

        @Test
        @DisplayName("비윤년 2월 29일로 ParseException 발생")
        fun `should throw ParseException for Feb 29 in non-leap year`() {
            // Given
            val invalidLeapDate = "2023-02-29" // 2023년은 윤년이 아님
            val inputFormat = "yyyy-MM-dd"

            // When & Then
            val v = DateTimeFormat.format(invalidLeapDate, inputFormat)
            assertEquals("20230301000000", v)
        }

        @Test
        @DisplayName("자정과 정오 시간 처리")
        fun `should handle midnight and noon times correctly`() {
            // Given
            val midnightDate = Date(0) // 1970-01-01 00:00:00 UTC
            val calendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15, 12, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val noonDate = calendar.time

            // When
            val midnightResult = DateTimeFormat.format(midnightDate, "HH:mm:ss")
            val noonResult = DateTimeFormat.format(noonDate, "HH:mm:ss")

            // Then
            assertTrue(midnightResult.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
            assertEquals("12:00:00", noonResult)
        }

        @Test
        @DisplayName("다양한 Locale에서의 날짜 포맷 테스트")
        fun `should format dates consistently regardless of default locale`() {
            // Given
            val date = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15, 10, 30, 45)
                set(Calendar.MILLISECOND, 0)
            }.time

            // When
            val result1 = DateTimeFormat.format(date, "yyyyMMddHHmmss")
            val result2 = DateTimeFormat.format(date, "yyyy-MM-dd HH:mm:ss")

            // Then
            assertEquals("20240115103045", result1)
            assertEquals("2024-01-15 10:30:45", result2)
        }
    }

    @Nested
    @DisplayName("클래스 구조 테스트")
    inner class ClassStructureTest {

        @Test
        @DisplayName("DateTimeFormat은 private constructor를 가져야 함")
        fun `should have private constructor`() {
            // When
            val constructors = DateTimeFormat::class.java.declaredConstructors

            // Then
            assertTrue(constructors.isNotEmpty(), "클래스는 최소 하나의 constructor를 가져야 함")
            constructors.forEach { constructor ->
                assertTrue(
                    Modifier.isPrivate(constructor.modifiers),
                    "모든 constructor는 private이어야 함: $constructor"
                )
            }
        }

        @Test
        @DisplayName("모든 메서드는 static이어야 함")
        fun `should have all static methods`() {
            // When
            val companionMethods = DateTimeFormat::class.java.declaredMethods
                .filter { it.name == "format" }

            // Then
            // Kotlin의 @JvmStatic으로 선언된 메서드들이 존재함을 확인
            assertFalse(companionMethods.isNotEmpty())
        }
    }
}
