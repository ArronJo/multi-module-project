package com.snc.zero.xss.extensions.xss

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

fun String.cleanXSS(jsoup: Boolean = false): String {
    return if (jsoup) {
        Jsoup.clean(this, Safelist.relaxed().preserveRelativeLinks(true))
    } else {
        this.replace("(?i)<script(.*?)</script>".toRegex(), "")
            .replace("(?i)javascript:".toRegex(), "")
            .escapeHtmlEntities()
    }
}
