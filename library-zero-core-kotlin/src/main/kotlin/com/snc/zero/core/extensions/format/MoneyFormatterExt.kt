package com.snc.zero.core.extensions.format

import java.text.DecimalFormat

fun Long.formatKoreanMoney(unit: String = "만"): String {
    val c = if ("경" == unit) { 16 }
    else if ("천조" == unit) { 15 }
    else if ("백조" == unit) { 14 }
    else if ("십조" == unit) { 13 }
    else if ("조" == unit) { 12 }
    else if ("천억" == unit) { 11 }
    else if ("백억" == unit) { 10 }
    else if ("십억" == unit) { 9 }
    else if ("억" == unit) { 8 }
    else if ("천만" == unit || "천만원" == unit) { 7 }
    else if ("백만" == unit || "백만원" == unit) { 6 }
    else if ("십만" == unit || "십만원" == unit) { 5 }
    else if ("만" == unit || "만원" == unit) { 4 }
    else if ("천" == unit || "천원" == unit) { 3 }
    else { 0 }

    var u = 1
    for (i in 1..c) {
        u *= 10
    }
    if (this < u) {
        return this.toString()
    }
    val n = this / u
    return DecimalFormat("#,###,###").format(n) + unit
}

fun Long.formatRealKoreanMoney(): String {
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
