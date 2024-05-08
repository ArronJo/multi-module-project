package com.snc.test.crypto.hash.sha3

import com.snc.zero.core.extensions.text.toHexString
import com.snc.zero.crypto.hash.sha3.SHAKE
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class SHAKETest : BaseTest() {

    companion object {
        private var max: Int = 1
        private lateinit var key: String

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            max = 100
            key = "abc"
        }
    }

    @Test
    fun `SHAKE128 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHAKE.shake128(data).toHexString()
        // then
        logger.debug { "SHA3.shake128: $v" }
        assertEquals(v, "b66869ad27654f99e4e620ddf5670ae5")
    }

    @Test
    fun `SHAKE256 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHAKE.shake256(data).toHexString()
        // then
        logger.debug { "SHA3.shake256: $v" }
        assertEquals(v, "36663c4c841a09dc408044d3536fc73e7de7ff542e3853accdf96cc48256d60a")
    }
}