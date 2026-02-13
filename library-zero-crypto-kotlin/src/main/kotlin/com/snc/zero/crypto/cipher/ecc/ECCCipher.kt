package com.snc.zero.crypto.cipher.ecc

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
 * ✅ 1. 가장 현실적인 대체: ECC (타원곡선 암호, Elliptic Curve Cryptography)
 * 👉 결론: 지금 RSA 대신 쓸 거면 ECC가 정답이야.
 *
 * > https://velog.io/@constantlearner/%EC%95%94%ED%98%B8%ED%99%94-%EC%95%8C%EA%B3%A0%EB%A6%AC%EC%A6%98-%EB%B9%84%EA%B5%90-RSA-vs-ED25519
 * >
 * > https://academy.gopax.co.kr/ed25519-seomyeong-gaenyeomgwa-api-boaneul-wihan-hwalyong-bangbeob/
 *
 * 왜 ECC가 좋은가?
 * | 항목		 | 		RSA		| 	ECC		|
 * |-------------|--------------|-----------|
 * | 키 길이		 | 2048~4096bit	| 	256bit	|
 * | 보안 강도	 | 		보통		| 	매우 높음	|
 * | 성능		 | 		느림		| 	빠름		|
 * | 서버 부하	 | 		큼		| 	작음		|
 * | 모바일/클라우드 | 		불리		| 	유리		|
 *
 * ✔️ 같은 보안 수준이면 ECC가 훨씬 짧은 키 + 빠른 속도
 * ✔️ TLS, HTTPS, JWT, WebAuthn 전부 ECC 기반으로 이동 중
 *
 *
 * ✅ 2. ECC로 가는 “정답 구조”
 *
 * 표준 구조는 무조건 이거야:
 *
 * 🔐 Hybrid Encryption (혼합 암호)
 *   1. ECDH → 공유키 생성
 *   2. HKDF → AES Key 파생
 *   3. AES-GCM → 데이터 암호화
 *
 *   즉:
 *     ❌ 공개키로 데이터 암호화 안 함
 *     ✅ 공개키로 "키"만 교환
 */
object ECCCipher {

    private const val CURVE = "secp256r1"

    class EncryptedData(
        publicKey: ByteArray,
        iv: ByteArray,
        cipherText: ByteArray
    ) {
        val publicKey = publicKey.clone()
        val iv = iv.clone()
        val cipherText = cipherText.clone()

        override fun toString(): String {
            return "EncryptedData(publicKey=${publicKey.toHexString()}, iv=${iv.toHexString()}, cipherText=${cipherText.toHexString()})"
        }

        private fun ByteArray.toHexString(): String {
            return this.joinToString("") { "%02x".format(it) }
        }
    }

    fun generateKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        return kpg.generateKeyPair()
    }

    fun encrypt(data: ByteArray, peerPublicKey: PublicKey): EncryptedData {
        // 1. Ephemeral Key 생성
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec(CURVE))
        val ephKey = kpg.generateKeyPair()

        // 2. ECDH
        val ka = KeyAgreement.getInstance("ECDH")
        ka.init(ephKey.private)
        ka.doPhase(peerPublicKey, true)
        val secret = ka.generateSecret()

        // 3. HKDF → AES Key
        val aesKey = HKDF.deriveKey(
            ikm = secret,
            length = 32,
            salt = null,
            info = "ECC-AES-KEY".toByteArray()
        )

        // 4. AES-GCM 암호화
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }

        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(aesKey, "AES"),
            GCMParameterSpec(128, iv)
        )

        val enc = cipher.doFinal(data)

        return EncryptedData(
            ephKey.public.encoded,
            iv,
            enc
        )
    }

    fun decrypt(enc: EncryptedData, myPrivateKey: PrivateKey): ByteArray {
        val kf = KeyFactory.getInstance("EC")
        val pubKey = kf.generatePublic(X509EncodedKeySpec(enc.publicKey))

        val ka = KeyAgreement.getInstance("ECDH")
        ka.init(myPrivateKey)
        ka.doPhase(pubKey, true)

        val secret = ka.generateSecret()

        val aesKey = HKDF.deriveKey(
            ikm = secret,
            length = 32,
            salt = null,
            info = "ECC-AES-KEY".toByteArray()
        )

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(aesKey, "AES"),
            GCMParameterSpec(128, enc.iv)
        )

        return cipher.doFinal(enc.cipherText)
    }
}
