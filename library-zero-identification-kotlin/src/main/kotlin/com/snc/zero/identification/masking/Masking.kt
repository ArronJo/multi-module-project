package com.snc.zero.identification.masking

import kotlin.math.max
import kotlin.math.min

class Masking {

    companion object {

        @JvmStatic
        fun regNo(v: String): String {
            return masking(v, "[-6]")
        }

        @JvmStatic
        fun name(v: String): String {
            if (1 == v.length) {
                return "*"
            }
            if (2 == v.length) {
                return v[0] + "*"
            }
            return v[0] + "*".repeat(max(0, v.length - 2)) + v[v.length - 1]
        }

        @JvmStatic
        fun phoneNum(v: String): String {
            return masking(v, "[-4]")
        }

        @JvmStatic
        fun email(v: String): String {
            val split = v.split("@")
            val m = masking(split[0], "[-3]")
            return m + "@" + split[1]
        }

        @JvmStatic
        fun masking(v: String, rangePattern: String, charMark: Char = '*'): String {
            var pattern = rangePattern

            if (pattern[0] != '[' && pattern[pattern.length - 1] != ']') {
                return v.replace(pattern.toRegex(), ("" + charMark).repeat(pattern.length))
            }

            pattern = pattern.substring(0, pattern.length - 1).substring(1)
            if (pattern.isEmpty()) {
                return v
            }

            return parsePattern(v, pattern, charMark)
        }

        @JvmStatic
        private fun parsePattern(v: String, pattern: String, charMark: Char = '*'): String {
            // '-' 가 없음
            if (!pattern.contains("-")) {
                if ("*" == pattern) {
                    return ("" + charMark).repeat(v.length)
                }
                if (pattern.matches("^\\d+$".toRegex())) { // isNumber
                    val idx = pattern.toInt()
                    return marking(v, idx, idx + pattern.length, charMark)
                }
                return v
            }

            // '-' 가 0번째 위치함. ex) "-7"
            if (0 == pattern.indexOf("-")) {
                val arr = pattern.split("-")
                val st = max(0, v.length - Integer.parseInt(arr[1]))
                var ed = v.length
                if (pattern.length > 2) {
                    ed = v.length - Integer.parseInt(arr[2])
                }
                return marking(v, st, ed, charMark)
            }

            // '-' 가 1번째 뒤에 위치함. ex) "2-", "2-7", "2-@"
            if (pattern.indexOf("-") > 0) {
                val arr = pattern.split("-")
                val st = min(v.length, Integer.parseInt(arr[0]))
                var ed = v.length
                if (arr.size >= 2) {
                    // 숫자 위치이냐? 문자이냐?에 따라 길이 분기
                    ed = if (arr[1].matches("^\\d+$".toRegex())) {
                        min(v.length, Integer.parseInt(arr[1]) + 1)
                    } else {
                        max(st, v.indexOf(arr[1]))
                    }
                }
                return marking(v, st, ed, charMark)
            }

            return ""
        }

        @JvmStatic
        private fun marking(v: String, st: Int, ed: Int, charMark: Char = '*'): String {
            val firstPart: String = v.substring(0, st)
            val secondPart: String = v.substring(ed)
            return firstPart + ("" + charMark).repeat((ed - st)) + secondPart
        }
    }
}