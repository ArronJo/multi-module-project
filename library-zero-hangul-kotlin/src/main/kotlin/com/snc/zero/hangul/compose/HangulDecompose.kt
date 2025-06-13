package com.snc.zero.hangul.compose

import com.snc.zero.hangul.charsets.HangulCharsets
import java.util.Arrays

class HangulDecompose private constructor() {

    companion object {

        fun decompose(c: Char): String {
            if (isSyllable(c)) {
                return decomposeCompat(c).joinToString(separator = "")
            }
            if (isCompatChoseong(c)) {
                return c.toString()
            }
            if (isChoseong(c)) {
                return choseongToCompatChoseong(c).toString()
            }
            return c.toString()
        }

        // 한글 자모 영역 초성을 한글 호환 자모 영역 문자로 변환
        private fun choseongToCompatChoseong(c: Char): Char {
            // Hangul Jamo               | 한글 자모 영역     | 0x1100-0x11F9
            // Hangul Compatibility Jamo | 한글 호환 자모 영역 | 0x3130-0x318E
            // Hangul Syllables          | 한글 영역         | 0xAC00-0xD7A3
            // Halfwidth Jamo            | 반각 자모         | 0xFF00-0xFFEF
            return HangulCharsets.COMPAT_CHOSEONG_COLLECTION[c.code - 0x1100]
        }

        // 한글 영역 (0xAC00-0xD7A3) 문자인가?
        private fun isSyllable(c: Char): Boolean {
            return c.code in HangulCharsets.HANGUL_SYLLABLES_BASE until HangulCharsets.HANGUL_SYLLABLES_END
        }

        // 한글 자모 영역 (0x1100-0x11F9) 문자인가?
        private fun isChoseong(c: Char): Boolean {
            return c.code in 0x1100..0x1112
        }

        private fun decomposeCompat(syllable: Char): Array<String> {
            val cho = getCompatChoseong(syllable).toString()
            val jung = getCompatJungseong(syllable).toString()
            val jong = getCompatJongseong(syllable).toString()
            return if (jong.isBlank()) arrayOf(cho, jung) else arrayOf(cho, jung, jong)
        }

        // 한글 호환 자모 영역 (0x3130-0x318E) 문자인가?
        private fun isCompatChoseong(c: Char): Boolean {
            val index = Arrays.binarySearch(HangulCharsets.COMPAT_CHOSEONG_COLLECTION, c)
            return index >= 0
        }

        // 초성 문자 추출
        fun getCompatChoseong(syllable: Char): Char {
            val sylIndex = syllable.code - HangulCharsets.HANGUL_SYLLABLES_BASE
            val index = sylIndex / (HangulCharsets.JUNGSEONG_COUNT * HangulCharsets.JONGSEONG_COUNT)
            return HangulCharsets.COMPAT_CHOSEONG_COLLECTION[index]
        }

        // 중성 문자 추출
        fun getCompatJungseong(syllable: Char): Char {
            val sylIndex = syllable.code - HangulCharsets.HANGUL_SYLLABLES_BASE
            val index = sylIndex % (HangulCharsets.JUNGSEONG_COUNT * HangulCharsets.JONGSEONG_COUNT) / HangulCharsets.JONGSEONG_COUNT
            return HangulCharsets.COMPAT_JUNGSEONG_COLLECTION[index]
        }

        // 종성 문자 추출
        fun getCompatJongseong(syllable: Char): Char {
            val sylIndex = syllable.code - HangulCharsets.HANGUL_SYLLABLES_BASE
            val index = sylIndex % HangulCharsets.JONGSEONG_COUNT
            return HangulCharsets.COMPAT_JONGSEONG_COLLECTION[index]
        }
    }
}
