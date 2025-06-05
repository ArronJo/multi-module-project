package com.snc.zero.xss.extensions.xss

fun String.escapeHtmlEntities(): String {
    // &apos; not recommended because it's not in the HTML spec (See: section 24.4.1) &apos; is in the XML and XHTML specs.
    // &apos; -> &#39 or &#x27,  https://www.w3schools.com/html/html_entities.asp
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
