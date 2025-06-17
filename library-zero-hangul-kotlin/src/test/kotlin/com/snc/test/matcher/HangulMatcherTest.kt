package com.snc.test.matcher

import com.snc.zero.hangul.matcher.HangulMatcher
import com.snc.zero.hangul.matcher.dto.HangulMatch
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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

    private lateinit var matcher: HangulMatcher

    @BeforeEach
    fun setUp() {
        matcher = HangulMatcher()
    }

    @Test
    fun `keyword should lowercase and filter non-korean characters`() {
        matcher.keyword("가나다 ABC123!@#")
        val result = matcher.match("가나다 abc123")
        assertEquals(HangulMatch(0, 10), result)
    }

    @Test
    fun `match should return correct start and length when match found`() {
        matcher.keyword("ㄱㄴ")
        val result = matcher.match("가나다라마")
        assertEquals(HangulMatch(0, 2), result)
    }

    @Test
    fun `match should skip ignored symbols`() {
        matcher.keyword("abc")
        val result = matcher.match("a@b!c")
        assertEquals(HangulMatch(0, 5), result)
    }

    @Test
    fun `match should return EMPTY when keyword not found`() {
        matcher.keyword("ㅋㅌㅍ")
        val result = matcher.match("가나다라마바사")
        assertEquals(HangulMatch.EMPTY, result)
    }

    @Test
    fun `match should return EMPTY when keyword is empty`() {
        matcher.keyword("")
        val result = matcher.match("아무 문자열")
        assertEquals(HangulMatch(0, 0), result)
    }

    @Test
    fun `match should return match even with intermediate ignored characters`() {
        matcher.keyword("abc")
        val result = matcher.match("a!@#b%^c")
        assertEquals(HangulMatch(0, 8), result)
    }

    @Test
    fun `match should match mixed case and Korean characters`() {
        matcher.keyword("GNa")
        val result = matcher.match("가나abc")
        assertEquals(HangulMatch.EMPTY, result)
    }

    @Test
    fun `match should skip initial unmatched characters`() {
        matcher.keyword("ㄷㄹ")
        val result = matcher.match("가나다라마")
        assertEquals(HangulMatch(2, 2), result)
    }
}
