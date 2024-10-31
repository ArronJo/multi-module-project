package com.snc.test.xss.extensions

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.xss.extensions.xss.escapeHtmlEntities
import com.snc.zero.xss.extensions.xss.unescapeHtmlEntities
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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
}
