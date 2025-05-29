package com.snc.test.matcher

import com.snc.zero.hangul.matcher.HangulMatcher
import com.snc.zero.hangul.matcher.dto.HangulMatch
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HangulMatcherTest : BaseJUnit5Test() {

    @Test
    fun `한글 초성 검색`() {
        // given
        val menus = arrayOf(
            "사고보험금 신청",
            "사고보험금 내역조회",
            "사고보험금 서류보완",
            "일반보험금 신청",
            "일반보험금 지급내역",
            "휴대폰인증 NICE 평가",
            "휴대폰인증NICE 인증",
            "휴대폰인증 KCB 평가 (not NICE))",
            "당사는 전용선을 통해 VAN사(NICE)에 카드인증을 요청합니다."
        )
        // when
        val keyword = "ㅈㅇㅅㅇㅌㅎv" // "ㅈ N", "ㅅㄱ"
        val matcher = HangulMatcher()
        matcher.keyword(keyword)
        for (m in menus) {
            val match = matcher.match(m)
            // then
            if (HangulMatch.EMPTY != match) {
                logger.debug { "한글 초성 검색 ($keyword) -> 결과: $m" }
            }
        }
    }
}
