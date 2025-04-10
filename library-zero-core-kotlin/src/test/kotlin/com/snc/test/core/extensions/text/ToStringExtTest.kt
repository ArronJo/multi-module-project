package com.snc.test.core.extensions.text

import com.snc.zero.core.extensions.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.Calendar

private val logger = TLogging.logger { }

class ToStringExtTest : BaseJUnit5Test() {

    @Test
    fun `Print - Array`() {
        // given
        val data = mutableListOf<String>()
        data.add("A")
        data.add("a")
        data.add("1")
        data.add("가")
        // when
        val v1 = data.toTypedArray().print()
        // then
        logger.debug { "Print - Array 결과: $v1" }
        assertEquals("[A, a, 1, 가]", v1)
    }

    @Test
    fun `Print - ByteArray`() {
        // given
        val data = "Aa1가"
        // when
        val v1 = data.toByteArray().print()
        // then
        logger.debug { "Print - ByteArray 결과: $v1" }
        assertEquals("[41, 61, 31, ea, b0, 80]", v1)
    }

    @Test
    fun `Print - IntArray`() {
        // given
        val data = arrayOf(1, 2, 3, 4)
        // when
        val v1 = data.print()
        // then
        logger.debug { "Print - IntArray 결과: $v1" }
        assertEquals("[1, 2, 3, 4]", v1)
    }

    @Test
    fun `Print - Calendar`() {
        // given
        val data = Calendar.getInstance()
        // when
        val v1 = data.print()
        // then
        logger.debug { "Print - Calendar 결과: $v1" }
    }
}
