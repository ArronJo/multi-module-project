package com.snc.zero.xss.extensions.xss

fun String.escapeHtmlEntities(): String {
    return this.replace("&".toRegex(), "&amp;")
        .replace("<".toRegex(), "&lt;")
        .replace(">".toRegex(), "&gt;")
        .replace("\"".toRegex(), "&quot;")
        .replace("'".toRegex(), "&#39;")
}

fun String.unescapeHtmlEntities(): String {
    return this.replace("&lt;".toRegex(), "<")
        .replace("&gt;".toRegex(), ">")
        .replace("&amp;".toRegex(), "&")
        .replace("&quot;".toRegex(), "\"")
        .replace("&apos;".toRegex(), "'")
        .replace("&#39;".toRegex(), "'")
}
