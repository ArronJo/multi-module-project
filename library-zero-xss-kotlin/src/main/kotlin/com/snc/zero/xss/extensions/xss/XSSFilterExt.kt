package com.snc.zero.xss.extensions.xss

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

fun String.cleanXSS(): String {
    return Jsoup.clean(this, Safelist.relaxed().preserveRelativeLinks(true))
    //return this.replace("(?i)<script(.*?)</script>".toRegex(), "")
    //    .replace("(?i)javascript:".toRegex(), "")
    //    .escapeHtmlEntities()
}
