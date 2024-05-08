package com.snc.test.crypto.hash.sha3

import com.snc.zero.core.extensions.text.toHexString
import com.snc.zero.crypto.hash.sha3.SHA3
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class SHA3Test : BaseTest() {

    companion object {
        private var max = 1
        private lateinit var key: String

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            max = 100
            key = "abc"
        }
    }

    @Test
    fun `SHA3 - SHA224 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA3.sha224(data).toHexString()
        // then
        logger.debug { "SHA3.sha224: $v" }
        assertEquals(v, "13783bdfa4a63b202d9aa1992eccdd68a9fa5e44539273d8c2b797cd")
    }

    @Test
    fun `SHA3 - SHA256 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v1 = SHA3.sha256(data).toHexString()
        val v2 = SHA3.sha256(data, salt = "12345").toHexString()
        val v3 = SHA3.sha256(data, iterationCount = 1).toHexString()
        val v4 = SHA3.sha256(data, salt = "12345", iterationCount = 10).toHexString()
        // then
        logger.debug { "SHA3.sha256: $v1" }
        logger.debug { "SHA3.sha256: $v2" }
        logger.debug { "SHA3.sha256: $v3" }
        logger.debug { "SHA3.sha256: $v4" }
        assertEquals(v1, "f171cbb35dd1166a20f99b5ad226553e122f3c0f2fe981915fb9e4517aac9038")
        assertEquals(v2, "880780e2fd46effa0d5d75b70c6724581364b2a9f70ed87f7ced9a0053593813")
        assertEquals(v3, "1f41a7e6823007e5ffb4ec1ddc8a92956bcacf95c2df50669a6a0bbe7ea1a1a7")
        assertEquals(v4, "796d31c78846ab6cd55ac0beba39eaf0a64afa6ca376e7d1be862ac8c18c081a")
    }

    @Test
    fun `SHA3 - SHA384 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA3.sha384(data).toHexString()
        // then
        logger.debug { "SHA3.sha384: $v" }
        assertEquals(
            v,
            "6729a614db5c5c97920e15501d361ba2f445758012e181af1c6300a99d9a951553fcc4e14aa614db164f61a758c6d6c9"
        )
    }

    @Test
    fun `SHA3 - SHA512 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA3.sha512(data).toHexString()
        // then
        logger.debug { "SHA3.sha512: $v" }
        assertEquals(
            v,
            "f6d1015e17df348f2d84b3b603648ae4bd14011f4e5b82f885e45587bcad48947d37d64501dc965c0f201171c44b656ee28ed9a5060aea1f2a336025320683d6"
        )
    }
}