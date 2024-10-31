package com.snc.zero.core.extensions.format

import java.text.DecimalFormat

fun Long.formatNumericalKoreanMoney(unit: String = "만"): String {
    val unitMap = mapOf(
        "경" to 16, "천조" to 15, "백조" to 14, "십조" to 13, "조" to 12,
        "천억" to 11, "백억" to 10, "십억" to 9, "억" to 8,
        "천만" to 7, "천만원" to 7, "백만" to 6, "백만원" to 6,
        "십만" to 5, "십만원" to 5, "만" to 4, "만원" to 4,
        "천" to 3, "천원" to 3
    )

    val c = unitMap[unit] ?: 0
    val u = calculateUnit(c)

    return if (this >= u) {
        val n = this / u
        "${DecimalFormat("#,###,###").format(n)}$unit"
    } else {
        this.toString()
    }
}

private fun calculateUnit(c: Int): Long {
    var u = 1L
    repeat(c) { u *= 10 }
    return u
}

fun Long.formatWordKoreanMoney(): String {
    val han1 = arrayOf("", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구")
    val han2 = arrayOf("", "십", "백", "천")
    val han3 = arrayOf("", "만", "억", "조", "경")

    val sb = StringBuilder()
    val str = this.toString()
    val len = str.length
    for (i in (len - 1) downTo 1) {
        val s = str.substring(len - i - 1, len - i)
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
