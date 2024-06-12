package com.snc.test.core.extensions.random

import com.snc.zero.core.counter.Counter
import com.snc.zero.core.extensions.random.getRandomItem
import com.snc.zero.core.extensions.random.getWeightedRandom
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

class DrawExtTest : BaseJUnit5Test() {

    private var max: Int = 0
    private lateinit var counter: Counter

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        max = 30
        counter = Counter()
    }

    @Test
    fun `Random Draw Item 1`() {
        // given
        val data = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        // when
        for (i in 1..max) {
            val v1 = data.getRandomItem()
            counter.put(v1)
            // then
            logger.debug { "Random Draw Item 결과: $v1" }
        }
        logger.debug { "Random Draw Item 결과: Counter[${counter.size()}, ${counter.total()}] : $counter" }
    }

    @Test
    fun `Random Draw Item 2`() {
        assertThrows(IllegalArgumentException::class.java) {
            val data = arrayListOf<String>()
            data.getRandomItem()
        }
    }
    @Test
    fun `Weighted Random Drawing`() {
        // given
        val data = hashMapOf<String, Double>()
        data["김"] = 0.10689
        data["이"] = 0.07307
        data["박"] = 0.04192
        data["정"] = 0.02333
        data["조"] = 0.01176
        data["강"] = 0.01055
        data["장"] = 0.00992
        data["임"] = 0.00823
        data["한"] = 0.00773
        data["류"] = 0.00642
        data["홍"] = 0.00558
        data["고"] = 0.00471
        data["허"] = 0.00326
        data["남"] = 0.00275
        data["정"] = 0.00243
        data["신"] = 0.00192
        data["엄"] = 0.00144
        // when
        for (i in 1..max) {
            val v1 = data.getWeightedRandom()
            v1?.let {
                counter.put(it)
            }
            // then
            logger.debug { "Weighted Random Drawing 결과: $v1" }
        }
        logger.debug { "Weighted Random Drawing 결과: Counter[${counter.size()}, ${counter.total()}] : $counter" }
    }
}