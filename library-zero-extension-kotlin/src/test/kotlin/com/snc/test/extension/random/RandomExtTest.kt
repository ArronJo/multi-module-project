package com.snc.test.extension.random

import com.snc.zero.etc.counter.Counter
import com.snc.zero.extensions.random.generateRandomString
import com.snc.zero.extensions.random.getRandomItem
import com.snc.zero.extensions.random.getWeightedRandom
import com.snc.zero.extensions.random.randomInt
import com.snc.zero.extensions.random.randomString
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
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

    @Nested
    inner class GenerateInt {

        @Test
        fun `Random Int - 1`() {
            // given
            for (i in 1..max) {
                // when
                val v1 = randomInt(bound = 10)
                counter.put(v1.toString())
                // then
                logger.debug { "Random Int 결과 - 1 [$i]: $v1" }
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
                logger.debug { "Random Int 결과 - 2 [$i]: $v1" }
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
                logger.debug { "Random Int 결과 - 3 [$i]: $v1" }
            }
            logger.debug { "Random Int 결과 - 3: Counter[${counter.size()}, ${counter.total()}] : $counter" }
        }
    }

    @Nested
    inner class GenerateString {

        @Test
        fun `Random 문자열 생성 - 1`() {
            // given
            // when
            val v1 = randomString(len)
            // then
            logger.debug { "Random String 결과: $v1" }
        }

        @Test
        fun `Random 문자열 생성 - 2`() {
            // given
            // when
            val v1 = randomString(len, isDigit = true)
            // then
            logger.debug { "Random String 결과: $v1" }
        }

        @Test
        fun `Random 문자열 생성 - 3`() {
            // given
            // when
            val v1 = randomString(len, isUpperCase = true)
            // then
            logger.debug { "Random String 결과: $v1" }
        }

        @Test
        fun `Random 문자열 생성 - 4`() {
            // given
            // when
            val v1 = randomString(len, isLowerCase = true)
            // then
            logger.debug { "Random String 결과: $v1" }
        }

        @Test
        fun `Random 문자열 생성 - 5`() {
            // given
            // when
            val v1 = randomString(len, isLetter = true)
            // then
            logger.debug { "Random String 결과: $v1" }
        }

        @Test
        fun `Random 문자열 생성 - 6`() {
            // given
            // when
            for (i in 1..max) {
                val v1 = randomString(len, isHangul = true)
                // then
                logger.debug { "Random String 결과 [$i]: $v1" }
            }
        }
    }

    @Nested
    inner class GenerateDrawItems {
        @Test
        fun `Random Draw Item 1`() {
            // given
            val data = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            // when
            for (i in 1..max) {
                val v1 = data.getRandomItem()
                counter.put(v1)
                // then
                logger.debug { "Random Draw Item 결과 [$i]: $v1" }
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
                logger.debug { "Weighted Random Drawing 결과 [$i]: $v1" }
            }
            logger.debug { "Weighted Random Drawing 결과: Counter[${counter.size()}, ${counter.total()}] : $counter" }
        }
    }

    @Nested
    inner class GenerateOthers {

        @Test
        fun `랜던 문자 만들기`() {
            (1..30).forEach { i ->
                val content = generateRandomString(100)
                println("[$i] $content")
            }
        }
    }
}
