package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AESGCMTest : BaseJUnit5Test() {

    @Test
    fun `encrypt and decrypt should return original plaintext`() {
        val key = AESGCM.generateAesKey()
        val plaintext = "Hello, Kotlin AES-GCM!".toByteArray(Charsets.UTF_8)

        val encrypted = AESGCM.encrypt(plaintext, key)
        val decrypted = AESGCM.decrypt(encrypted, key)

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `decrypt with wrong key should throw exception`() {
        val key1 = AESGCM.generateAesKey(256)
        val key2 = AESGCM.generateAesKey(256)
        val plaintext = "Sensitive data".toByteArray(Charsets.UTF_8)

        val encrypted = AESGCM.encrypt(plaintext, key1)

        assertThrows(Exception::class.java) {
            AESGCM.decrypt(encrypted, key2)
        }
    }

    @Test
    fun `encrypted data should contain iv and ciphertext`() {
        val key = AESGCM.generateAesKey()
        val plaintext = "Short text".toByteArray(Charsets.UTF_8)

        val encrypted = AESGCM.encrypt(plaintext, key)

        // IV는 12바이트여야 하며 암호문은 최소 몇 바이트 이상
        assertTrue(encrypted.size > 12)
    }
}
