package com.snc.test.extension.text

import com.snc.zero.extensions.text.escapeRegExp
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class EscapeExtTest : BaseJUnit5Test() {

    @Nested
    inner class EscapeStringTest {

        @Test
        fun `입력된 문자를 정규식으로 변환하기`() {
            val input = "(C)가나다"
            val escapedRegex = "(c".escapeRegExp().toRegex(RegexOption.IGNORE_CASE)

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
}
