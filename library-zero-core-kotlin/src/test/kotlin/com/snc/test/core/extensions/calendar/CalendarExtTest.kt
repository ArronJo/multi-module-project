package com.snc.test.core.extensions.calendar

import com.snc.zero.core.extensions.random.getRandomItem
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.test.counter.Counter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

class CalendarExtTest : BaseJUnit5Test() {

    private var max: Int = 0
    private lateinit var counter: Counter

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        max = 30
        counter = Counter()
    }

    @Test
    fun `Random Draw Item`() {
        // given
        val data = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        // when
        for (i in 1..max) {
            val v1 = data.getRandomItem()
            counter.put(v1)

            // then
            logger.debug { "Random Draw Item 결과: $v1" }
            //assertEquals(v1, false)
        }
        logger.debug { "Random Draw Item 결과: Counter[${counter.size()}, ${counter.total()}] : $counter" }
    }
}