package com.snc.test.identification.faker

import com.snc.zero.identification.faker.Faker
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class FakerTest : BaseJUnit5Test() {

    @Test
    fun `가명 만들기 1`() {
        // given
        val max = 30
        // when
        for (i in 1..max) {
            val r = getRandomInt(0, 1)
            if (r > 1) { logger.debug { "r : $r" } }
            val sex = if (r > 0) "F" else "M"
            val v = Faker.Name.fakeKoreanName(r > 0)
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [$sex] $v" }
        }
    }
}