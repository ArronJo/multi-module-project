package com.snc.test.crypto.hash.sha3

import com.snc.zero.extensions.random.randomString
import com.snc.zero.extensions.text.toHexString
import com.snc.zero.crypto.hash.sha3.SHA3
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class SHA3Test : BaseJUnit5Test() {

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
    fun `SHA3 - SHA128 테스트 - 미지원 알고리즘, 자체 구현`() {
        // given
        var data = "qwerty"
        val ret = mutableListOf<String>()
        // when
        var v = ""
        var d = -1
        for (i in 1..max) {
            data += randomString(1)
            v = SHA3.sha128(data).toHexString()
            // then
            //logger.debug { "sha128: $v" }
            if (ret.contains(v)) {
                logger.debug { "duplicate [$i]: $v" }
                d = i
                break
            }
            ret.add(v)
        }
        // then
        logger.debug { "SHA3.sha128: [$d] $v" }
        //assertEquals("8dcccaaeab3b51c23c591638492cd5f2a7582b983a895efdfcc54543", v)
    }

    @Test
    fun `SHA3 - SHA224 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA3.sha224(data).toHexString()
        // then
        logger.debug { "SHA3.sha224: $v" }
        assertEquals("13783bdfa4a63b202d9aa1992eccdd68a9fa5e44539273d8c2b797cd", v)
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
        assertEquals("f171cbb35dd1166a20f99b5ad226553e122f3c0f2fe981915fb9e4517aac9038", v1)
        assertEquals("880780e2fd46effa0d5d75b70c6724581364b2a9f70ed87f7ced9a0053593813", v2)
        assertEquals("1f41a7e6823007e5ffb4ec1ddc8a92956bcacf95c2df50669a6a0bbe7ea1a1a7", v3)
        assertEquals("796d31c78846ab6cd55ac0beba39eaf0a64afa6ca376e7d1be862ac8c18c081a", v4)
    }

    @Test
    fun `SHA3 - SHA384 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA3.sha384(data).toHexString()
        // then
        logger.debug { "SHA3.sha384: $v" }
        assertEquals("6729a614db5c5c97920e15501d361ba2f445758012e181af1c6300a99d9a951553fcc4e14aa614db164f61a758c6d6c9", v)
    }

    @Test
    fun `SHA3 - SHA512 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA3.sha512(data).toHexString()
        // then
        logger.debug { "SHA3.sha512: $v" }
        assertEquals("f6d1015e17df348f2d84b3b603648ae4bd14011f4e5b82f885e45587bcad48947d37d64501dc965c0f201171c44b656ee28ed9a5060aea1f2a336025320683d6", v)
    }

    @Test
    fun `SHA3 - SHA EXCEPTION 1-1`() {
        assertThrows(IllegalArgumentException::class.java) {
            val data = "qwerty"
            SHA3.digest(data, 111, "", 0, Charset.defaultCharset()).toHexString()
        }
    }
}
