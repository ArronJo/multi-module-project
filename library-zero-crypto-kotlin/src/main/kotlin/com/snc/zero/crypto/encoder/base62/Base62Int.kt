package com.snc.zero.crypto.encoder.base62

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Base62 from Integer
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object Base62Int {

    private val BASE62 =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

    fun encode(value: Int): String {
        val sb = StringBuilder()
        var tmp = abs(value)
        do {
            val i = tmp % 62
            sb.append(BASE62[i])
            tmp /= 62
        } while (tmp > 0)
        return sb.toString()
    }

    fun encode(value: Long): String {
        val sb = java.lang.StringBuilder()
        var v = abs(value)
        do {
            val i = v % 62
            sb.append(BASE62[i.toInt()])
            v /= 62
        } while (v > 0)
        return sb.toString()
    }

    fun decode(value: String): Int {
        var result = 0
        var power = 1
        for (v in value) {
            val digit: Int = String(BASE62).indexOf(v)
            result += digit * power
            power *= 62
        }
        return result
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("62^0 : 0                   : " + encode(0))
        println("62^1 : 62                  : " + encode(62))
        println("62^2 : 3,844               : " + encode(3844))
        println("62^3 : 238,328             : " + encode(238328))
        println("62^4 : 14,776,336          : " + encode(14776336))
        println("62^5 : 916,132,832         : " + encode(916132832))
        println("62^6 : 56,800,235,584      : " + encode(56800235584))
        println("62^7 : 3,521,614,606,208   : " + encode(3521614606208))
        println(" ~max: 218,340,105,584,895 : " + encode(218340105584895))
        println("62^8 : 218,340,105,584,896 : " + encode(218340105584896))
        println("MAX  : 9,223,372,036,854,775,807 : " + encode(Long.MAX_VALUE))
        println()
        println()

        println("DTC회원(12자리)   : " + encode(102312345678L))
        println("DTC회원(12자리): " + decode(encode(102312345678L)))

        println("주민등록번호(13자리): " + encode(7911231234567L))
        println("주민등록번호(13자리): " + decode(encode(7911231234567L)))
        println()
        println()

        println("날짜: " + encode(today("yyyyMMddHHmmssSSS").toLong()))
        println()
        println()
    }

    fun today(format: String = "yyyyMMdd"): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        return simpleDateFormat.format(Calendar.getInstance().timeInMillis)
    }
}
