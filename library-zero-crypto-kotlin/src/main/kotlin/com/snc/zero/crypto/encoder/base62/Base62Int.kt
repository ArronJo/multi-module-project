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

    fun today(format: String = "yyyyMMdd"): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        return simpleDateFormat.format(Calendar.getInstance().timeInMillis)
    }
}
