package com.snc.test.core.date

import com.snc.zero.core.date.DateTimeCompare
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.Calendar

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DateTimeCompareTest : BaseJUnit5Test() {

    @Test
    fun `날짜 비교 compare 테스트 1`() {
        // given
        val srcDate = "20170215"
        val dstDate = "20170309"
        // when
        val v1 = DateTimeCompare.compare(srcDate, "<=", dstDate)
        // then
        logger.debug { "Date 비교: $srcDate '<=' $dstDate => $v1" }
        assertEquals(v1, true)
    }

    @Test
    fun `날짜 비교 isBetweenDate 테스트 1`() {
        // given
        val srcDate = "20170215"
        val dstDate = "20170309"
        // when
        val v1 = DateTimeCompare.isBetweenDate(srcDate, "20170228", dstDate)
        // then
        logger.debug { "Date 비교: $srcDate '20170228' $dstDate => $v1" }
        assertEquals(v1, 0)
    }

    @Test
    fun `날짜 비교 betweenDays 테스트 1`() {
        // given
        val srcDate = "20170215"
        val dstDate = "20170309"
        // when
        val v1 = DateTimeCompare.betweenDays(srcDate, dstDate)
        // then
        logger.debug { "Date 남은 일자: $srcDate ~ $dstDate => $v1 일" }
        assertEquals(v1, 22)
    }

    @Nested
    @DisplayName("isBetweenDate 메서드 테스트")
    inner class IsBetweenDateTest {

        @Test
        @DisplayName("날짜가 범위 내에 있는 경우 0을 반환")
        fun `should return 0 when date is between start and end date`() {
            // Given
            val startDate = "2024-01-01"
            val date = "2024-01-15"
            val endDate = "2024-01-31"

            // When
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // Then
            assertEquals(0, result)
        }

        @Test
        @DisplayName("날짜가 시작 날짜보다 이전인 경우 -1을 반환")
        fun `should return -1 when date is before start date`() {
            // Given
            val startDate = "2024-01-15"
            val date = "2024-01-01"
            val endDate = "2024-01-31"

            // When
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // Then
            assertEquals(-1, result)
        }

        @Test
        @DisplayName("날짜가 종료 날짜보다 이후인 경우 1을 반환")
        fun `should return 1 when date is after end date`() {
            // Given
            val startDate = "2024-01-01"
            val date = "2024-02-15"
            val endDate = "2024-01-31"

            // When
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // Then
            assertEquals(1, result)
        }

        @Test
        @DisplayName("날짜가 시작 날짜와 같은 경우 0을 반환")
        fun `should return 0 when date equals start date`() {
            // Given
            val startDate = "2024-01-15"
            val date = "2024-01-15"
            val endDate = "2024-01-31"

            // When
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // Then
            assertEquals(0, result)
        }

        @Test
        @DisplayName("날짜가 종료 날짜와 같은 경우 0을 반환")
        fun `should return 0 when date equals end date`() {
            // Given
            val startDate = "2024-01-01"
            val date = "2024-01-31"
            val endDate = "2024-01-31"

            // When
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // Then
            assertEquals(0, result)
        }
    }

    @Nested
    @DisplayName("compare 메서드 테스트")
    inner class CompareTest {

        @ParameterizedTest
        @DisplayName("greater than or equal (>=) 연산자 테스트")
        @CsvSource(
            "2024-01-15, 2024-01-01, true",
            "2024-01-01, 2024-01-01, true",
            "2024-01-01, 2024-01-15, false"
        )
        fun `should handle greater than or equal operator correctly`(data1: String, data2: String, expected: Boolean) {
            // When
            val result = DateTimeCompare.compare(data1, ">=", data2)

            // Then
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @DisplayName("less than or equal (<=) 연산자 테스트")
        @CsvSource(
            "2024-01-01, 2024-01-15, true",
            "2024-01-01, 2024-01-01, true",
            "2024-01-15, 2024-01-01, false"
        )
        fun `should handle less than or equal operator correctly`(data1: String, data2: String, expected: Boolean) {
            // When
            val result = DateTimeCompare.compare(data1, "<=", data2)

            // Then
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @DisplayName("equal (==, =) 연산자 테스트")
        @CsvSource(
            "2024-01-01, 2024-01-01, true",
            "2024-01-01, 2024-01-15, false"
        )
        fun `should handle equal operator correctly`(data1: String, data2: String, expected: Boolean) {
            // When
            val resultDoubleEqual = DateTimeCompare.compare(data1, "==", data2)
            val resultSingleEqual = DateTimeCompare.compare(data1, "=", data2)

            // Then
            assertEquals(expected, resultDoubleEqual)
            assertEquals(expected, resultSingleEqual)
        }

        @ParameterizedTest
        @DisplayName("not equal (!=) 연산자 테스트")
        @CsvSource(
            "2024-01-01, 2024-01-15, true",
            "2024-01-01, 2024-01-01, false"
        )
        fun `should handle not equal operator correctly`(data1: String, data2: String, expected: Boolean) {
            // When
            val result = DateTimeCompare.compare(data1, "!=", data2)

            // Then
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @DisplayName("greater than (>) 연산자 테스트")
        @CsvSource(
            "2024-01-15, 2024-01-01, true",
            "2024-01-01, 2024-01-01, false",
            "2024-01-01, 2024-01-15, false"
        )
        fun `should handle greater than operator correctly`(data1: String, data2: String, expected: Boolean) {
            // When
            val result = DateTimeCompare.compare(data1, ">", data2)

            // Then
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @DisplayName("less than (<) 연산자 테스트")
        @CsvSource(
            "2024-01-01, 2024-01-15, true",
            "2024-01-01, 2024-01-01, false",
            "2024-01-15, 2024-01-01, false"
        )
        fun `should handle less than operator correctly`(data1: String, data2: String, expected: Boolean) {
            // When
            val result = DateTimeCompare.compare(data1, "<", data2)

            // Then
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @DisplayName("잘못된 연산자에 대해 false 반환")
        @ValueSource(strings = ["<>", "===", "unknown", ""])
        fun `should return false for invalid operators`(invalidOperator: String) {
            // When
            val result = DateTimeCompare.compare("2024-01-01", invalidOperator, "2024-01-01")

            // Then
            assertFalse(result)
        }
    }

    @Nested
    @DisplayName("betweenDays 메서드 테스트")
    inner class BetweenDaysTest {

        @Test
        @DisplayName("Calendar 객체를 사용한 날짜 차이 계산")
        fun `should calculate days between two Calendar objects correctly`() {
            // Given
            val startCalendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 1)
            }
            val endCalendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 31)
            }

            // When
            val result = DateTimeCompare.betweenDays(startCalendar, endCalendar)

            // Then
            assertEquals(30, result)
        }

        @Test
        @DisplayName("같은 날짜인 경우 0을 반환")
        fun `should return 0 for same dates`() {
            // Given
            val calendar1 = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15)
            }
            val calendar2 = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 15)
            }

            // When
            val result = DateTimeCompare.betweenDays(calendar1, calendar2)

            // Then
            assertEquals(0, result)
        }

        @Test
        @DisplayName("시작 날짜가 종료 날짜보다 이후인 경우 음수 반환")
        fun `should return negative number when start date is after end date`() {
            // Given
            val startCalendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 31)
            }
            val endCalendar = Calendar.getInstance().apply {
                set(2024, Calendar.JANUARY, 1)
            }

            // When
            val result = DateTimeCompare.betweenDays(startCalendar, endCalendar)

            // Then
            assertTrue(result < 0)
        }

        @Test
        @DisplayName("문자열을 사용한 날짜 차이 계산")
        fun `should calculate days between two date strings correctly`() {
            // Given
            val srcDate = "2024-01-01"
            val dstDate = "2024-01-11"

            // When
            val result = DateTimeCompare.betweenDays(srcDate, dstDate)

            // Then
            assertEquals(10, result)
        }

        @Test
        @DisplayName("월을 넘나드는 날짜 차이 계산")
        fun `should calculate days correctly across months`() {
            // Given
            val srcDate = "2024-01-25"
            val dstDate = "2024-02-05"

            // When
            val result = DateTimeCompare.betweenDays(srcDate, dstDate)

            // Then
            assertEquals(11, result)
        }

        @Test
        @DisplayName("년을 넘나드는 날짜 차이 계산")
        fun `should calculate days correctly across years`() {
            // Given
            val srcDate = "2023-12-25"
            val dstDate = "2024-01-05"

            // When
            val result = DateTimeCompare.betweenDays(srcDate, dstDate)

            // Then
            assertEquals(11, result)
        }

        @Test
        @DisplayName("윤년을 고려한 날짜 차이 계산")
        fun `should calculate days correctly considering leap year`() {
            // Given
            val srcDate = "2024-02-28" // 2024는 윤년
            val dstDate = "2024-03-01"

            // When
            val result = DateTimeCompare.betweenDays(srcDate, dstDate)

            // Then
            assertEquals(2, result) // 2월 29일이 포함되어 2일
        }
    }

    @Nested
    @DisplayName("Edge Cases 테스트")
    inner class EdgeCasesTest {

        @Test
        @DisplayName("빈 문자열 비교 테스트")
        fun `should handle empty strings in compare method`() {
            // When & Then
            assertTrue(DateTimeCompare.compare("", "==", ""))
            assertFalse(DateTimeCompare.compare("2024-01-01", "==", ""))
            assertFalse(DateTimeCompare.compare("", "==", "2024-01-01"))
        }

        @Test
        @DisplayName("null 값이 아닌 빈 문자열 처리")
        fun `should handle empty string operations`() {
            // When
            val result1 = DateTimeCompare.compare("", ">", "")
            val result2 = DateTimeCompare.compare("a", ">", "")
            val result3 = DateTimeCompare.compare("", ">", "a")

            // Then
            assertFalse(result1)
            assertTrue(result2)
            assertFalse(result3)
        }
    }
}
