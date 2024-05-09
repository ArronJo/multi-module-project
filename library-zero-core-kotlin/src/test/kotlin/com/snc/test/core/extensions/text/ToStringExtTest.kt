package com.snc.test.core.extensions.text

import com.snc.zero.core.extensions.text.toStrings
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class ToStringExtTest : BaseJUnit5Test() {

    @Test
    fun `toStrings - Array`() {
        // given
        val data = mutableListOf<String>()
        data.add("A")
        data.add("a")
        data.add("1")
        data.add("가")
        // when
        val v1 = data.toTypedArray().toStrings()
        // then
        logger.debug { "toStrings - Array 결과: $v1" }
        assertEquals(v1, "[A, a, 1, 가]")
    }

    @Test
    fun `toStrings - ByteArray`() {
        // given
        val data = "Aa1가"
        // when
        val v1 = data.toByteArray().toStrings()
        // then
        logger.debug { "toStrings - ByteArray 결과: $v1" }
        assertEquals(v1, "[41, 61, 31, ea, b0, 80]")
    }
}