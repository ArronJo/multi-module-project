package com.snc.test.crypto.cipher

import com.snc.zero.crypto.cipher.Cipher
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class CipherTest : BaseTest() {

    private val key = "as"
    private val iv = "va"

    @Test
    fun `AES Encrypt`() {
        // given
        val data = "qwerty"
        // when
        val v = Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .encrypt(data.toByteArray())
        // then
        logger.debug { "encrypt: $v" }
        assertEquals(v, "NY8CIRn5Ni6AOxb7g7qtmg==")
    }

    @Test
    fun `AES Decrypt`() {
        // given
        val data = "NY8CIRn5Ni6AOxb7g7qtmg=="
        // when
        val v = String(Cipher.with(Cipher.Algo.AES)
            .key(key, iv)
            .decrypt(data))
        // then
        logger.debug { "decrypt: $v" }
        assertEquals(v, "qwerty")
    }
}