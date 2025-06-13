package com.snc.zero.hangul.compose

import com.snc.zero.hangul.charsets.HangulCharsets

class HangulCompose private constructor() {

    companion object {

        fun compose(cho: Char, jung: Char? = null, jong: Char? = null): String {
            if (null == jung) { return cho.toString(); }
            val iCho = HangulCharsets.COMPAT_CHOSEONG_COLLECTION.indexOf(cho)
            val iJung = HangulCharsets.COMPAT_JUNGSEONG_COLLECTION.indexOf(jung)
            var iJong = 0
            jong?.let {
                iJong = HangulCharsets.COMPAT_JONGSEONG_COLLECTION.indexOf(it)
            }

            // 입력받은 초성, 중성, 종성의 인덱스를 이용해 실제 유니코드 계산
            val unicode = HangulCharsets.HANGUL_SYLLABLES_BASE +
                (iCho * HangulCharsets.JUNGSEONG_COUNT * HangulCharsets.JONGSEONG_COUNT) +
                (iJung * HangulCharsets.JONGSEONG_COUNT) +
                iJong
            // 계산된 유니코드 값을 문자로 변환
            return unicode.toChar().toString()
        }
    }
}
