package com.snc.test.crypto.cipher.rsa

import com.snc.zero.crypto.cipher.rsa.RSACipher
import com.snc.zero.crypto.cipher.rsa.RSAKeyGen
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.security.KeyPair

@Suppress("NonAsciiCharacters")
@DisplayName("RSACipher 테스트")
class RSACipherTest {

    private lateinit var keyPair: KeyPair

    @BeforeEach
    fun setup() {
        keyPair = RSAKeyGen.generateKeyPair()
    }

    @DisplayName("기본 암호화/복호화 테스트")
    @Nested
    inner class DefaultEncDecTest {

        @Test
        @DisplayName("RSA 기본 암복호화 테스트")
        fun `RSA 기본 암호화 복호화 테스트`() {
            val origin = "Integration test data".toByteArray()

            val encrypted = RSACipher.encrypt(origin, keyPair.public)
            val decrypted = RSACipher.decrypt(encrypted, keyPair.private)

            assertArrayEquals(origin, decrypted)
        }

        @Test
        @DisplayName("RSA+AES 기본 암복호화 테스트")
        fun `RSA+AES 기본 암호화 복호화 테스트`() {
            val origin = "Integration test data".toByteArray()

            val encrypted = RSACipher.encryptWithAES(origin, keyPair.public)
            val decrypted = RSACipher.decryptWithAES(encrypted, keyPair.private)

            assertArrayEquals(origin, decrypted)
        }
    }
}
