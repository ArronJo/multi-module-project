package com.snc.test.crypto.hash.sha2

import com.snc.zero.core.extensions.text.toHexString
import com.snc.zero.crypto.hash.sha2.SHA2
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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
    fun `SHA2 - hmacSHA224 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA224(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA224: $v" }
        assertEquals(v, "0548931713e38196e753621c43523ee539ef65b6a35f0f5ac977d7dd")
    }

    @Test
    fun `SHA2 - hmacSHA256 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA256(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA256: $v" }
        assertEquals(v, "ab797fa89ea3bcfa3ae65a5273732adc801019186682c6896dfe8c0355609687")
    }

    @Test
    fun `SHA2 - hmacSHA384 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA384(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA384: $v" }
        assertEquals(
            v,
            "fbe34011f801eae454af427424a7b13b08855dce3e85a7f2befc53e49336305b389dacd3f7c9e2219bbed5fee4d4f9a7"
        )
    }

    @Test
    fun `SHA2 - hmacSHA512 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.hmacSHA512(data, key, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.hmacSHA512: $v" }
        assertEquals(
            v,
            "d822b408db0f7a784df6f2c5f8c29205d99dde87e71871f3f050b6990bac726968488d7d5f75f2f0be0024e1e1e147afceeb8edda291a7e913e685627974a0ef"
        )
    }

    @Test
    fun `SHA2 - SHA224 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha224(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha224: $v" }
        assertEquals(v, "8dcccaaeab3b51c23c591638492cd5f2a7582b983a895efdfcc54543")
    }

    @Test
    fun `SHA2 - SHA256 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha256(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha256: $v" }
        assertEquals(v, "ba467682f4b8c0a1f4f74137cf39598569e13692eea3146b9107ed61888bb499")
    }

    @Test
    fun `SHA2 - SHA384 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha384(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha384: $v" }
        assertEquals(
            v,
            "2e4e60fc809f234c19d3c132106ccbd3e7344e3bb1d1b80f6bce0708ad46782997cf8517eaf74d29cd481428243c6db7"
        )
    }

    @Test
    fun `SHA2 - SHA512 테스트`() {
        // given
        val data = "qwerty"
        // when
        val v = SHA2.sha512(data, salt = "12345").toHexString()
        // then
        logger.debug { "SHA2.sha512: $v" }
        assertEquals(
            v,
            "a2b4dd04be49317f7648ffda4902fc0e8d6e4ec269c5de4acbeda6ef6ec2329066ef12f97b813f0d857f58135d07779dd88a2ad6d93d524d115913dd5fc95190"
        )
    }
}