package com.snc.test.crypto.cipher

import com.snc.zero.crypto.cipher.Cipher
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class CipherTest : BaseJUnit5Test() {

    private val key = "as"
    private val iv = "va"
    private val data = "qwerty"

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
        logger.debug { "AES encrypt [$transform]: $enc" }   // NY8CIRn5Ni6AOxb7g7qtmg==

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(plainText, data)
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
        logger.debug { "AES encrypt [$transform]: $enc" }   // o4dICw2KpRvvWwgVbKj0yA==

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(plainText, data)
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
        logger.debug { "AES encrypt [$transform]: $enc" }   // NY8CIRn5

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(plainText, data)
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
        logger.debug { "AES encrypt [$transform]: $enc" }   // NY8CIRn5

        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .transform(transform)
            .decrypt(enc)

        val plainText = String(v)
        logger.debug { "AES decrypt [$transform]: $plainText" }

        assertEquals(plainText, data)
    }

}