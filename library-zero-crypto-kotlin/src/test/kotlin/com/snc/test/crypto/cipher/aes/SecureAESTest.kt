package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.SecureAES
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SecureAESTest : BaseJUnit5Test() {

    @Test
    fun `encrypt and decrypt should return original plaintext`() {
        val key = SecureAES.generateAESKey()
        val plaintext = "Hello, Kotlin AES-GCM!".toByteArray(Charsets.UTF_8)

        val encrypted = SecureAES.encrypt(plaintext, key)
        val decrypted = SecureAES.decrypt(encrypted, key)

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `decrypt with wrong key should throw exception`() {
        val key1 = SecureAES.generateAESKey()
        val key2 = SecureAES.generateAESKey()
        val plaintext = "Sensitive data".toByteArray(Charsets.UTF_8)

        val encrypted = SecureAES.encrypt(plaintext, key1)

        assertThrows(Exception::class.java) {
            SecureAES.decrypt(encrypted, key2)
        }
    }

    @Test
    fun `encrypted data should contain iv and ciphertext`() {
        val key = SecureAES.generateAESKey()
        val plaintext = "Short text".toByteArray(Charsets.UTF_8)

        val encrypted = SecureAES.encrypt(plaintext, key)

        // IV는 12바이트여야 하며 암호문은 최소 몇 바이트 이상
        assertTrue(encrypted.size > 12)
    }

    @Test
    fun `null plaintext should throw exception`() {
        val key = SecureAES.generateAESKey()

        assertThrows(Exception::class.java) {
            SecureAES.encrypt(null, key)
        }
    }

    @Test
    fun `null key should throw exception`() {
        val plaintext = "Some data".toByteArray(Charsets.UTF_8)

        assertThrows(Exception::class.java) {
            SecureAES.encrypt(plaintext, null)
        }
    }
}
