package com.snc.zero.identification.masking

import kotlin.math.max
import kotlin.math.min

/**
 * 중요정보 마스킹
 *
 * @author mcharima5@gmail.com
 * @since 2024
 */
object Masking {

    fun regNo(v: String): String {
        return masking(v, "[-6]")
    }

    fun name(v: String): String {
        if (1 == v.length) {
            return "*"
        }
        if (2 == v.length) {
            return v[0] + "*"
        }
        return v[0] + "*".repeat(max(0, v.length - 2)) + v[v.length - 1]
    }

    fun phoneNum(v: String): String {
        return masking(v, "[-4]")
    }

    fun email(v: String): String {
        val split = v.split("@")
        val m = masking(split[0], "[-3]")
        return m + "@" + split[1]
    }

    fun masking(v: String, rangePattern: String, charMark: Char = '*'): String {
        var pattern = rangePattern
        if (pattern[0] != '[' || pattern[pattern.length - 1] != ']') {
            return v.replace(pattern, ("" + charMark).repeat(pattern.length))
        }
        pattern = pattern.substring(0, pattern.length - 1).substring(1)
        if (pattern.isEmpty()) {
            return v
        }
        return parsePattern(v, pattern, charMark)
    }

    fun masking(v: String, regex: Regex, charMark: Char = '*'): String {
        return v.replace(regex, charMark.toString())
    }

    fun masking(v: String, st: Int, ed: Int, charMark: Char = '*'): String {
        val firstPart: String = v.substring(0, st)
        val secondPart: String = v.substring(ed)
        return firstPart + ("" + charMark).repeat((ed - st)) + secondPart
    }

    private fun parsePattern(v: String, pattern: String, charMark: Char): String {
        // '-' 가 없음
        if (!pattern.contains("-")) {
            if ("*" == pattern) {
                return ("" + charMark).repeat(v.length)
            }
            if (pattern.matches("^\\d+$".toRegex())) { // isNumber
                val idx = pattern.toInt()
                return masking(v, idx, idx + pattern.length, charMark)
            }
            return v
        }

        // '-' 가 0번째 위치함. ex) "-7"
        if (0 == pattern.indexOf("-")) {
            val arr = pattern.split("-")
            val st = max(0, v.length - Integer.parseInt(arr[1]))
            val ed = v.length
            return masking(v, st, ed, charMark)
        }

        // '-' 가 1번째 뒤에 위치함. ex) "2-", "2-7", "2-@"
        val arr = pattern.split("-")
        val st = min(v.length, Integer.parseInt(arr[0]))
        // 숫자 위치이냐? 문자이냐?에 따라 길이 분기
        val ed = if (arr[1].matches("^\\d+$".toRegex())) {
            min(v.length, Integer.parseInt(arr[1]) + 1)
        } else {
            max(st, v.indexOf(arr[1]))
        }
        return masking(v, st, ed, charMark)
    }
}
