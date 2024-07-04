package com.snc.test.core.extensions.format

import com.snc.zero.core.extensions.format.formatNumericalKoreanMoney
import com.snc.zero.core.extensions.format.formatWordKoreanMoney
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class MoneyFormatterExtTest : BaseJUnit5Test() {

    @Test
    fun `한글 금액 표기법 테스트 1`() {
        // given
        val data = 25000000L
        // when
        val v1 = data.formatNumericalKoreanMoney("만원")
        // then
        logger.debug { "한글 금액 1: $v1" }
        assertEquals("2,500만원", v1)
    }

    @Test
    fun `한글 금액 표기법 테스트 2`() {
        // given
        val data = 25000000L
        // when
        val v2 = data.formatWordKoreanMoney()
        // then
        logger.debug { "한글 금액 2: $v2" }
        assertEquals("이천오백만", v2)
    }

    @Test
    fun `한글 금액 표기법 테스트 3`() {
        // given
        val data = 25000000L
        // when
        val v1 = data.formatNumericalKoreanMoney()
        // then
        logger.debug { "한글 금액 3: $v1" }
        assertEquals("2,500만", v1)
    }

    @Test
    fun `한글 금액 표기법 테스트 all`() {
        val data = 807L
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney()}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("경")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("천조")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("백조")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("십조")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("조")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("천억")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("백억")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("십억")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("억")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("천만")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("천만원")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("백만")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("백만원")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("십만")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("십만원")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("만")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("만원")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("천")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("천원")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("백")}" }
        logger.debug { "한글 금액 : ${data.formatNumericalKoreanMoney("10000")}" }
    }
}