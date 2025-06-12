package com.sun.toolkit

import java.net.MalformedURLException

class Uri(val uri: String) {
    var scheme: String = ""
    var hasAuthority: Boolean = false
    var host: String = ""
    var path: String = ""
    var port: Int = -1
    var query: String? = null

    init {
        parse()
    }

    private fun parse() {
        val schemeEnd = parseScheme()
        val authorityEnd = parseAuthority(schemeEnd)
        parsePathAndQuery(authorityEnd)
    }

    private fun parseScheme(): Int {
        val colonIndex = uri.indexOf(':')
        if (colonIndex < 0) {
            throw MalformedURLException("Invalid URI: $uri")
        }
        scheme = uri.substring(0, colonIndex)
        return colonIndex + 1
    }

    private fun parseAuthority(startIndex: Int): Int {
        var i = startIndex
        hasAuthority = uri.startsWith("//", i)
        if (!hasAuthority) {
            return i
        }
        i += 2 // skip past "//"
        val authorityEnd = findPathStartOrEnd(i)
        i = parseHost(i, authorityEnd)
        parsePort(i, authorityEnd)
        return authorityEnd
    }

    private fun findPathStartOrEnd(startIndex: Int): Int {
        val slashIndex = uri.indexOf('/', startIndex)
        val questionIndex = uri.indexOf('?', startIndex)

        return when {
            slashIndex >= 0 && questionIndex >= 0 -> minOf(slashIndex, questionIndex)
            slashIndex >= 0 -> slashIndex
            questionIndex >= 0 -> questionIndex
            else -> uri.length
        }
    }

    private fun parseHost(startIndex: Int, endIndex: Int): Int {
        var i = startIndex

        if (uri.startsWith("[", i)) {
            i = parseIPv6Host(i, endIndex)
        } else {
            i = parseRegularHost(i, endIndex)
        }

        return i
    }

    private fun parseIPv6Host(startIndex: Int, endIndex: Int): Int {
        val bracketEnd = uri.indexOf(']', startIndex + 1)
        if (bracketEnd < 0 || bracketEnd > endIndex) {
            throw MalformedURLException("Invalid URI: $uri")
        }
        host = uri.substring(startIndex, bracketEnd + 1)
        return bracketEnd + 1
    }

    private fun parseRegularHost(startIndex: Int, endIndex: Int): Int {
        val colon = uri.indexOf(':', startIndex)
        val hostEnd = if (colon < 0 || colon > endIndex) endIndex else colon

        if (startIndex < hostEnd) {
            host = uri.substring(startIndex, hostEnd)
        }

        return hostEnd
    }

    private fun parsePort(startIndex: Int, endIndex: Int): Int {
        if (startIndex + 1 < endIndex && uri.startsWith(":", startIndex)) {
            val portStart = startIndex + 1
            port = uri.substring(portStart, endIndex).toInt()
        }
        return endIndex
    }

    private fun parsePathAndQuery(startIndex: Int) {
        val questionMark = uri.indexOf('?', startIndex)

        if (questionMark < 0) {
            path = uri.substring(startIndex)
        } else {
            path = uri.substring(startIndex, questionMark)
            query = uri.substring(questionMark)
        }
    }
}
