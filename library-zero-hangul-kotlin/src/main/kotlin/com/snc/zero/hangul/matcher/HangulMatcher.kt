package com.snc.zero.hangul.matcher

import com.snc.zero.hangul.compose.HangulDecompose
import com.snc.zero.hangul.matcher.dto.HangulMatch
import java.util.Locale

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
            //.replace("[^0-9a-zA-Z\\u1100-\\u11FFㄱ-ㅎㅏ-ㅣ\\u3165-\\u318E\\uD7B0-\\uD7FB\\uA960-\\uA97C가-힣\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D]+".toRegex(), "")
            .replace("[^0-9a-zA-Z\\u1100-\\u11FFㅏ-ㅣ\\u3165-\\u318E\\uD7B0-\\uD7FB\\uA960-\\uA97C가-힣\\u00B7\\u2022\\u2024\\u2025\\u2219\\u302E]+".toRegex(), "")
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
}
