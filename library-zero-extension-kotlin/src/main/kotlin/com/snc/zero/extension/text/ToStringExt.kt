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

fun CharArray.print(): String {
    val byteArray =  this.map { it.code.toByte() }.toByteArray()
    val hexString = byteArray.joinToString(", ", "[", "]") {
        String.format("%02x", it)
    }
    return hexString
}

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
