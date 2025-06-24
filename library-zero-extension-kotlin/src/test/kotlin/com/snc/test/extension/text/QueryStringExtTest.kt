package com.snc.test.extension.text

import com.snc.zero.extensions.text.getQueryStringValue
import com.snc.zero.extensions.text.queryStringToMap
import com.snc.zero.extensions.text.toQueryString
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

class QueryStringExtTest : BaseJUnit5Test() {

    private lateinit var data: MutableMap<String, String>

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        data = mutableMapOf()
        data["a"] = "1"
        data["b"] = "2"
    }

    @Test
    fun `to QueryString`() {
        // given
        // when
        val v1 = data.toQueryString()
        // then
        logger.debug { "결과: $v1" }
        assertEquals("a=1&b=2", v1)
    }

    @Test
    fun `get QueryString`() {
        // given
        val data = "a=1&b=2"
        // when
        val v1 = data.getQueryStringValue("b")
        // then
        logger.debug { "결과: $v1" }
        assertEquals("2", v1)
    }

    @Test
    fun `parsing QueryString`() {
        // given
        val data = "https://www.fsc.go.kr/no010101/81929?srchCtgry=1&curPage=2&srchKey=3&srchText=4&srchBeginDt=5&srchEndDt="
        // when
        val v1 = data.queryStringToMap()
        // then
        logger.debug { "결과: $v1" }
        assertEquals("{srchCtgry=1, curPage=2, srchKey=3, srchEndDt=, srchBeginDt=5, srchText=4}", v1.toString())
    }
}
