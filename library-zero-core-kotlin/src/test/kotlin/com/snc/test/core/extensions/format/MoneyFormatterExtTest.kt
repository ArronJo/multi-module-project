package com.snc.test.core.extensions.format

import com.snc.zero.core.extensions.format.formatKoreanMoney
import com.snc.zero.core.extensions.format.formatRealKoreanMoney
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
        val v1 = data.formatKoreanMoney("만원")
        // then
        logger.debug { "한글 금액 1: $v1" }
        assertEquals(v1, "2,500만원")
    }

    @Test
    fun `한글 금액 표기법 테스트 2`() {
        // given
        val data = 25000000L
        // when
        val v2 = data.formatRealKoreanMoney()
        // then
        logger.debug { "한글 금액 2: $v2" }
        assertEquals(v2, "이천오백만")
    }
}