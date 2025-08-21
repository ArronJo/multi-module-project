package com.snc.test.core.util

import com.snc.zero.core.util.EscapeUtil
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class EscapeUtilTest : BaseJUnit5Test() {

    @Nested
    inner class EscapeStringTest {

        @Test
        fun `입력된 문자를 정규식으로 변환하기`() {
            val input = "(C)가나다"
            val escapedRegex = EscapeUtil.escapeRegExp("(c").toRegex(RegexOption.IGNORE_CASE)

            val matchResult = escapedRegex.find(input)
            if (matchResult != null) {
                println("매칭된 부분: ${matchResult.value}")
            } else {
                println("매칭되는 부분이 없습니다.")
            }

            if (input.matches(escapedRegex)) {
                println("'$input'이(가) 매칭됩니다.")
            } else {
                println("'$input'이(가) 매칭되지 않습니다.")
            }

        }
    }

    @Nested
    inner class EscapeHtmlEntitiesTest {

        private val data1 = "Hello, <b>World!</b> & <font color=\"#A6ABB1\">Welcome</font>> to \"Java\"."
        private val data2 = "Hello, &lt;b&gt;World!&lt;/b&gt; &amp; &lt;font color=&quot;#A6ABB1&quot;&gt;Welcome&lt;/font&gt;&gt; to &quot;Java&quot;."

        @Test
        fun `escape HtmlEntities`() {
            val v1 = EscapeUtil.escapeHtmlEntities(data1)
            println("escape HtmlEntities 결과: $v1")
        }

        @Test
        fun `unescape HtmlEntities`() {
            val v1 = EscapeUtil.unescapeHtmlEntities(data2)
            println("unescape HtmlEntities 결과: $v1")
        }
    }
}
