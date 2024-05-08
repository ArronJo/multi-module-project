package com.snc.zero.xss.extensions.xss

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

fun String.cleanXSS(): String {
    return Jsoup.clean(this, Safelist.relaxed().preserveRelativeLinks(true))
}
