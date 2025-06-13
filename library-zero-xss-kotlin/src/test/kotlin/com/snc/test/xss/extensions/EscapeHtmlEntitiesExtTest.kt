package com.snc.test.xss.extensions

import com.google.common.html.HtmlEscapers
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.xss.extensions.xss.escapeHtmlEntities
import com.snc.zero.xss.extensions.xss.unescapeHtmlEntities
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.math.max

private val logger = TLogging.logger { }

class EscapeHtmlEntitiesExtTest : BaseJUnit5Test() {

    companion object {

        private lateinit var data1: String
        private lateinit var data2: String

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            data1 = "Hello, <b>World!</b> & <font color=\"#A6ABB1\">Welcome</font>> to \"Java\"."
            data2 = "Hello, &lt;b&gt;World!&lt;/b&gt; &amp; &lt;font color=&quot;#A6ABB1&quot;&gt;Welcome&lt;/font&gt;&gt; to &quot;Java&quot;."
        }
    }

    @Test
    fun `escape HtmlEntities`() {
        // given
        // when
        val v1 = data1.escapeHtmlEntities()
        // then
        logger.debug { "escape HtmlEntities 결과: $v1" }
        assertEquals(data2, v1)
    }

    @Test
    fun `unescape HtmlEntities`() {
        // given
        // when
        val v1 = data2.unescapeHtmlEntities()
        // then
        logger.debug { "unescape HtmlEntities 결과: $v1" }
        assertEquals(data1, v1)
    }

    @Test
    fun `Google Guava`() {
        val v1 = HtmlEscapers.htmlEscaper().escape(data1)
        logger.debug { "Google Guava: escapeHtml 결과: $v1" }
    }

    @Test
    fun `Simple Stackoverflow`() {
        val v1 = escapeHTML(data1)
        logger.debug { "Stackoverflow: escapeHtml 결과: $v1" }
    }

    // 코드 직접 구현
    // https://stackoverflow.com/a/8838023/1199155
    private fun escapeHTML(s: String): String {
        val out: StringBuilder = StringBuilder(max(16, s.length))
        for (c in s) {
            if (c.code > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#")
                out.append(c.code)
                out.append(';')
            } else {
                out.append(c)
            }
        }
        return out.toString()
    }
}
