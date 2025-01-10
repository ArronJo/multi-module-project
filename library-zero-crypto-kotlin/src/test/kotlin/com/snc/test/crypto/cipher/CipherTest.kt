package com.snc.test.crypto.cipher

import com.snc.zero.crypto.cipher.Cipher
import com.snc.zero.crypto.cipher.aes.AES
import com.snc.zero.crypto.cipher.rsa.RSA
import com.snc.zero.crypto.cipher.rsa.RSAException
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.crypto.extensions.cipher.encryptRSA
import com.snc.zero.crypto.extensions.cipher.decryptRSA
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

    @Test
    fun `Cipher RSA 테스트 1-1`() {
        // 새로운 RSA 키쌍 생성
        val rsa = RSA()

        // 암호화할 데이터
        val originalText = "안녕하세요! 이것은 테스트 메시지입니다."

        // 방법 1: 일반적인 방식으로 암호화/복호화
        val encrypted = rsa.encrypt(originalText)
        println("암호화된 텍스트: $encrypted")

        val decrypted = rsa.decrypt(encrypted)
        println("복호화된 텍스트: $decrypted")

        // 방법 2: 확장 함수를 사용한 암호화/복호화
        val encryptedWithExtension = originalText.encryptRSA(rsa)
        val decryptedWithExtension = encryptedWithExtension.decryptRSA(rsa)

        // 키 저장을 위한 내보내기
        val publicKeyStr = rsa.exportPublicKey()
        val privateKeyStr = rsa.exportPrivateKey()

        // 저장된 키로 새 인스턴스 생성
        val importedPublicKey = RSA.importPublicKey(publicKeyStr)
        val importedPrivateKey = RSA.importPrivateKey(privateKeyStr)
        val rsa2 = RSA(importedPublicKey, importedPrivateKey)

        // try-catch를 사용한 예외 처리
        try {
            val encryptedText = originalText.encryptRSA(rsa2)
            val decryptedText = encryptedText.decryptRSA(rsa2)
            println("성공적으로 암호화/복호화 되었습니다. $decryptedText")
        } catch (e: RSAException) {
            println("RSA 처리 중 오류 발생: ${e.message}")
        }
    }
}
