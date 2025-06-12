package com.snc.test.core.regex

import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_EMAIL
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_HANGUL
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_INPUT_CHARS
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PHONE_NUM
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PW_ENG_NUM_SPC
import com.snc.zero.core.regex.RegularExpr.Companion.PATTERN_PW_ENG_UC_LC_NUM_SPC
import com.snc.zero.core.regex.RegularExpr.Companion.find
import com.snc.zero.core.regex.RegularExpr.Companion.matches
import com.snc.zero.core.util.StringUtil
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

class RegularExprTest : BaseJUnit5Test() {

    @Test
    fun testRegularExpr() {
        val arr: Array<String> = find("[a-zA-Z]+", "/api/envelopes")
        println("API: ${StringUtil.toString(arr)}")
        //println("API: ${arrayListOf("1","2").joinToString(prefix = "[", separator = "|", postfix = "]")}")
        //println("API: ${listOf("1")}")
        println()

        println("PATTERN_DIGIT: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_LETTER_LOWER: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_LETTER_UPPER: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")

        println("PATTERN_INPUT_HANGUL: ${StringUtil.toString(find(PATTERN_INPUT_CHARS, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_HANGUL: ${find(PATTERN_HANGUL, "ㄱㅎㅏ123ㅣ가").joinToString(separator = "")}")
        println("PATTERN_PHONE_NUM: ${StringUtil.toString(find(PATTERN_PHONE_NUM, "ㄱㅎㅏ123301012345678ㅣ가"))}")
        println("PATTERN_EMAIL ${StringUtil.toString(find(PATTERN_EMAIL, "ㄱㅎㅏ12aas@hanwha.com3ㅣ가"))}")
        println()

        println("PATTERN_PWD_ENG_UC_LC_NUM_SPC_ALL: ${matches(PATTERN_PW_ENG_UC_LC_NUM_SPC, "Ab1@Ab1@11")}")
        println("PATTERN_PWD_ENG_NUM_SPC: ${matches(PATTERN_PW_ENG_NUM_SPC, "Ab1@Ab1@11")}")
        println()
    }
}
