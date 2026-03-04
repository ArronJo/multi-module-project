package com.snc.zero.crypto.cipher.ecc

import java.nio.ByteBuffer
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Hybrid Encryption: ECDH + HKDF + AES-GCM
 *
 * 1. ECDH   → 공유키 생성 (Ephemeral Key로 Forward Secrecy 보장)
 * 2. HKDF   → AES Key 파생 (RFC 5869)
 * 3. AES-GCM → 데이터 암호화 (인증 포함)
 *
 * ---
 * 용도:
 *   ✅ 두 당사자가 안전하게 공유 비밀키(세션키) 생성
 *   ✅ TLS 핸드셰이크 기반
 *   ✅ E2E 암호화 (Signal, WhatsApp 등)
 *
 * 흐름:
 *   Alice 공개키 → Bob 에게 전달
 *   Bob 공개키   → Alice 에게 전달
 *   각자 상대방 공개키 + 자신의 개인키 → 동일한 공유비밀 도출
 */
object ECCCipher {

    private const val CURVE = "secp256r1"
    private const val AES_KEY_LENGTH = 32
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_BITS = 128
    private const val HKDF_INFO = "ECC-AES-KEY"

    private val secureRandom = SecureRandom()

    // ──────────────────────────────────────────────
    // EncryptedData
    // ──────────────────────────────────────────────
    class EncryptedData(
        publicKey: ByteArray,
        iv: ByteArray,
        cipherText: ByteArray
    ) {
        val publicKey = publicKey.clone()
        val iv = iv.clone()
        val cipherText = cipherText.clone()

        /**
         * 직렬화: [4byte: pubKeyLen][pubKey][12byte: iv][cipherText]
         */
        fun toByteArray(): ByteArray {
            return ByteBuffer.allocate(4 + publicKey.size + GCM_IV_LENGTH + cipherText.size).apply {
                putInt(publicKey.size)
                put(publicKey)
                put(iv)
                put(cipherText)
            }.array()
        }

        override fun toString(): String {
            return "EncryptedData(" +
                "publicKey=${publicKey.toHexString()}, " +
                "iv=${iv.toHexString()}, " +
                "cipherText=${cipherText.toHexString()})"
        }

        private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

        companion object {
            /**
             * 역직렬화: toByteArray() 의 역연산
             */
            fun fromByteArray(data: ByteArray): EncryptedData {
                val buf = ByteBuffer.wrap(data)
                val pubKeyLen = buf.int
                val pubKey = ByteArray(pubKeyLen).also { buf.get(it) }
                val iv = ByteArray(GCM_IV_LENGTH).also { buf.get(it) }
                val cipher = ByteArray(buf.remaining()).also { buf.get(it) }
                return EncryptedData(pubKey, iv, cipher)
            }
        }
    }

    // ──────────────────────────────────────────────
    // Key Generation
    // ──────────────────────────────────────────────
    fun generateKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec(CURVE))
        return kpg.generateKeyPair()
    }

    // ──────────────────────────────────────────────
    // Encrypt
    // ──────────────────────────────────────────────
    fun encrypt(data: ByteArray, peerPublicKey: PublicKey): EncryptedData {
        // 1. Ephemeral Key 생성 (Forward Secrecy)
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec(CURVE))
        val ephKey = kpg.generateKeyPair()

        // 2. ECDH → 공유 비밀키
        val secret = ecdh(ephKey.private, peerPublicKey)

        // 3. HKDF → AES-256 Key
        val aesKey = deriveAesKey(secret)

        // 4. AES-GCM 암호화
        val iv = ByteArray(GCM_IV_LENGTH).also { secureRandom.nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(aesKey, "AES"), GCMParameterSpec(GCM_TAG_BITS, iv))

        return EncryptedData(
            publicKey = ephKey.public.encoded,
            iv = iv,
            cipherText = cipher.doFinal(data)
        )
    }

    // ──────────────────────────────────────────────
    // Decrypt
    // ──────────────────────────────────────────────
    fun decrypt(enc: EncryptedData, myPrivateKey: PrivateKey): ByteArray {
        // 1. Ephemeral 공개키 복원
        val ephPublicKey = KeyFactory.getInstance("EC")
            .generatePublic(X509EncodedKeySpec(enc.publicKey))

        // 2. ECDH → 공유 비밀키
        val secret = ecdh(myPrivateKey, ephPublicKey)

        // 3. HKDF → AES-256 Key
        val aesKey = deriveAesKey(secret)

        // 4. AES-GCM 복호화
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(aesKey, "AES"), GCMParameterSpec(GCM_TAG_BITS, enc.iv))

        return cipher.doFinal(enc.cipherText)
    }

    // ──────────────────────────────────────────────
    // Private
    // ──────────────────────────────────────────────
    private fun ecdh(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
        val ka = KeyAgreement.getInstance("ECDH")
        ka.init(privateKey)
        ka.doPhase(publicKey, true)
        return ka.generateSecret()
    }

    private fun deriveAesKey(secret: ByteArray): ByteArray {
        return HKDF.deriveKey(
            ikm = secret,
            length = AES_KEY_LENGTH,
            salt = null,
            info = HKDF_INFO.toByteArray()
        )
    }
}
