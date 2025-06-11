package com.snc.test.crypto.hash.sha2

import com.snc.zero.extension.random.randomString
import com.snc.zero.extension.text.toHexString
import com.snc.zero.crypto.hash.sha2.SHA2
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class SHA2Test : BaseJUnit5Test() {

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
    fun `SHA2 - hmacSHA224 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA224(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA224: $v" }
        assertEquals("0548931713e38196e753621c43523ee539ef65b6a35f0f5ac977d7dd", v)
    }

    @Test
    fun `SHA2 - hmacSHA224 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA224(data, key).toHexString()
        // then
        logger.debug { "SHA2.hmacSHA224: $v" }
        assertEquals("ec85c6b61056b438c0bbd95ff021543a3bd85c4572562f708ffcb8ee", v)
    }

    @Test
    fun `SHA2 - hmacSHA256 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA256(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA256: $v" }
        assertEquals("ab797fa89ea3bcfa3ae65a5273732adc801019186682c6896dfe8c0355609687", v)
    }

    @Test
    fun `SHA2 - hmacSHA256 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA256(data, key).toHexString()
        // then
        logger.debug { "SHA2.hmacSHA256: $v" }
        assertEquals("1eea2ffaeebbc5ab3946e1a15ffc7f13129a54571195952e995c3785a4a5cf89", v)
    }

    @Test
    fun `SHA2 - hmacSHA384 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA384(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA384: $v" }
        assertEquals("fbe34011f801eae454af427424a7b13b08855dce3e85a7f2befc53e49336305b389dacd3f7c9e2219bbed5fee4d4f9a7", v)
    }

    @Test
    fun `SHA2 - hmacSHA384 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA384(data, key).toHexString()
        // then
        logger.debug { "SHA2.hmacSHA384: $v" }
        assertEquals("5a497b34497187f764f94a5ddd6e16b5b7be924f2d32857cd29944a02fff0b0514fb83387ec85212be2a5ddf1e18993c", v)
    }

    @Test
    fun `SHA2 - hmacSHA512 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA512(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA512: $v" }
        assertEquals("d822b408db0f7a784df6f2c5f8c29205d99dde87e71871f3f050b6990bac726968488d7d5f75f2f0be0024e1e1e147afceeb8edda291a7e913e685627974a0ef", v)
    }

    @Test
    fun `SHA2 - hmacSHA512 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA512(data, key).toHexString()
        // then
        logger.debug { "SHA2.hmacSHA512: $v" }
        assertEquals("7979a814a63df67c03d308c1a06e4e4a311589ec5a9eb1872ca401555b7ddf1bad6002022580a63efd8c1f1bc207ab01ff37e13b7d60503ca89bcef7ba42f202", v)
    }

    @Test
    fun `SHA2 - SHA128 테스트 - 미지원 알고리즘, 자체 구현`() {
        // given
        var data = "qwerty"
        val ret = mutableListOf<String>()
        // when
        var v = ""
        var d = -1
        for (i in 1..max) {
            data += randomString(1)
            v = SHA2.sha128(data, salt = "12345").toHexString()
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
        logger.debug { "SHA2.sha128: [$d] $v" }
        //assertEquals("8dcccaaeab3b51c23c591638492cd5f2a7582b983a895efdfcc54543", v)
    }

    @Test
    fun `SHA2 - SHA224 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha224(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha224: $v" }
        assertEquals("8dcccaaeab3b51c23c591638492cd5f2a7582b983a895efdfcc54543", v)
    }

    @Test
    fun `SHA2 - SHA224 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha224(data).toHexString()
        // then
        logger.debug { "SHA2.sha224: $v" }
        assertEquals("5154aaa49392fb275ce7e12a7d3e00901cf9cf3ab10491673f97322f", v)
    }

    @Test
    fun `SHA2 - SHA256 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha256(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha256: $v" }
        assertEquals("ba467682f4b8c0a1f4f74137cf39598569e13692eea3146b9107ed61888bb499", v)
    }

    @Test
    fun `SHA2 - SHA256 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha256(data).toHexString()
        // then
        logger.debug { "SHA2.sha256: $v" }
        assertEquals("65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5", v)
    }

    @Test
    fun `SHA2 - SHA384 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha384(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha384: $v" }
        assertEquals("2e4e60fc809f234c19d3c132106ccbd3e7344e3bb1d1b80f6bce0708ad46782997cf8517eaf74d29cd481428243c6db7", v)
    }

    @Test
    fun `SHA2 - SHA384 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha384(data).toHexString()
        // then
        logger.debug { "SHA2.sha384: $v" }
        assertEquals("1ab60e110d41a9aac5e30d086c490819bfe3461b38c76b9602fe9686aa0aa3d28c63c96a1019e3788c40a14f4292e50f", v)
    }

    @Test
    fun `SHA2 - SHA512 테스트 1-1`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha512(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha512: $v" }
        assertEquals("a2b4dd04be49317f7648ffda4902fc0e8d6e4ec269c5de4acbeda6ef6ec2329066ef12f97b813f0d857f58135d07779dd88a2ad6d93d524d115913dd5fc95190", v)
    }

    @Test
    fun `SHA2 - SHA512 테스트 1-2`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha512(data, salt = "12345", iterationCount = 1).toHexString()
        // then
        logger.debug { "SHA2.sha512: $v" }
        assertEquals("ae078302fd6ee3a2899253080b27438fcc6ffc55c8946661d83e3a51a2139f54567b7a66909141ba8ca1befc1648682b623ace62529e79731170d0b67d2e709e", v)
    }

    @Test
    fun `SHA2 - SHA512 테스트 1-3`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha512(data).toHexString()
        // then
        logger.debug { "SHA2.sha512: $v" }
        assertEquals("0dd3e512642c97ca3f747f9a76e374fbda73f9292823c0313be9d78add7cdd8f72235af0c553dd26797e78e1854edee0ae002f8aba074b066dfce1af114e32f8", v)
    }

    @Test
    fun `SHA2 - SHA EXCEPTION 1-1`() {
        assertThrows(IllegalArgumentException::class.java) {
            val data = "qwerty"
            SHA2.digest(data, 111, "", 0, Charset.defaultCharset()).toHexString()
        }
    }
}
