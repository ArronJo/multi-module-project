package com.snc.test.etc.counter

import com.snc.zero.etc.counter.Counter
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class CounterTest : BaseJUnit5Test() {

    @Test
    fun `Counter 테스트 1`() {
        // given
        val data = Counter()
        // when
        data.put("1")
        data.put("2")
        data.put("3")
        data.put("2")
        data.put("1")
        // then
        logger.debug { "Counter 1 결과: $data, size: ${data.size()}, total: ${data.total()}" }
        assertEquals("{1=2, 2=2, 3=1}", data.toString())
        logger.debug { "Counter : ${data.get()}, ${data.get("1")}" }
    }
}
