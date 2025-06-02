package com.snc.test.extensions.text

import com.snc.zero.extensions.text.filter
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

class FilterExtTest : BaseJUnit5Test() {

    private lateinit var data: String

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        data = "1abc2DEF3ㅁㅇ릐ㅜㅍ치ㅗ감4!@#$%5^&*()6-=_+\\7[]';\":/?.,<>89"
    }

    @Test
    fun `filter empty`() {
        // given
        // when
        val v1 = data.filter()
        // then
        logger.debug { "결과: $v1" }
    }

    @Test
    fun `filter number`() {
        // given
        // when
        val v1 = data.filter(number = true)
        // then
        logger.debug { "number 결과: $v1" }
    }

    @Test
    fun `filter letter`() {
        // given
        // when
        val v1 = data.filter(letter = true)
        // then
        logger.debug { "letter 결과: $v1" }
    }

    @Test
    fun `filter uppercase`() {
        // given
        // when
        val v1 = data.filter(uppercase = true)
        // then
        logger.debug { "uppercase 결과: $v1" }
    }

    @Test
    fun `filter lowercase`() {
        // given
        // when
        val v1 = data.filter(lowercase = true)
        // then
        logger.debug { "lowercase 결과: $v1" }
    }

    @Test
    fun `filter korean`() {
        // given
        // when
        val v1 = data.filter(korean = true)
        // then
        logger.debug { "korean 결과: $v1" }
    }

    @Test
    fun `filter special`() {
        // given
        // when
        val v1 = data.filter(special = true)
        // then
        logger.debug { "special 결과: $v1" }
    }

    @Test
    fun `filter number + lowercase`() {
        // given
        // when
        val v1 = data.filter(number = true, lowercase = true)
        // then
        logger.debug { "number + lowercase 결과: $v1" }
    }

    @Test
    fun `filter number + uppercase`() {
        // given
        // when
        val v1 = data.filter(number = true, uppercase = true)
        // then
        logger.debug { "number + uppercase 결과: $v1" }
    }

    @Test
    fun `filter number + letter`() {
        // given
        // when
        val v1 = data.filter(number = true, letter = true)
        // then
        logger.debug { "number + letter 결과: $v1" }
    }

    @Test
    fun `filter number + korean`() {
        // given
        // when
        val v1 = data.filter(number = true, korean = true)
        // then
        logger.debug { "number + korean 결과: $v1" }
    }

    @Test
    fun `filter number + special`() {
        // given
        // when
        val v1 = data.filter(number = true, special = true)
        // then
        logger.debug { "number + special 결과: $v1" }
    }

    @Test
    fun `filter all`() {
        // given
        // when
        val v1 = data.filter(number = true, letter = true, korean = true, special = true)
        // then
        logger.debug { "all 결과: $v1" }
    }
}
