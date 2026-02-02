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

    // matching 함수 위치(기존 86줄)에 추가
    private fun extractChosung(char: Char): String {
        if (char !in '가'..'힣') {
            return char.toString()
        }

        val chosung = listOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
            'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        val code = char.code - 0xAC00
        val chosungIndex = code / (21 * 28)

        return chosung[chosungIndex].toString()
    }

    private fun isChosung(char: Char): Boolean {
        return char in 'ㄱ'..'ㅎ'
    }

    //++ 26.1.30 검색 기능 강화
    // match 함수 수정
    fun match(data: String, startIndex: Int = 0): HangulMatchResult {
        if (keyword.isEmpty()) {
            return HangulMatchResult(0, 0)
        }

        val target = data.lowercase(Locale.getDefault())

        // keyword에 초성이 포함되어 있는지 확인
        val hasChosung = keyword.any { isChosung(it) }

        return if (hasChosung) {
            matchByChosung(target, startIndex)
        } else {
            matchByJamo(target, startIndex)
        }
    }

    // 자모 검색 함수
    private fun matchByJamo(target: String, startIndex: Int): HangulMatchResult {
        // keyword에서 허용되지 않은 문자 제거 후 자모 분리
        val keywordDecomposed = keyword.replace(notAllowedRegex, "")
            .map { HangulDecompose.decompose(it) }
            .joinToString("")

        if (keywordDecomposed.isEmpty()) {
            return HangulMatchResult(0, 0)
        }

        for (searchPos in startIndex until target.length) {
            var decomposedStr = ""
            var actualStartPos = -1 // 실제 매칭 시작 위치

            for (charCount in 0 until (target.length - searchPos)) {
                val currentChar = target[searchPos + charCount]

                // 허용된 문자만 자모 분리하여 추가
                if (!notAllowedRegex.matches(currentChar.toString())) {
                    // 첫 번째 허용된 문자 위치 기록
                    if (actualStartPos == -1) {
                        actualStartPos = searchPos + charCount
                    }
                    decomposedStr += HangulDecompose.decompose(currentChar).replace(" ", "")
                }

                if (decomposedStr.startsWith(keywordDecomposed)) {
                    val matchLength = (searchPos + charCount + 1) - actualStartPos
                    return HangulMatchResult(actualStartPos, matchLength)
                }

                if (!keywordDecomposed.startsWith(decomposedStr)) {
                    break
                }
            }
        }

        return HangulMatchResult.EMPTY
    }

    // 초성 검색 함수
    private fun matchByChosung(target: String, startIndex: Int): HangulMatchResult {
        // keyword에서 허용되지 않은 문자 제거 후 초성 추출
        val keywordChosung = keyword.replace(notAllowedRegex, "")
            .map { if (isChosung(it)) it.toString() else extractChosung(it) }
            .joinToString("")

        if (keywordChosung.isEmpty()) {
            return HangulMatchResult(0, 0)
        }

        for (searchPos in startIndex until target.length) {
            var chosungStr = ""

            for (charCount in 0 until (target.length - searchPos)) {
                val currentChar = target[searchPos + charCount]

                // 한글만 초성 추출, 그 외 문자는 건너뛰기
                if (currentChar in '가'..'힣') {
                    chosungStr += extractChosung(currentChar)
                }

                if (chosungStr.startsWith(keywordChosung)) {
                    return HangulMatchResult(searchPos, charCount + 1)
                }

                if (!keywordChosung.startsWith(chosungStr)) {
                    break
                }
            }
        }

        return HangulMatchResult.EMPTY
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
