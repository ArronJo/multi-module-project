package com.snc.zero.core.util

class EscapeUtil private constructor() {

    companion object {

        fun escapeRegExp(text: String): String {
            return text.replace("""[.*+?^$\\{}\[\]()|]""".toRegex(), "\\\\$0")
        }

        fun escapeHtmlEntities(text: String): String {
            // &apos; not recommended because it's not in the HTML spec (See: section 24.4.1) &apos; is in the XML and XHTML specs.
            // &apos; -> &#39 or &#x27,  https://www.w3schools.com/html/html_entities.asp
            return text.replace("&".toRegex(), "&amp;")
                .replace("<".toRegex(), "&lt;")
                .replace(">".toRegex(), "&gt;")
                .replace("\"".toRegex(), "&quot;")
                .replace("'".toRegex(), "&#39;")
        }

        fun unescapeHtmlEntities(text: String): String {
            return text.replace("&lt;".toRegex(), "<")
                .replace("&gt;".toRegex(), ">")
                .replace("&amp;".toRegex(), "&")
                .replace("&quot;".toRegex(), "\"")
                .replace("&apos;".toRegex(), "'")
                .replace("&#39;".toRegex(), "'")
        }
    }
}
