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
        var i: Int // index into URI

        i = uri.indexOf(':') // parse scheme

        if (i < 0) {
            throw MalformedURLException("Invalid URI: $uri")
        }
        scheme = uri.substring(0, i)
        i++ // skip past ":"

        hasAuthority = uri.startsWith("//", i)
        if (hasAuthority) { // parse "//host:port"
            i += 2 // skip past "//"
            var slash: Int = uri.indexOf('/', i)
            if (slash < 0) {
                slash = uri.length
            }
            if (uri.startsWith("[", i)) { // at IPv6 literal
                val brac: Int = uri.indexOf(']', i + 1)
                if (brac < 0 || brac > slash) {
                    throw MalformedURLException("Invalid URI: $uri")
                }
                host = uri.substring(i, brac + 1) // include brackets
                i = brac + 1 // skip past "[...]"
            } else { // at host name or IPv4
                val colon: Int = uri.indexOf(':', i)
                val hostEnd = if (colon < 0 || colon > slash) slash else colon
                if (i < hostEnd) {
                    host = uri.substring(i, hostEnd)
                }
                i = hostEnd // skip past host
            }
            if (i + 1 < slash &&
                uri.startsWith(":", i)
            ) { // parse port
                i++ // skip past ":"
                port = uri.substring(i, slash).toInt()
            }
            i = slash // skip to path
        }
        val qmark: Int = uri.indexOf('?', i) // look for query

        if (qmark < 0) {
            path = uri.substring(i)
        } else {
            path = uri.substring(i, qmark)
            query = uri.substring(qmark)
        }
    }
}
