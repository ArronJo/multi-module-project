package com.snc.test.core.extensions.text

import com.snc.zero.core.extensions.text.queryStringToMap
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class QueryStringExtTest : BaseJUnit5Test() {

    @Test
    fun `parsing QueryString`() {
        // given
        val data = "https://www.fsc.go.kr/no010101/81929?srchCtgry=1&curPage=2&srchKey=3&srchText=4&srchBeginDt=5&srchEndDt="
        // when
        val v1 = data.queryStringToMap()
        // then
        logger.debug { "결과: $v1" }
        //assertEquals(v1, false)
    }
}