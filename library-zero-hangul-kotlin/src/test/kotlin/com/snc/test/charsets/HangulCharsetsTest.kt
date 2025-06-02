package com.snc.test.charsets

import com.snc.zero.extensions.text.print
import com.snc.zero.hangul.charsets.HangulCharsets
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HangulCharsetsTest : BaseJUnit5Test() {

    @Test
    fun `유니코드 확인하기`() {
        logger.info { HangulCharsets.unicode("ㄱ").print() }
        logger.info { HangulCharsets.unicode("ㅎ").print() }
        logger.info { HangulCharsets.unicode("ㅏ").print() }
        logger.info { HangulCharsets.unicode("ㅣ").print() }
        logger.info { HangulCharsets.unicode("가").print() }
        logger.info { HangulCharsets.unicode("핳").print() }
    }
}
