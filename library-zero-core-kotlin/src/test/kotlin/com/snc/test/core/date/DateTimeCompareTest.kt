package com.snc.test.core.date

import com.snc.zero.core.date.DateTimeCompare
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DateTimeCompareTest : BaseJUnit5Test() {

    @Test
    fun `날짜 비교 compare 테스트 Exception`() {
        val srcDate = "20170215"
        val dstDate = "20170309"
        val v1 = DateTimeCompare.compare(srcDate, "!", dstDate)
        assertFalse(v1)
    }

    @Test
    fun `날짜 비교 compare 테스트 1`() {
        // given
        val srcDate = "20170215"
        val dstDate = "20170309"
        // when
        val v1 = DateTimeCompare.compare(srcDate, "<=", dstDate)
        // then
        logger.debug { "Date 비교: $srcDate '<=' $dstDate => $v1" }
        assertEquals(true, v1)
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
        assertEquals(0, v1)
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
        assertEquals(22, v1)
    }

    @Test
    fun `compare string dates with various operators`() {
        assertTrue(DateTimeCompare.compare("2025-06-13", "<", "2025-06-14"))
        assertTrue(DateTimeCompare.compare("2025-06-13", "<=", "2025-06-13"))
        assertTrue(DateTimeCompare.compare("2025-06-13", "==", "2025-06-13"))
        assertTrue(DateTimeCompare.compare("2025-06-13", "=", "2025-06-13"))
        assertTrue(DateTimeCompare.compare("2025-06-13", "!=", "2025-06-14"))
        assertTrue(DateTimeCompare.compare("2025-06-14", ">", "2025-06-13"))
        assertTrue(DateTimeCompare.compare("2025-06-14", ">=", "2025-06-14"))

        assertFalse(DateTimeCompare.compare("2025-06-13", ">", "2025-06-14"))
        assertFalse(DateTimeCompare.compare("2025-06-13", "<", "2025-06-12"))
        assertFalse(DateTimeCompare.compare("2025-06-13", "!=", "2025-06-13"))
        assertFalse(DateTimeCompare.compare("2025-06-13", "==", "2025-06-14"))
    }

    @Test
    fun `isBetweenDate returns correct code`() {
        assertEquals(0, DateTimeCompare.isBetweenDate("2025-06-10", "2025-06-13", "2025-06-15"))
        assertEquals(-1, DateTimeCompare.isBetweenDate("2025-06-10", "2025-06-09", "2025-06-15"))
        assertEquals(1, DateTimeCompare.isBetweenDate("2025-06-10", "2025-06-16", "2025-06-15"))
        assertEquals(0, DateTimeCompare.isBetweenDate("2025-06-13", "2025-06-13", "2025-06-13"))
    }

    @Test
    fun `betweenDays Calendar calculates days accurately`() {
        val cal1 = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 10, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val cal2 = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 15, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val days = DateTimeCompare.betweenDays(cal1, cal2)
        assertEquals(5, days)
    }

    @Test
    fun `betweenDays String parses and calculates correctly`() {
        val days = DateTimeCompare.betweenDays("2025-06-10", "2025-06-15")
        assertEquals(5, days)
    }

    @Test
    fun `betweenDays negative difference`() {
        val days = DateTimeCompare.betweenDays("2025-06-15", "2025-06-10")
        assertEquals(-5, days)
    }

    @Test
    fun `betweenDays same day should be 0`() {
        val days = DateTimeCompare.betweenDays("2025-06-13", "2025-06-13")
        assertEquals(0, days)
    }

    private val earlier = "2024-01-01"
    private val same = "2024-01-01"
    private val later = "2025-01-01"

    @Test
    fun `valid operators should compare correctly`() {
        assertTrue(DateTimeCompare.compare(later, ">", earlier))
        assertFalse(DateTimeCompare.compare(earlier, ">", later))

        assertTrue(DateTimeCompare.compare(earlier, "<", later))
        assertFalse(DateTimeCompare.compare(later, "<", earlier))

        assertTrue(DateTimeCompare.compare(earlier, ">=", earlier))
        assertTrue(DateTimeCompare.compare(later, ">=", earlier))
        assertFalse(DateTimeCompare.compare(earlier, ">=", later))

        assertTrue(DateTimeCompare.compare(earlier, "<=", later))
        assertTrue(DateTimeCompare.compare(earlier, "<=", same))
        assertFalse(DateTimeCompare.compare(later, "<=", earlier))

        assertTrue(DateTimeCompare.compare(earlier, "==", same))
        assertTrue(DateTimeCompare.compare(earlier, "=", same))
        assertFalse(DateTimeCompare.compare(earlier, "==", later))

        assertTrue(DateTimeCompare.compare(earlier, "!=", later))
        assertFalse(DateTimeCompare.compare(earlier, "!=", same))
    }

    @Test
    fun `invalid operators should return false`() {
        val invalidOperators = listOf(
            "=>", "=<", "!==", "><", "<>", "=>=", "=<=", "===", "== =",
            "!!", "!>", "!<", "><", "~=", "equal", "lte"
        )

        for (op in invalidOperators) {
            val result = DateTimeCompare.compare(earlier, op, later)
            assertFalse(result, "Expected false for invalid operator: '$op'")
        }
    }

    @Test
    fun `compare operator - greater than`() {
        assertTrue(DateTimeCompare.compare(later, ">", earlier))
        assertFalse(DateTimeCompare.compare(earlier, ">", later))
        assertFalse(DateTimeCompare.compare(same, ">", same))
    }

    @Test
    fun `compare operator - greater than or equal`() {
        assertTrue(DateTimeCompare.compare(later, ">=", earlier))
        assertTrue(DateTimeCompare.compare(same, ">=", same))
        assertFalse(DateTimeCompare.compare(earlier, ">=", later))
    }

    @Test
    fun `compare operator - less than`() {
        assertTrue(DateTimeCompare.compare(earlier, "<", later))
        assertFalse(DateTimeCompare.compare(later, "<", earlier))
        assertFalse(DateTimeCompare.compare(same, "<", same))
    }

    @Test
    fun `compare operator - less than or equal`() {
        assertTrue(DateTimeCompare.compare(earlier, "<=", later))
        assertTrue(DateTimeCompare.compare(same, "<=", same))
        assertFalse(DateTimeCompare.compare(later, "<=", earlier))
    }

    @Test
    fun `compare operator - equals`() {
        assertTrue(DateTimeCompare.compare(same, "==", same))
        assertTrue(DateTimeCompare.compare(same, "=", same)) // alias
        assertFalse(DateTimeCompare.compare(earlier, "==", later))
        assertFalse(DateTimeCompare.compare(earlier, "=", later))
    }

    @Test
    fun `compare operator - not equals`() {
        assertTrue(DateTimeCompare.compare(earlier, "!=", later))
        assertFalse(DateTimeCompare.compare(same, "!=", same))
    }

    @Test
    fun `compare operator - invalid signs return false`() {
        val invalidOps = listOf("", "=", "!==", "><", "<>", "===", "=", ">>>", "~=", "??", "=>", "=<")
        for (op in invalidOps) {
            assertFalse(DateTimeCompare.compare(earlier, op, later), "Expected false for operator: $op")
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
        fun `betweenDays should return 1 when times are just under 24 hours apart`() {
            val start = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 15, 23, 0, 0) // 2025-06-15 23:00
                set(Calendar.MILLISECOND, 0)
            }
            val end = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 16, 22, 0, 0) // 2025-06-16 22:00
                set(Calendar.MILLISECOND, 0)
            }

            val result = DateTimeCompare.betweenDays(start, end)
            assertEquals(1, result)
        }

        @Test
        fun `betweenDays should return 0 when difference is less than 1 day`() {
            val start = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 15, 12, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val end = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 15, 23, 59, 59)
                set(Calendar.MILLISECOND, 0)
            }

            val result = DateTimeCompare.betweenDays(start, end)
            assertEquals(0, result)
        }

        @Test
        fun `betweenDays should return 2 when milliseconds difference overestimates by a day`() {
            val start = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 15, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val end = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 17, 0, 0, 1) // Slightly over 2 days
                set(Calendar.MILLISECOND, 1)
            }

            val result = DateTimeCompare.betweenDays(start, end)
            assertEquals(2, result)
        }

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

    @Nested
    @DisplayName("compare 메서드 테스트 - 모든 JaCoCo 조건 커버리지")
    inner class CompareMethodTest {

        @Nested
        @DisplayName(">= 연산자 테스트")
        inner class GreaterThanOrEqualTest {

            @Test
            @DisplayName("data1이 data2보다 큰 경우 - true 반환")
            fun `should return true when data1 is greater than data2`() {
                // given
                val data1 = "2024-01-02"
                val data2 = "2024-01-01"
                val sign = ">="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2와 같은 경우 - true 반환")
            fun `should return true when data1 equals data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-01"
                val sign = ">="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2보다 작은 경우 - false 반환")
            fun `should return false when data1 is less than data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = ">="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("<= 연산자 테스트")
        inner class LessThanOrEqualTest {

            @Test
            @DisplayName("data1이 data2보다 작은 경우 - true 반환")
            fun `should return true when data1 is less than data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = "<="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2와 같은 경우 - true 반환")
            fun `should return true when data1 equals data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-01"
                val sign = "<="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2보다 큰 경우 - false 반환")
            fun `should return false when data1 is greater than data2`() {
                // given
                val data1 = "2024-01-02"
                val data2 = "2024-01-01"
                val sign = "<="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("== 연산자 테스트")
        inner class EqualsTest {

            @Test
            @DisplayName("data1이 data2와 같은 경우 - true 반환")
            fun `should return true when data1 equals data2 with double equals`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-01"
                val sign = "=="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2와 다른 경우 - false 반환")
            fun `should return false when data1 not equals data2 with double equals`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = "=="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("= 연산자 테스트")
        inner class SingleEqualsTest {

            @Test
            @DisplayName("data1이 data2와 같은 경우 - true 반환")
            fun `should return true when data1 equals data2 with single equals`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-01"
                val sign = "="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2와 다른 경우 - false 반환")
            fun `should return false when data1 not equals data2 with single equals`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = "="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("!= 연산자 테스트")
        inner class NotEqualsTest {

            @Test
            @DisplayName("data1이 data2와 다른 경우 - true 반환")
            fun `should return true when data1 not equals data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = "!="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2와 같은 경우 - false 반환")
            fun `should return false when data1 equals data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-01"
                val sign = "!="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("> 연산자 테스트")
        inner class GreaterThanTest {

            @Test
            @DisplayName("data1이 data2보다 큰 경우 - true 반환")
            fun `should return true when data1 is greater than data2`() {
                // given
                val data1 = "2024-01-02"
                val data2 = "2024-01-01"
                val sign = ">"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2와 같은 경우 - false 반환")
            fun `should return false when data1 equals data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-01"
                val sign = ">"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }

            @Test
            @DisplayName("data1이 data2보다 작은 경우 - false 반환")
            fun `should return false when data1 is less than data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = ">"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("< 연산자 테스트")
        inner class LessThanTest {

            @Test
            @DisplayName("data1이 data2보다 작은 경우 - true 반환")
            fun `should return true when data1 is less than data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = "<"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("data1이 data2와 같은 경우 - false 반환")
            fun `should return false when data1 equals data2`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-01"
                val sign = "<"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }

            @Test
            @DisplayName("data1이 data2보다 큰 경우 - false 반환")
            fun `should return false when data1 is greater than data2`() {
                // given
                val data1 = "2024-01-02"
                val data2 = "2024-01-01"
                val sign = "<"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("기타 연산자 테스트")
        inner class DefaultCaseTest {

            @Test
            @DisplayName("지원하지 않는 연산자인 경우 - false 반환")
            fun `should return false for unsupported operator`() {
                // given
                val data1 = "2024-01-01"
                val data2 = "2024-01-02"
                val sign = "unknown"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertFalse(result)
            }
        }

        @Nested
        @DisplayName("경계값 테스트")
        inner class BoundaryValueTest {

            @Test
            @DisplayName("빈 문자열 비교 테스트")
            fun `should handle empty strings`() {
                // given
                val data1 = ""
                val data2 = ""
                val sign = "=="

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("null이 아닌 문자열과 빈 문자열 비교")
            fun `should handle non empty and empty string comparison`() {
                // given
                val data1 = "2024-01-01"
                val data2 = ""
                val sign = ">"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }

            @Test
            @DisplayName("특수 문자가 포함된 문자열 비교")
            fun `should handle special characters in strings`() {
                // given
                val data1 = "2024-01-01T00:00:00"
                val data2 = "2024-01-01T00:00:01"
                val sign = "<"

                // when
                val result = DateTimeCompare.compare(data1, sign, data2)

                // then
                assertTrue(result)
            }
        }
    }

    @Nested
    @DisplayName("isBetweenDate 메서드 테스트")
    inner class IsBetweenDateTest {

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

        @Test
        @DisplayName("날짜가 범위 내에 있는 경우 - 0 반환")
        fun `should return 0 when date is within range`() {
            // given
            val startDate = "2024-01-01"
            val date = "2024-01-05"
            val endDate = "2024-01-10"

            // when
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // then
            assertEquals(0, result)
        }

        @Test
        @DisplayName("날짜가 시작일보다 이전인 경우 - -1 반환")
        fun `should return -1 when date is before start date`() {
            // given
            val startDate = "2024-01-05"
            val date = "2024-01-01"
            val endDate = "2024-01-10"

            // when
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // then
            assertEquals(-1, result)
        }

        @Test
        @DisplayName("날짜가 종료일보다 이후인 경우 - 1 반환")
        fun `should return 1 when date is after end date`() {
            // given
            val startDate = "2024-01-01"
            val date = "2024-01-15"
            val endDate = "2024-01-10"

            // when
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // then
            assertEquals(1, result)
        }

        @Test
        @DisplayName("날짜가 시작일과 같은 경우 - 0 반환")
        fun `should return 0 when date equals start date`() {
            // given
            val startDate = "2024-01-01"
            val date = "2024-01-01"
            val endDate = "2024-01-10"

            // when
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // then
            assertEquals(0, result)
        }

        @Test
        @DisplayName("날짜가 종료일과 같은 경우 - 0 반환")
        fun `should return 0 when date equals end date`() {
            // given
            val startDate = "2024-01-01"
            val date = "2024-01-10"
            val endDate = "2024-01-10"

            // when
            val result = DateTimeCompare.isBetweenDate(startDate, date, endDate)

            // then
            assertEquals(0, result)
        }
    }
}
