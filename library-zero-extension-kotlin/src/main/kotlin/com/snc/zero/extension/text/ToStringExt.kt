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
    val processor = JsonFormatter(indentSize, padChar)
    return processor.format(this)
}

private class JsonFormatter(
    private val indentSize: Int,
    private val padChar: Char
) {
    private var indentLevel = 0
    private val sb = StringBuilder()

    fun format(input: String): String {
        var i = 0
        while (i < input.length) {
            val char = input[i]
            i = processCharacter(input, i, char)
        }
        return sb.toString()
    }

    private fun processCharacter(input: String, currentIndex: Int, char: Char): Int {
        return when (char) {
            '(', '[', '{' -> handleOpeningBrace(input, currentIndex, char)
            ')', ']', '}' -> {
                handleClosingBrace(char)
                currentIndex + 1
            }
            ',' -> handleComma(input, currentIndex)
            ' ' -> {
                handleSpace(input, currentIndex)
                currentIndex + 1
            }
            else -> {
                handleRegularChar(char)
                currentIndex + 1
            }
        }
    }

    private fun handleOpeningBrace(input: String, currentIndex: Int, char: Char): Int {
        sb.append(char).appendLine()
        indentLevel++
        sb.append(padding())
        return findNextNonSpacePosition(input, currentIndex)
    }

    private fun handleClosingBrace(char: Char) {
        removeTrailingSpaces()
        indentLevel = maxOf(0, indentLevel - 1)
        sb.appendLine().append(padding()).append(char)
    }

    private fun handleComma(input: String, currentIndex: Int): Int {
        removeTrailingSpaces()
        sb.append(',').appendLine()
        sb.append(padding())
        return findNextNonSpacePosition(input, currentIndex)
    }

    private fun handleSpace(input: String, currentIndex: Int) {
        if (shouldAddSpace(input, currentIndex)) {
            sb.append(' ')
        }
    }

    private fun handleRegularChar(char: Char) {
        sb.append(char)
    }

    private fun shouldAddSpace(input: String, currentIndex: Int): Boolean {
        val nextCharIndex = currentIndex + 1
        return when {
            nextCharIndex >= input.length -> hasValidPreviousChar()
            isSpecialChar(input[nextCharIndex]) -> false
            else -> hasValidPreviousChar()
        }
    }

    private fun hasValidPreviousChar(): Boolean {
        return sb.isNotEmpty() && sb.last() != ' '
    }

    private fun isSpecialChar(char: Char): Boolean {
        return char in "()[]{},"
    }

    private fun removeTrailingSpaces() {
        while (sb.isNotEmpty() && sb.last() == ' ') {
            sb.deleteCharAt(sb.length - 1)
        }
    }

    private fun findNextNonSpacePosition(input: String, currentIndex: Int): Int {
        var position = currentIndex + 1
        while (position < input.length && input[position] == ' ') {
            position++
        }
        return position
    }

    private fun padding(): String {
        return "".padStart(maxOf(0, indentLevel * indentSize), padChar)
    }
}
