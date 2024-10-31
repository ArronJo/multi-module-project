package com.snc.test.xss.extensions

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.xss.extensions.xss.cleanXSS
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class XSSFilterExtTest : BaseJUnit5Test() {

    companion object {

        private lateinit var data1: String
        private lateinit var data2: String

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            data1 =
                "Hello, <b>World!</b> & <font color=\"#A6ABB1\">Welcome</font> to \"<Script>Java</script>\". <script>javascript:alert('1')</script>"

            data2 = "당사 사정에 따라 일정이 변경될 수 있으며 변동 시 재공지하도록 하겠습니다." +
                "<sCripT>javascript:alert(1111)</ScriPt>\"<BR>오늘도 방문해주셔서 감사합니다." +
                "<sCripT>javascript:alert(222)</ScriPt>마지막" +
                "test><b onclick=JaVascrIpT:alert(document.cookie + 'abcde')>>xsstest</b>" +
                "P Tag 테스트 ><P onclick=alert(document.cookie)>>PPPPP</p>" +
                "Font Tag 테스트 ><font color=\"red\">>FFFFFFFF</font>" +
                "></script><script>alert(document.cookie)</script>응???"
        }
    }

    @Test
    fun `clean XSS 1`() {
        // given
        // when
        val v1 = data1.cleanXSS()
        // then
        logger.debug { "clean XSS 1 결과: $v1" }
        assertEquals("Hello, &lt;b&gt;World!&lt;/b&gt; &amp; &lt;font color=&quot;#A6ABB1&quot;&gt;Welcome&lt;/font&gt; to &quot;&quot;. ", v1)
    }

    @Test
    fun `clean XSS 2`() {
        // given
        // when
        val v1 = data2.cleanXSS(jsoup = true)
        // then
        logger.debug { "clean XSS 2 결과: $v1" }
        assertEquals(
            "당사 사정에 따라 일정이 변경될 수 있으며 변동 시 재공지하도록 하겠습니다.\"\n" +
                "<br>\n" +
                "오늘도 방문해주셔서 감사합니다.마지막test&gt;<b>&gt;xsstest</b>P Tag 테스트 &gt;\n" +
                "<p>&gt;PPPPP</p>Font Tag 테스트 &gt;&gt;FFFFFFFF&gt;응???",
            v1
        )
    }

    @Test
    fun `Jsoup clean 테스트`() {
        logger.debug { "-Jsoup.clean 테스트 (모든 태그 삭제): \n${Jsoup.clean(data2, Safelist())}" }
        logger.debug { "\n\n" }

        logger.debug {
            """-Jsoup.clean 테스트 (Safelist.basicWithImages):
            ${Jsoup.clean(data2, Safelist.basicWithImages().preserveRelativeLinks(true))}
            """
        }
        logger.debug { "\n\n" }
    }
}
