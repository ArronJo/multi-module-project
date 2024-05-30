package com.snc.test.core.extensions.random

import com.snc.zero.core.counter.Counter
import com.snc.zero.core.extensions.random.randomInt
import com.snc.zero.core.extensions.random.randomString
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class RandomExtTest : BaseJUnit5Test() {

    private var max = 1
    private var len = 1
    private lateinit var counter: Counter

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        max = 10
        len = 20
        counter = Counter()
    }

    @Test
    fun `Random Int - 1`() {
        // given
        for (i in 1..max) {
            // when
            val v1 = randomInt(bound = 10)
            counter.put(v1.toString())
            // then
            logger.debug { "Random Int 결과 - 1: $v1" }
            //assertEquals(v1, false)
        }
        logger.debug { "Random Int 결과 - 1: Counter[${counter.size()}, ${counter.total()}] : $counter" }
    }

    @Test
    fun `Random Int - 2`() {
        // given
        for (i in 1..max) {
            // when
            val v1 = randomInt(0, 2)
            counter.put(v1.toString())
            // then
            logger.debug { "Random Int 결과 - 2: $v1" }
            //assertEquals(v1, false)
        }
        logger.debug { "Random Int 결과 - 2: Counter[${counter.size()}, ${counter.total()}] : $counter" }
    }

    @Test
    fun `Random Int - 3`() {
        // given
        for (i in 1..max) {
            // when
            val v1 = randomInt(10, 40)
            counter.put(v1.toString())
            // then
            logger.debug { "Random Int 결과 - 3: $v1" }
            //assertEquals(v1, false)
        }
        logger.debug { "Random Int 결과 - 3: Counter[${counter.size()}, ${counter.total()}] : $counter" }
    }

    @Test
    fun `Random 문자열 생성 - 1`() {
        // given
        // when
        val v1 = randomString(len)
        // then
        logger.debug { "Random String 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Random 문자열 생성 - 2`() {
        // given
        // when
        val v1 = randomString(len, isDigit = true)
        // then
        logger.debug { "Random String 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Random 문자열 생성 - 3`() {
        // given
        // when
        val v1 = randomString(len, isUpperCase = true)
        // then
        logger.debug { "Random String 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Random 문자열 생성 - 4`() {
        // given
        // when
        val v1 = randomString(len, isLowerCase = true)
        // then
        logger.debug { "Random String 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Random 문자열 생성 - 5`() {
        // given
        // when
        val v1 = randomString(len, isLetter = true)
        // then
        logger.debug { "Random String 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `Random 문자열 생성 - 6`() {
        // given
        // when
        for (i in 1..max) {
            val v1 = randomString(len, isHangul = true)
            // then
            logger.debug { "Random String 결과: $v1" }
            //assertEquals(v1, false)
        }
    }
}