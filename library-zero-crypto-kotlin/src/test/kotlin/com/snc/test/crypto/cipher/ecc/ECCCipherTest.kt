package com.snc.test.crypto.cipher.ecc

import com.snc.zero.crypto.cipher.ecc.ECCCipher
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * ECC + AES-GCM 암복호화 TDD 테스트
 */
@Suppress("NonAsciiCharacters")
class ECCCipherTest {

    private lateinit var keyPair: KeyPair

    @BeforeEach
    fun setup() {
        keyPair = ECCCipher.generateKeyPair()
    }

    @Nested
    inner class ECCKeyGenerationTest {
        @Test
        fun `키쌍 생성 검증`() {
            val keyPair = ECCCipher.generateKeyPair()
            assertNotNull(keyPair.private)
            assertNotNull(keyPair.public)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class DefaultEncDec {
        private val origin = "Hello ECC Crypto Test".toByteArray()
        private var encrypted: ECCCipher.EncryptedData? = null
        private val keyPair = ECCCipher.generateKeyPair()

        /**
         * 정상 암복호화 테스트
         */
        @Test
        @Order(1)
        fun `정상 분리 암호화 테스트`() {
            println("정상 분리 암호화 테스트")
            this.encrypted = ECCCipher.encrypt(this.origin, this.keyPair.public)
            println(this.encrypted)
        }

        /**
         * 정상 복복호화 테스트
         */
        @Test
        @Order(2)
        fun `정상 분리 복호화 테스트`() {
            println("정상 분리 복호화 테스트")
            if (null == this.encrypted) throw Exception("암호화 값이 없음")

            this.encrypted?.let {
                val decrypted = ECCCipher.decrypt(it, this.keyPair.private)
                assertArrayEquals(this.origin, decrypted)
            }
        }
    }

    @Nested
    inner class DefaultEncDec2 {

        /**
         * 정상 암복호화 테스트
         */
        @Test
        fun `encrypt and decrypt should return original data`() {
            val origin = "Hello ECC Crypto Test".toByteArray()

            val encrypted = ECCCipher.encrypt(origin, keyPair.public)
            val decrypted = ECCCipher.decrypt(encrypted, keyPair.private)

            assertArrayEquals(origin, decrypted)
        }
    }

    /**
     * 매번 다른 암호문이 생성되는지 테스트 (Ephemeral Key 검증)
     */
    @Test
    fun `encrypt should generate different ciphertext every time`() {
        val origin = "Same Message".toByteArray()

        val enc1 = ECCCipher.encrypt(origin, keyPair.public)
        val enc2 = ECCCipher.encrypt(origin, keyPair.public)

        assertNotEquals(
            enc1.cipherText.contentToString(),
            enc2.cipherText.contentToString()
        )
    }

    /**
     * 다른 개인키로 복호화 시 실패 테스트
     */
    @Test
    fun `decrypt with wrong private key should fail`() {
        val origin = "Secret Data".toByteArray()

        val otherKeyPair = generateKeyPair()

        val encrypted = ECCCipher.encrypt(origin, keyPair.public)

        assertThrows(Exception::class.java) {
            ECCCipher.decrypt(encrypted, otherKeyPair.private)
        }
    }

    /**
     * 공개키 직렬화/역직렬화 테스트
     */
    @Test
    fun `public key serialize and restore test`() {
        val pubKey = keyPair.public
        val encoded = pubKey.encoded
        val restored = restorePublicKey(encoded)
        val origin = "Key Restore Test".toByteArray()

        val encrypted = ECCCipher.encrypt(origin, restored)
        val decrypted = ECCCipher.decrypt(encrypted, keyPair.private)

        assertArrayEquals(origin, decrypted)
    }

    /**
     * 개인키 직렬화/역직렬화 테스트
     */
    @Test
    fun `private key serialize and restore test`() {
        val privKey = keyPair.private
        val encoded = privKey.encoded
        val restored = restorePrivateKey(encoded)
        val origin = "Private Key Test".toByteArray()

        val encrypted = ECCCipher.encrypt(origin, keyPair.public)
        val decrypted = ECCCipher.decrypt(encrypted, restored)

        assertArrayEquals(origin, decrypted)
    }

    // ================== Utils ==================

    private fun generateKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        return kpg.generateKeyPair()
    }

    private fun restorePublicKey(encoded: ByteArray): PublicKey {
        val kf = KeyFactory.getInstance("EC")
        return kf.generatePublic(X509EncodedKeySpec(encoded))
    }

    private fun restorePrivateKey(encoded: ByteArray): PrivateKey {
        val kf = KeyFactory.getInstance("EC")
        return kf.generatePrivate(PKCS8EncodedKeySpec(encoded))
    }
}
