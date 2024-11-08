package com.snc.test.crypto.cipher

import com.snc.zero.crypto.cipher.Cipher
import com.snc.zero.crypto.cipher.aes.AES
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class CipherTest : BaseJUnit5Test() {

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
    fun `AES default Encrypt Decrypt`() {
        // given
        // when
        val enc = AES.encrypt(data.toByteArray(), key, iv)
        val dec = AES.decrypt(enc, key, iv)
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
}
