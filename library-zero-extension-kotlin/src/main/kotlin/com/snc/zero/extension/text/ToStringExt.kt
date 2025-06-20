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

fun String.safeSubstring(startIndex: Int, endIndex: Int = this.length): String {
    val safeStart = maxOf(0, minOf(startIndex, this.length))
    val safeEnd = maxOf(safeStart, minOf(endIndex, this.length))
    return this.substring(safeStart, safeEnd)
}

fun String.printJSON(indentSize: Int = 2, padChar: Char = ' '): String {
    fun nextCharPos(sb: String, i: Int): Int {
        var j = 1
        while (i + j < sb.length) {
            // ignore space after comma as we have added a newline
            val nextChar = sb.getOrElse(i + j) { ' ' }
            if (nextChar != ' ') {
                return maxOf(i, i + j - 1) // 음수 방지
            }
            j++
        }
        return i
    }

    var indentLevel = 0
    fun padding() = "".padStart(maxOf(0, indentLevel * indentSize), padChar) // 음수 방지
    val toString = toString()
    val sb = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                sb.append(char).appendLine()
                indentLevel++
                sb.append(padding())
                i = nextCharPos(toString, i)
            }
            ')', ']', '}' -> {
                // 닫는 괄호 앞의 공백 제거
                while (sb.isNotEmpty() && sb.last() == ' ') {
                    sb.deleteCharAt(sb.length - 1)
                }
                indentLevel = maxOf(0, indentLevel - 1) // 음수 방지
                sb.appendLine().append(padding()).append(char)
            }
            ',' -> {
                // 쉼표 앞의 공백 제거
                while (sb.isNotEmpty() && sb.last() == ' ') {
                    sb.deleteCharAt(sb.length - 1)
                }
                sb.append(char).appendLine()
                sb.append(padding())
                i = nextCharPos(toString, i)
            }
            ' ' -> {
                // 연속된 공백 처리: 현재 위치가 특수문자 앞이 아닌 경우에만 공백 추가
                val nextCharIndex = i + 1
                if (nextCharIndex < toString.length) {
                    val nextChar = toString[nextCharIndex]
                    if (nextChar !in "()[]{}," && sb.isNotEmpty() && sb.last() != ' ') {
                        sb.append(char)
                    }
                } else if (sb.isNotEmpty() && sb.last() != ' ') {
                    sb.append(char)
                }
            }
            else -> {
                sb.append(char)
            }
        }
        i++
    }
    return sb.toString()
}
