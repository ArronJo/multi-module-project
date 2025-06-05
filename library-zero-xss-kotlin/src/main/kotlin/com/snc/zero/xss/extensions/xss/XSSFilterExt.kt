package com.snc.zero.xss.extensions.xss

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

fun String.cleanXSS(jsoup: Boolean = false): String {
    return if (jsoup) {
        // 간략한 버전: Jsoup.clean(this, Safelist.relaxed().preserveRelativeLinks(true))
        Jsoup.clean(
            this,
            Safelist()
                // 허용할 태그들
                .addTags(
                    "a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                    "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                    "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong",
                    "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul"
                )
                // 허용할 속성들
                .addAttributes("a", "href", "title")
                .addAttributes("blockquote", "cite")
                .addAttributes("col", "span", "width")
                .addAttributes("colgroup", "span", "width")
                .addAttributes("font", "color")
                .addAttributes("img", "align", "alt", "height", "src", "title", "width")
                .addAttributes("ol", "start", "type")
                .addAttributes("q", "cite")
                .addAttributes("table", "summary", "width")
                .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
                .addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width")
                .addAttributes("ul", "type")
                // 허용할 프로토콜
                .addProtocols("a", "href", "ftp", "http", "https", "mailto")
                .addProtocols("blockquote", "cite", "http", "https")
                .addProtocols("cite", "cite", "http", "https")
                .addProtocols("img", "src", "http", "https")
                .addProtocols("q", "cite", "http", "https")
                .addEnforcedAttribute("a", "rel", "nofollow")
                .preserveRelativeLinks(true) // img src 값으로 상대주소 허용
        )
    } else {
        this.replace("(?i)<script(.*?)</script>".toRegex(), "")
            .replace("(?i)javascript:".toRegex(), "")
            .escapeHtmlEntities()
    }
}
