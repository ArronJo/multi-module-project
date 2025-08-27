package com.snc.test.crypto.cipher

import com.snc.zero.crypto.cipher.Cipher
import com.snc.zero.crypto.cipher.aes.SimpleAES
import com.snc.zero.crypto.cipher.rsa.RSAKeyGen
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.security.SecureRandom

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class CipherTest : BaseJUnit5Test() {

    private val pair = RSAKeyGen.genKeyPair(2048, SecureRandom("".repeat(16).toByteArray()))
    private val pair2 = RSAKeyGen.genKeyPair(2048)

    companion object {
        private lateinit var key: String
        private lateinit var iv: String
        private lateinit var data: String

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            key = "as"
            iv = "va"
            data = "qwerty"
        }
    }

    @Test
    fun `AES+CFB+PKCS5Padding Encrypt Decrypt`() {
        // given
        val transform = "AES/CFB/PKCS5Padding"
        // when
        val enc = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .encrypt(data.toByteArray())
        // then
        logger.debug { "AES encrypt [$transform]: $enc" } // NY8CIRn5Ni6AOxb7g7qtmg==

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `AES+CBC+PKCS5Padding Encrypt Decrypt`() {
        // given
        val transform = "AES/CBC/PKCS5Padding"
        // when
        val enc = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .encrypt(data.toByteArray())
        // then
        logger.debug { "AES encrypt [$transform]: $enc" } // o4dICw2KpRvvWwgVbKj0yA==

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `AES+CTR+NoPadding Encrypt Decrypt`() {
        // given
        val transform = "AES/CTR/NoPadding"
        // when
        val enc = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .encrypt(data.toByteArray())
        // then
        logger.debug { "AES encrypt [$transform]: $enc" } // NY8CIRn5

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `AES+GCM+NoPadding Encrypt Decrypt`() {
        // given
        val transform = "AES/GCM/NoPadding"
        // when
        val enc = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .encrypt(data.toByteArray())
        // then
        logger.debug { "AES encrypt [$transform]: $enc" } // NY8CIRn5

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `RSA Encrypt Decrypt`() {
        val pubHex = RSAKeyGen.toHexString(pair.public)
        val priHex = RSAKeyGen.toHexString(pair.private)
        //logger.debug { "pair.public :${pair.public}" }
        //logger.debug { "pair.public.conv : ${RSAKeyGen.toPublicKey(pubHex)}" }
        //logger.debug { "pair.private: ${pair.private}" }
        //logger.debug { "pair.private.conv: ${RSAKeyGen.toPrivateKey(priHex)}" }

        assertEquals(RSAKeyGen.toPublicKey(pubHex), pair.public)
        assertEquals(RSAKeyGen.toPrivateKey(priHex), pair.private)

        val transform = RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256 // RSAKeyGen.TRANSFORM_RSA_ECB_OAEP
        // given
        val data = "qwerty"
        // when
        val encrypted = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair.public)
            .encrypt(data.toByteArray())
        // then
        logger.debug { "RSA encrypt [$transform]: $encrypted" }

        // given
        // when
        val v = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair.private)
            .decrypt(encrypted)
        // then
        val plainText = String(v)
        logger.debug { "RSA decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `RSA Encrypt Decrypt TRANSFORM - 1`() {
        val transform = RSAKeyGen.TRANSFORM_RSA_ECB_OAEP_SHA256
        val data = "qwerty"
        val encrypted = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair2.public)
            .encrypt(data.toByteArray())
        logger.debug { "RSA encrypt [$transform]: $encrypted" }

        val v = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair2.private)
            .decrypt(encrypted)
        val plainText = String(v)
        logger.debug { "RSA decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `RSA Encrypt Decrypt TRANSFORM - 2`() {
        val transform = RSAKeyGen.TRANSFORM_RSA_ECB_OAEP
        val data = "qwerty"
        val encrypted = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair2.public)
            .encrypt(data.toByteArray())
        logger.debug { "RSA encrypt [$transform]: $encrypted" }

        val v = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair2.private)
            .decrypt(encrypted)
        val plainText = String(v)
        logger.debug { "RSA decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `RSA Encrypt Decrypt TRANSFORM - 3`() {
        val transform = RSAKeyGen.TRANSFORM_RSA_ECB_PKCS1
        val data = "qwerty"
        val encrypted = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair2.public)
            .encrypt(data.toByteArray())
        logger.debug { "RSA encrypt [$transform]: $encrypted" }

        val v = Cipher.with(Cipher.Algo.RSA)
            .transform(transform)
            .key(pair2.private)
            .decrypt(encrypted)
        val plainText = String(v)
        logger.debug { "RSA decrypt [$transform]: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `AES default Encrypt Decrypt`() {
        // given
        // when
        val enc = SimpleAES.encrypt(data.toByteArray(), key, iv)
        val dec = SimpleAES.decrypt(enc, key, iv)
        // then
        val plainText = String(dec)
        logger.debug { "AES default: $plainText" }

        assertEquals(data, plainText)
    }

    @Test
    fun `Cipher 테스트 1-1`() {
        val enc = Cipher.with().algo(Cipher.Algo.AES).key(key, iv).encrypt(data.toByteArray())
        val dec = Cipher.with().algo(Cipher.Algo.AES).key(key, iv).decrypt(enc)
        val plainText = String(dec)
        assertEquals(data, plainText)
    }

    @Test
    fun `Cipher Exception 테스트`() {
        val cipher = Cipher.with().algo(Cipher.Algo.RSA)
        assertThrows(IllegalArgumentException::class.java) {
            cipher.encrypt(data.toByteArray())
        }
        assertThrows(IllegalArgumentException::class.java) {
            cipher.decrypt(data)
        }
    }

    @Test
    fun `Cipher Enum 100프로 테스트 완료를 위한 entries 테스트`() {
        val e = Cipher.Algo.entries.toTypedArray()
        assertEquals(Cipher.Algo.AES, e[0])
        assertEquals(Cipher.Algo.RSA, e[1])
    }
}
