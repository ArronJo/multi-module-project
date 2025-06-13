package com.snc.test.e2e

import com.snc.zero.e2e.E2EEncryption
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class E2EEncryptionTest : BaseJUnit5Test() {

    @Test
    fun `E2E Encryption 테스트 1`() {
        // 1. 공개키 요청
        val e2e = E2EEncryption()

        // 2. Alice와 Bob의 키 쌍 생성
        val (bobPublicKey, bobPrivateKey) = e2e.generateKeyPair()

        // 2. Alice가 Bob에게 메시지를 보내는 과정
        // 2.1 Alice가 AES 키 생성
        val aesKey = e2e.generateAESKey()

        // 2.2 AES 키를 Bob의 공개키로 암호화
        val encryptedAESKey = e2e.encryptAESKey(aesKey, bobPublicKey)

        // 2.3 메시지를 AES 키로 암호화
        val message = "안녕하세요, Bob! 이것은 비밀 메시지입니다."
        val (encryptedMessage, iv) = e2e.encryptMessage(message, aesKey)

        // 3. Bob이 메시지를 받아서 복호화하는 과정
        // 3.1 자신의 개인키로 AES 키 복호화
        val decryptedAESKey = e2e.decryptAESKey(encryptedAESKey, bobPrivateKey)

        // 3.2 복호화된 AES 키로 메시지 복호화
        val decryptedMessage = e2e.decryptMessage(encryptedMessage, decryptedAESKey, iv)

        logger.debug { "원본 메시지: $message" }
        logger.debug { "복호화된 메시지: $decryptedMessage" }

        assertEquals(message, decryptedMessage)
    }

    private lateinit var e2eEncryption: E2EEncryption
    private lateinit var keyPair: Pair<PublicKey, PrivateKey>
    private lateinit var aesKey: SecretKey

    @BeforeEach
    fun setup() {
        e2eEncryption = E2EEncryption()
        keyPair = e2eEncryption.generateKeyPair()
        aesKey = e2eEncryption.generateAESKey()
    }

    @Test
    fun `generateKeyPair - should return valid RSA key pair`() {
        assertNotNull(keyPair.first)
        assertNotNull(keyPair.second)
        assertEquals("RSA", keyPair.first.algorithm)
        assertEquals("RSA", keyPair.second.algorithm)
    }

    @Test
    fun `generateAESKey - should return 256-bit AES key`() {
        assertNotNull(aesKey)
        assertEquals("AES", aesKey.algorithm)
        assertEquals(32, aesKey.encoded.size) // 256-bit = 32 bytes
    }

    @Test
    fun `encrypt and decrypt AES key - should match original AES key`() {
        val encryptedKey = e2eEncryption.encryptAESKey(aesKey, keyPair.first)
        val decryptedKey = e2eEncryption.decryptAESKey(encryptedKey, keyPair.second)

        assertArrayEquals(aesKey.encoded, decryptedKey.encoded)
    }

    @Test
    fun `encrypt and decrypt message - should return original message`() {
        val message = "Hello, this is a secure message."
        val (encrypted, iv) = e2eEncryption.encryptMessage(message, aesKey)
        val decrypted = e2eEncryption.decryptMessage(encrypted, aesKey, iv)

        assertEquals(message, decrypted)
    }

    @Test
    fun `key to string and back - public and private key should match`() {
        val publicKeyStr = e2eEncryption.keyToString(keyPair.first)
        val privateKeyStr = e2eEncryption.keyToString(keyPair.second)

        val restoredPublic = e2eEncryption.stringToPublicKey(publicKeyStr)
        val restoredPrivate = e2eEncryption.stringToPrivateKey(privateKeyStr)

        assertArrayEquals(keyPair.first.encoded, restoredPublic.encoded)
        assertArrayEquals(keyPair.second.encoded, restoredPrivate.encoded)
    }

    @Test
    fun `end-to-end encryption-decryption - full flow`() {
        val message = "🔐 End-to-end encryption test 메시지 with Unicode 😎"

        // 1. AES 키 암호화 및 복호화 (RSA)
        val encryptedAESKey = e2eEncryption.encryptAESKey(aesKey, keyPair.first)
        val decryptedAESKey = e2eEncryption.decryptAESKey(encryptedAESKey, keyPair.second)

        // 2. 메시지 암호화 및 복호화 (AES)
        val (encryptedMessage, iv) = e2eEncryption.encryptMessage(message, decryptedAESKey)
        val decryptedMessage = e2eEncryption.decryptMessage(encryptedMessage, decryptedAESKey, iv)

        assertEquals(message, decryptedMessage)
    }
}
