package com.snc.test.core.ua

import com.snc.zero.core.ua.UAParser
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import ua_parser.Client

private val logger = TLogging.logger { }

class UAParserTest : BaseJUnit5Test() {

    @Test
    fun `UA Parsing iOS`() {
        // given
        val data =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3"
        val c: Client = UAParser().parse(data)
        // when
        val v1 = c.userAgent
        // then
        logger.debug { "UA Parsing iOS 결과: $v1" }
    }

    @Test
    fun `UA Parsing Android`() {
        // given
        val data =
            "Mozilla/5.0 (Linux; Android 7.0; SM-P585N0 Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/56.0.2924.87 Safari/537.36"
        val c: Client = UAParser().parse(data)
        // when
        val v1 = c.userAgent
        // then
        logger.debug { "UA Parsing Android 결과: $v1" }
    }
}
