package com.snc.zero.extension.text

import com.snc.zero.extension.format.formatDateTime
import java.util.*

fun <T> Array<T>.print(): String {
    val sb = StringBuilder()
    for (v in this) {
        if (sb.isNotEmpty()) {
            sb.append(", ")
        }
        sb.append(v)
    }
    sb.insert(0, "[")
    sb.append("]")
    return sb.toString()
}

fun Char.toHexString(): String =
    if (code in 0..127) "%02x".format(code) else "%04x".format(code)

fun CharArray.print(): String =
    joinToString(", ", "[", "]") { it.toHexString() }

fun ByteArray.print(): String {
    val sb = StringBuilder()
    for (v in this) {
        if (sb.isNotEmpty()) {
            sb.append(", ")
        }
        sb.append(v.toHexString())
    }
    sb.insert(0, "[")
    sb.append("]")
    return sb.toString()
}

fun IntArray.print(): String {
    return this.contentToString()
    //return this.joinToString(prefix = "[", separator = ",", postfix = "]")
}

fun Calendar.print(): String {
    return this.formatDateTime("yyyyMMddHHmmss")
}
