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
            val v1 = Faker.Name.fakeKoreanName(true)
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [F] $v1" }
        }
    }

    @Test
    fun `가명 만들기 2`() {
        // given
        val max = 30
        // when
        for (i in 1..max) {
            val v1 = Faker.Name.fakeKoreanName(false)
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [M] $v1" }
        }
    }

    @Test
    fun `가명 만들기 랜덤 1`() {
        // given
        val max = 30
        // when
        for (i in 1..max) {
            val r = getRandomInt(0, 1)
            if (r > 1) { logger.debug { "r : $r" } }
            val sex = if (r > 0) "F" else "M"
            val provider = if (r > 0) Faker.ProviderType.KOREAN else Faker.ProviderType.ENGLISH
            val v1 = Faker.Name.fake(type = provider, female = r > 0)
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [$sex] ${v1.joinToString(separator = "")}" }
        }
    }

    @Test
    fun `가명 만들기 Provider 1`() {
        // given
        val provider = Faker.ProviderType.ENGLISH
        // when
        val v1 = Faker.Name.fake(provider, female = true)
        logger.debug { "fake name: ${v1.joinToString(separator = "")}" }
    }

    @Test
    fun `가명 만들기 Provider 2`() {
        // given
        val provider = Faker.ProviderType.KOREAN
        // when
        val v1 = Faker.Name.fake(provider, female = false)
        logger.debug { "fake name: ${v1.joinToString(separator = "")}" }
    }
}