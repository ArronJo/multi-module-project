package com.snc.test.extension.date

import com.snc.zero.extension.date.parseDate
import com.snc.zero.extension.format.formatDateTime
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

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
}
