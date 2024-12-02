package com.snc.test.e2e

import com.snc.zero.e2e.E2EEncryption
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

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
}
