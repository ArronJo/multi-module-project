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
    return joinToString(", ", "[", "]") {
        if (it.code in 0..127) "%02x".format(it.code) else "%04x".format(it.code)
    }
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

/**
 * https://gist.github.com/Mayankmkh/92084bdf2b59288d3e74c3735cccbf9f
 */
fun Any.toPrettyString(indentSize: Int = 2, padChar: Char = ' '): String {
    fun nextCharPos(sb: String, i: Int): Int {
        var j = 1
        while (i + j < sb.length) {
            // ignore space after comma as we have added a newline
            val nextChar = sb.getOrElse(i + j) { ' ' }
            if (nextChar != ' ') {
                return i + j - 1
            }
            j++
        }
        return i
    }

    var indentLevel = 0
    fun padding() = "".padStart(indentLevel * indentSize, padChar)
    val toString = toString()
    val sb = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                indentLevel++
                sb.appendLine(char).append(padding())
                i = nextCharPos(toString, i)
            }
            ')', ']', '}' -> {
                indentLevel--
                sb.appendLine().append(padding()).append(char)
            }
            ',' -> {
                sb.appendLine(char).append(padding())
                i = nextCharPos(toString, i)
            }
            else -> {
                sb.append(char)
            }
        }
        i++
    }
    return sb.toString()
}
