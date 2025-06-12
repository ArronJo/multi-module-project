package com.snc.test.core.random

import com.snc.zero.core.random.RandomGenerator
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.Random

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class RandomGeneratorTest : BaseJUnit5Test() {

    @Test
    fun `RandomGenerator random 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.random(12)
        // then
        logger.debug { "RandomGenerator random 1 결과: $v1" }
        //assertEquals(v1, false)

        val v2 = RandomGenerator.random(12, letters = false)
        logger.debug { "RandomGenerator random 2 결과: $v2" }

        val v3 = RandomGenerator.random(12, numbers = false)
        logger.debug { "RandomGenerator random 3 결과: $v3" }

        val v4 = RandomGenerator.random(12, letters = false, numbers = false)
        logger.debug { "RandomGenerator random 4 결과: $v4" }
    }

    @Test
    fun `RandomGenerator randomNumber 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.randomNumber(12)
        // then
        logger.debug { "RandomGenerator randomNumber 1 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `RandomGenerator randomKorean 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.randomKorean(12)
        // then
        logger.debug { "RandomGenerator randomKorean 1 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `RandomGenerator random 커스텀 테스트`() {
        // given
        // when
        val v1 = RandomGenerator.random(
            12,
            0,
            "ABCDEFG".length,
            true,
            false,
            "ABCDEFG".toCharArray(),
            Random()
        )
        // then
        logger.debug { "RandomGenerator random 커스텀 1 결과: $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `RandomGenerator random 한글 테스트`() {
        val v1 = RandomGenerator.random(12, 44032, 55203, true, true)
        logger.debug { "RandomGenerator random 한글 1 결과: $v1" }

        val v2 = RandomGenerator.random(12, 44032, 55203, true, false)
        logger.debug { "RandomGenerator random 한글 2 결과: $v2" }

        val v3 = RandomGenerator.random(12, 44032, 55203, false, false)
        logger.debug { "RandomGenerator random 한글 3 결과: $v3" }

        //assertEquals(v1, false)
    }
}
