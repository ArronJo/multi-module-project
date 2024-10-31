package com.snc.zero.core.extensions.text

import com.snc.zero.core.extensions.format.formatDateTime
import java.util.*

fun <T> Array<T>.toStrings(): String {
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

fun ByteArray.toStrings(): String {
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

fun Calendar.toStrings(): String {
    return this.formatDateTime("yyyyMMddHHmmss")
}
