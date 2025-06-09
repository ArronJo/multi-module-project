package com.snc.test.compose

import com.snc.zero.hangul.compose.HangulCompose
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HangulComposeTest : BaseJUnit5Test() {

    @Test
    fun `한글 조합`() {
        // given
        // when
        val v1 = HangulCompose.compose('ㅍ')
        val v2 = HangulCompose.compose('ㅍ', 'ㅡ')
        val v3 = HangulCompose.compose('ㅍ', 'ㅢ', 'ㄴ')
        val v4 = HangulCompose.compose('ㅍ', jung = 'ㅢ')
        val v5 = HangulCompose.compose('ㅍ', jong = 'ㄴ')
        // then
        logger.debug { "한글 조합 결과: $v1" }
        logger.debug { "한글 조합 결과: $v2" }
        logger.debug { "한글 조합 결과: $v3" }
        logger.debug { "한글 조합 결과: $v4" }
        logger.debug { "한글 조합 결과: $v5" }
    }
}
