package com.snc.zero.hangul.matcher

import com.snc.zero.hangul.compose.HangulDecompose
import com.snc.zero.hangul.matcher.dto.HangulMatchResult
import java.util.Locale

/**
 * 한글 검색 (초성 검색 포함)
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class HangulMatcher {
    private var keyword: String = ""

    /**
     * 정규식 설명
     *
     * 0-9: 숫자
     * a-zA-Z: 영문자
     * ㄱ-ㅎ: 한글 자음
     * ㅏ-ㅣ: 한글 모음
     * \\u3165-\\u318E: 한글 호환 자모
     * \\uD7B0-\\uD7FB: 한글 자모 확장-A
     * \\uA960-\\uA97C: 한글 자모 확장-B
     * 가-힣: 한글 완성형 글자
     * \\u00B7: 가운뎃점(·)
     * \\u119E: ᆞ (아래아)
     * \\u11A2: ᆢ (쌍아래아)
     * \\u2022: 불릿(•)
     * \\u2024: 원점(․)
     * \\u2025: 두 점 리더(..)
     * \\u2219: 불릿 연산자(∙)
     * \\u302E: 한글 겹받침 표시
     * \\u318D: 한글 자모 중 'ㆍ'(아래아)
     */
    private val allowedPattern = "0-9a-zA-Zㄱ-ㅎㅏ-ㅣ\\u3165-\\u318E\\uD7B0-\\uD7FB\\uA960-\\uA97C가-힣\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D"
    private val notAllowedRegex = "[^$allowedPattern]+".toRegex()

    fun keyword(keyword: String): HangulMatcher {
        this.keyword = keyword.lowercase(Locale.getDefault())
            .replace(notAllowedRegex, "")
        return this
    }

    fun match(data: String, startIndex: Int = 0): HangulMatchResult {
        if (keyword.isEmpty()) {
            return HangulMatchResult(0, 0)
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
                    return HangulMatchResult(i, j + 1)
                }
                j++
            }
        }
        return HangulMatchResult.EMPTY
    }

    private fun matching(t: Char, k: Char): Int {
        if (!isValidCharacter(t)) {
            return 0 // ignore
        }
        if (HangulDecompose.decompose(t).startsWith(HangulDecompose.decompose(k))) {
            return 1 // match
        }
        return -1 // not a match
    }

    fun isValidCharacter(char: Char): Boolean {
        return when (char) {
            in '0'..'9' -> true
            in 'a'..'z' -> true
            in 'A'..'Z' -> true
            in '가'..'힣' -> true
            else -> false
        }
    }
}
