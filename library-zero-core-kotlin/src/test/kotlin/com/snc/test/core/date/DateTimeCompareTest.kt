package com.snc.test.core.date

import com.snc.zero.core.date.DateTimeCompare
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

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
}
