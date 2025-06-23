package com.snc.zero.hangul.matcher.dto

/**
 * 한글 검색 결과 (초성 검색 포함)
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
data class HangulMatchResult(val start: Int, val length: Int) {

    companion object {
        val EMPTY = HangulMatchResult(-1, 0)
    }
}
