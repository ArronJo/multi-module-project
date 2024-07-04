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
        var v1 = ""
        for (i in 1..max) {
            v1 = Faker.Name.fakeKoreanName(true)
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [F] $v1" }
        }
        assertNotEquals("", v1)
    }

    @Test
    fun `가명 만들기 2`() {
        // given
        val max = 30
        // when
        var v1 = ""
        for (i in 1..max) {
            v1 = Faker.Name.fakeKoreanName(false)
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [M] $v1" }
        }
        assertNotEquals("", v1)
    }

    @Test
    fun `가명 만들기 3`() {
        // given
        val max = 30
        // when
        var v1 = ""
        for (i in 1..max) {
            v1 = Faker.Name.fakeKoreanName()
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [M] $v1" }
        }
        assertNotEquals("", v1)
    }

    @Test
    fun `가명 만들기 랜덤 1`() {
        // given
        val max = 30
        // when
        var v1: Array<String> = arrayOf()
        for (i in 1..max) {
            val r = getRandomInt(0, 1)
            if (r > 1) { logger.debug { "r : $r" } }
            val sex = if (r > 0) "F" else "M"
            val provider = if (r > 0) Faker.ProviderType.KOREAN else Faker.ProviderType.ENGLISH
            v1 = Faker.Name.fake(type = provider, female = r > 0)
            // then
            logger.debug { "fake name: ${i.toString().padStart(2, '0')} : [$sex] ${v1.joinToString(separator = "")}" }
        }
        assertNotEquals("", v1.joinToString(separator = ""))
    }

    @Test
    fun `가명 만들기 Provider 1`() {
        // given
        val provider = Faker.ProviderType.ENGLISH
        // when
        val v1 = Faker.Name.fake(provider, female = true)
        logger.debug { "fake name: ${v1.joinToString(separator = "")}" }
        assertNotEquals("", v1.joinToString(separator = ""))
    }

    @Test
    fun `가명 만들기 Provider 2`() {
        // given
        val provider = Faker.ProviderType.KOREAN
        // when
        val v1 = Faker.Name.fake(provider, female = false)
        logger.debug { "fake name: ${v1.joinToString(separator = "")}" }
        assertNotEquals("", v1.joinToString(separator = ""))
    }

    @Test
    fun `가명 만들기 Provider 3`() {
        // given
        val provider = Faker.ProviderType.KOREAN
        // when
        val v1 = Faker.Name.fake(provider)
        logger.debug { "fake name: ${v1.joinToString(separator = "")}" }
        assertNotEquals("", v1.joinToString(separator = ""))
    }

    @Test
    fun `Enum 테스트 1-1`() {
        // given
        val v = Faker.ProviderType.KOREAN
        // when
        assertEquals("KOREAN", v.name)
    }

    @Test
    fun `Enum 테스트 1-2`() {
        // given
        val v = Faker.ProviderType.valueOf("KOREAN")
        // when
        assertEquals(Faker.ProviderType.KOREAN, v)
    }

    @Test
    fun `Enum 테스트 2-1`() {
        // given
        val v = Faker.ProviderType.entries.toTypedArray()
        // when
        assertEquals(Faker.ProviderType.KOREAN, v[0])
        assertEquals(Faker.ProviderType.ENGLISH, v[1])
    }

    @Test
    fun `Enum 테스트 2-2`() {
        // given
        val v = Faker.ProviderType.entries
        // when
        assertEquals(Faker.ProviderType.KOREAN, v[0])
        assertEquals(Faker.ProviderType.ENGLISH, v[1])
    }

    @Test
    fun `Enum 테스트 2-3`() {
        // given
        val v = Faker.ProviderType.values()
        // when
        assertEquals(Faker.ProviderType.KOREAN, v[0])
        assertEquals(Faker.ProviderType.ENGLISH, v[1])
    }

    @Test
    fun `Enum 테스트 3`() {
        assertEquals(Faker.checkEnum(Faker.ProviderType.KOREAN), true)
        assertEquals(Faker.checkEnum(Faker.ProviderType.ENGLISH), false)
    }
}