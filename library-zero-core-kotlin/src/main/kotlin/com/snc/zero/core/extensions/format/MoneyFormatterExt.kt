package com.snc.zero.core.extensions.format

import java.text.DecimalFormat

fun Long.formatNumericalKoreanMoney(unit: String = "만"): String {
    val c = when (unit) {
        "경" -> { 16 }
        "천조" -> { 15 }
        "백조" -> { 14 }
        "십조" -> { 13 }
        "조" -> { 12 }
        "천억" -> { 11 }
        "백억" -> { 10 }
        "십억" -> { 9 }
        "억" -> { 8 }
        "천만", "천만원" -> { 7 }
        "백만", "백만원" -> { 6 }
        "십만", "십만원" -> { 5 }
        "만", "만원" -> { 4 }
        "천", "천원" -> { 3 }
        else -> { 0 }
    }

    var u = 1
    for (i in 1..c) {
        u *= 10
    }
    if (this >= u) {
        val n = this / u
        return DecimalFormat("#,###,###").format(n) + unit
    }
    return this.toString()
}

fun Long.formatWordKoreanMoney(): String {
    val han1 = arrayOf( "", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구" )
    val han2 = arrayOf( "", "십", "백", "천" )
    val han3 = arrayOf( "", "만", "억", "조", "경" )

    val sb = StringBuilder()
    val str = this.toString()
    val len = str.length
    for (i in (len - 1) downTo 1) {
        val s = str.substring(len-i-1, len-i)
        val v = Integer.parseInt(s)
        sb.append(han1[v])
        if (v > 0) {
            sb.append(han2[i % 4])
        }
        if (0 == i % 4) {
            sb.append(han3[i / 4])
        }
    }
    return sb.toString()
}
