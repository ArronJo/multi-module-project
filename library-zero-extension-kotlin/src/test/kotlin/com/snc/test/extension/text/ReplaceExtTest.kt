package com.snc.test.extension.text

import com.snc.zero.extension.text.remove
import com.snc.zero.extension.text.removeNewLine
import com.snc.zero.extension.text.removePrefixSuffix
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class ReplaceExtTest : BaseJUnit5Test() {

    @Test
    fun `remove NewLine Character`() {
        // given
        val data = "안녕하세요\n이것은 여러 줄의\n문자열입니다.\r\n줄 바꿈을 제거합니다."
        // when
        val v1 = data.removeNewLine()
        // then
        logger.debug { "결과: $v1" }
    }

    @Test
    fun `remove Letter Character`() {
        // given
        val data = "안녕하세요\n이것은 abcd01234ABCD567890\n문자열입니다.\r\n영어 문자를 제거합니다."
        // when
        val v1 = data.remove("[a-zA-Z]".toRegex())
        // then
        logger.debug { "결과: $v1" }
    }

    @Test
    fun `remove surround Character`() {
        // given
        val data = "안녕하세요\n이것은 abcd01234ABCD567890\n문자열입니다.\r\n영어 문자를 제거합니다."
        // when
        val v1 = data.removePrefixSuffix("안녕하세요", "제거합니다.")
        val v2 = data.removePrefix("안녕하세요")
        val v3 = data.removeSuffix("제거합니다.")
        // then
        logger.debug { "결과: $v1" }
        logger.debug { "결과: $v2" }
        logger.debug { "결과: $v3" }
    }
}
