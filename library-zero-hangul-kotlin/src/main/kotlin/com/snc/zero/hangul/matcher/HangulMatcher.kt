package com.snc.zero.hangul.matcher

import com.snc.zero.hangul.compose.HangulDecompose
import com.snc.zero.hangul.matcher.dto.HangulMatch
import com.snc.zero.logger.jvm.TLogging
import java.util.Locale

private val logger = TLogging.logger { }

/**
 * 한글 검색 (초성 검색 포함)
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class HangulMatcher {
    private var keyword: String = ""

    fun keyword(keyword: String): HangulMatcher {
        this.keyword = keyword.lowercase(Locale.getDefault())
            .replace("[^0-9a-zA-Z\\u1100-\\u11FFㄱ-ㅎㅏ-ㅣ\\u3165-\\u318E\\uD7B0-\\uD7FB\\uA960-\\uA97C가-힣\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D]+".toRegex(), "")
        return this
    }

    fun match(data: String, startIndex: Int = 0): HangulMatch {
        if (keyword.isEmpty()) {
            return HangulMatch(0, 0)
        }

        val target = data.lowercase(Locale.getDefault())
        val len = target.length - keyword.length + 1

        for (i in startIndex until len) {
            var k = 0
            var j = 0
            while (i + j < target.length) {
                val tc = target[i + j]
                val kc = keyword[k]

                val matched = matching(tc, kc)

                // 불일치 혹은 매칭 안된 상태에서 무시 문자인 경우
                if (-1 == matched || (0 == k && 0 == matched)) {
                    break
                }
                if (1 == matched) {
                    k++
                }
                if (k == keyword.length) {
                    return HangulMatch(i, j + 1)
                }
                j++
            }
        }
        return HangulMatch.EMPTY
    }

    private val ignore = "!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~` "

    private fun matching(t: Char, k: Char): Int {
        if (-1 != ignore.indexOf(t)) {
            return 0 // ignore
        }
        if (HangulDecompose.decompose(t).startsWith(HangulDecompose.decompose(k))) {
            return 1 // match
        }
        return -1 // not match
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            println("main $args")

            println("decompose = ${HangulDecompose.decompose('n')}")

            val menus = arrayOf(
                "사고보험금 신청",
                "사고보험금 내역조회",
                "사고보험금 서류보완",
                "일반보험금 신청",
                "일반보험금 지급내역",
                "휴대폰인증 NICE 평가",
                "휴대폰인증NICE 인증",
                "휴대폰인증 KCB 평가 (not NICE))",
                "현대해상은 전용선을 통해 VAN사(NICE)에 카드인증을 요청합니다."
            )

            val matcher = HangulMatcher()
            matcher.keyword("ㅈㅇㅅㅇㅌㅎv") // "ㅈ N", "ㅅㄱ")
            for (m in menus) {
                val match = matcher.match(m)
                if (HangulMatch.EMPTY != match) {
                    logger.info { "검색: $m" }
                }
            }
        }
    }
}