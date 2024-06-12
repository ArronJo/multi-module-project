package com.snc.test.core.counter

import com.snc.zero.core.counter.Counter
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
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
        logger.debug { "Counter 1 결과: $data, ${data.total()}" }
        assertEquals(data.toString(), "{1=2, 2=2, 3=1}")
        logger.debug { "Counter : ${data.get()}, ${data.get("1")}" }
    }
}