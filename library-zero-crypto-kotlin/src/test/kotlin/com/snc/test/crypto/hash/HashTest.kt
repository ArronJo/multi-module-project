package com.snc.test.crypto.hash

import com.snc.zero.extension.text.toHexString
import com.snc.zero.crypto.hash.Hash
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HashTest : BaseJUnit5Test() {

    companion object {

        private const val MSG_EMPTY_KEY = "Empty key"

        private lateinit var key: String

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            key = "abc"
        }
    }

    @Test
    fun `Hash-Algo-HmacSHA224 테스트 1`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.HmacSHA224).key(key).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.HmacSHA224: $v" }
        assertEquals("ec85c6b61056b438c0bbd95ff021543a3bd85c4572562f708ffcb8ee", v)
    }

    @Test
    fun `Hash-Algo-HmacSHA224 테스트 2`() {
        // given
        val data = "qwerty"
        // when
        val e = assertThrows(
            IllegalArgumentException::class.java
        ) {
            Hash.with(Hash.Algo.HmacSHA224).key("").digest(data).toHexString()
        }
        // then
        assertEquals(MSG_EMPTY_KEY, e.message)
    }

    @Test
    fun `Hash-Algo-HmacSHA256 테스트 1`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.HmacSHA256).key(key).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.HmacSHA256: $v" }
        assertEquals("1eea2ffaeebbc5ab3946e1a15ffc7f13129a54571195952e995c3785a4a5cf89", v)
    }

    @Test
    fun `Hash-Algo-HmacSHA256 테스트 2`() {
        // given
        val data = "qwerty"
        // when
        val e = assertThrows(
            IllegalArgumentException::class.java
        ) {
            Hash.with(Hash.Algo.HmacSHA256).key("").digest(data).toHexString()
        }
        // then
        assertEquals(MSG_EMPTY_KEY, e.message)
    }

    @Test
    fun `Hash-Algo-HmacSHA384 테스트 1`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.HmacSHA384).key(key).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.HmacSHA384: $v" }
        assertEquals("5a497b34497187f764f94a5ddd6e16b5b7be924f2d32857cd29944a02fff0b0514fb83387ec85212be2a5ddf1e18993c", v)
    }

    @Test
    fun `Hash-Algo-HmacSHA384 테스트 2`() {
        // given
        val data = "qwerty"
        // when
        val e = assertThrows(
            IllegalArgumentException::class.java
        ) {
            Hash.with(Hash.Algo.HmacSHA384).key("").digest(data).toHexString()
        }
        // then
        assertEquals(MSG_EMPTY_KEY, e.message)
    }

    @Test
    fun `Hash-Algo-HmacSHA512 테스트 1`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.HmacSHA512).key(key).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.HmacSHA512: $v" }
        assertEquals("7979a814a63df67c03d308c1a06e4e4a311589ec5a9eb1872ca401555b7ddf1bad6002022580a63efd8c1f1bc207ab01ff37e13b7d60503ca89bcef7ba42f202", v)
    }

    @Test
    fun `Hash-Algo-HmacSHA512 테스트 2`() {
        // given
        val data = "qwerty"
        // when
        val e = assertThrows(
            IllegalArgumentException::class.java
        ) {
            Hash.with(Hash.Algo.HmacSHA512).key("").digest(data).toHexString()
        }
        // then
        assertEquals(MSG_EMPTY_KEY, e.message)
    }

    @Test
    fun `Hash-Algo-SHA224 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA224).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA224: $v" }
        assertEquals("5154aaa49392fb275ce7e12a7d3e00901cf9cf3ab10491673f97322f", v)
    }

    @Test
    fun `Hash-Algo-SHA256 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA256).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA256: $v" }
        assertEquals("65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5", v)
    }

    @Test
    fun `Hash-Algo-SHA384 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA384).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA384: $v" }
        assertEquals("1ab60e110d41a9aac5e30d086c490819bfe3461b38c76b9602fe9686aa0aa3d28c63c96a1019e3788c40a14f4292e50f", v)
    }

    @Test
    fun `Hash-Algo-SHA512 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA512).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA512: $v" }
        assertEquals("0dd3e512642c97ca3f747f9a76e374fbda73f9292823c0313be9d78add7cdd8f72235af0c553dd26797e78e1854edee0ae002f8aba074b066dfce1af114e32f8", v)
    }

    @Test
    fun `Hash-Algo-SHA-3 224 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA3_224).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA3_224: $v" }
        assertEquals("13783bdfa4a63b202d9aa1992eccdd68a9fa5e44539273d8c2b797cd", v)
    }

    @Test
    fun `Hash-Algo-SHA-3 256 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA3_256).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA3_256: $v" }
        assertEquals("f171cbb35dd1166a20f99b5ad226553e122f3c0f2fe981915fb9e4517aac9038", v)
    }

    @Test
    fun `Hash-Algo-SHA-3 384 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA3_384).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA3_384: $v" }
        assertEquals("6729a614db5c5c97920e15501d361ba2f445758012e181af1c6300a99d9a951553fcc4e14aa614db164f61a758c6d6c9", v)
    }

    @Test
    fun `Hash-Algo-SHA-3 512 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHA3_512).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHA3_512: $v" }
        assertEquals("f6d1015e17df348f2d84b3b603648ae4bd14011f4e5b82f885e45587bcad48947d37d64501dc965c0f201171c44b656ee28ed9a5060aea1f2a336025320683d6", v)
    }

    @Test
    fun `Hash-Algo-SHA-3 SHAKE128 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHAKE128).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHAKE128: $v" }
        assertEquals("b66869ad27654f99e4e620ddf5670ae5", v)
    }

    @Test
    fun `Hash-Algo-SHA-3 SHAKE256 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = Hash.with(Hash.Algo.SHAKE256).digest(data).toHexString()
        // then
        logger.debug { "Hash.Algo.SHAKE256: $v" }
        assertEquals("36663c4c841a09dc408044d3536fc73e7de7ff542e3853accdf96cc48256d60a", v)
    }

    @Test
    fun `Hash 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v1 = Hash.with().digest(data).toHexString()
        val v2 = Hash.with(Hash.Algo.SHA256).digest(data).toHexString()
        // then
        assertEquals("65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5", v1)
        assertEquals("65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5", v2)
    }

    @Test
    fun `Hash 테스트 1-2`() {
        // given
        // when
        val v = Hash.Algo.valueOf("SHA224")
        // then
        logger.debug { "Hash: $v" }
        assertEquals(Hash.Algo.SHA224, v)
    }

    @Test
    fun `Hash 테스트 1-3`() {
        // given
        // when
        val v1 = Hash.Algo.entries.toTypedArray()
        // then
        assertNotEquals(Hash.Algo.SHA224, v1[0])
    }
}
