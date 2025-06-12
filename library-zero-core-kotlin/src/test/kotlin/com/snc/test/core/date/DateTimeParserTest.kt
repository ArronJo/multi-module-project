package com.snc.test.core.date

import com.snc.zero.core.date.DateTimeParser
import com.snc.zero.extension.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.Calendar

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
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
        val v3 = DateTimeParser.today2(format = "yyyyMMdd")
        val v4 = DateTimeParser.tomorrow()
        // then
        logger.debug { "날짜 today: $v1" }
        logger.debug { "날짜 today: $v2" }
        logger.debug { "날짜 today: $v3" }
        logger.debug { "날짜 tomorrow: $v4" }
        //assertEquals(v1, "20170215")
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
}
