package com.snc.test.extension.date

import com.snc.zero.extensions.date.parseDate
import com.snc.zero.extensions.date.parseDateTime
import com.snc.zero.extensions.format.formatDateTime
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DateExtTest : BaseJUnit5Test() {

    @Test
    fun `문자열로 된 날짜 파싱 1`() {
        // given
        val v1 = "20250604"
        val v2 = "250604"
        val v3 = "ab0604"
        // when
        val d1 = v1.parseDate()
        val d2 = v2.parseDate(pattern = "yyMMdd")
        val d3 = v3.parseDate(pattern = "yyMMdd")
        // then
        logger.debug { "parseDate1 : ${d1?.toString()}" }
        logger.debug { "parseDate2 : ${d2?.formatDateTime("yyyy-MM-dd HH:mm:ss")}" }
        logger.debug { "parseDate1 : $d3" }
    }

    @Test
    fun `should parse valid date string with default pattern`() {
        // given
        val input = "20250609" // yyyyMMdd

        // when
        val result = input.parseDateTime()

        // then
        val expected = SimpleDateFormat("yyyy-MM-dd").parse("2025-06-09")
        val actual = SimpleDateFormat("yyyy-MM-dd").format(result)
        val expectedStr = SimpleDateFormat("yyyy-MM-dd").format(expected)

        assertEquals(expectedStr, actual)
    }

    @Test
    fun `should parse date string with custom pattern`() {
        // given
        val input = "09-06-2025" // dd-MM-yyyy

        // when
        val result = input.parseDateTime("dd-MM-yyyy")

        // then
        val expected = SimpleDateFormat("yyyy-MM-dd").parse("2025-06-09")
        val actual = SimpleDateFormat("yyyy-MM-dd").format(result)
        val expectedStr = SimpleDateFormat("yyyy-MM-dd").format(expected)

        assertEquals(expectedStr, actual)
    }

    @Test
    fun `should throw exception for invalid pattern`() {
        val input = "20250609"
        assertThrows(java.time.format.DateTimeParseException::class.java) {
            input.parseDateTime("yyyy-MM-dd")
        }
    }

    @Test
    fun `should throw exception for malformed date`() {
        val input = "invalid-date"
        assertThrows(java.time.format.DateTimeParseException::class.java) {
            input.parseDateTime()
        }
    }
}
