package com.snc.zero.crypto.signer.ed25519

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

/**
 * EdDSA - Ed25519 전자서명
 *
 * 용도:
 *   ✅ SSH 키 인증 (ssh-keygen -t ed25519)
 *   ✅ JWT EdDSA 서명
 *   ✅ 암호화폐 (Solana, Cardano 등)
 *   ✅ API 요청 서명 (webhook 무결성 검증)
 *
 * ECDSA 대비 장점:
 *   ✅ 결정적 서명 (랜덤 불필요 → k 노출 위험 없음)
 *   ✅ 더 빠름
 *   ✅ 구현 실수에 강함
 *   ✅ 키 길이 더 짧음 (32바이트)
 *
 * ---
 * Ed25519 = Edwards-curve Digital Signature Algorithm (EdDSA)
 * → 타원곡선 Curve25519 (Twisted Edwards Curve) 기반 서명 알고리즘
 *
 * 특징:
 * 🔐 매우 빠름
 * 🔐 결정적 서명 (랜덤 필요 없음)
 * 🔐 구현 실수에 강함
 * 🔐 키 길이 짧음 (32바이트)
 * 🔐 현대 암호 표준 (TLS, SSH, JWT 등)
 *
 * Ed25519는 강력한 보안성과 빠른 속도를 제공하는 현대적인 타원곡선 디지털 서명 알고리즘 (EdDSA)의 한 종류로,
 * Curve25519를 기반으로 하며 32바이트의 짧은 키와 64바이트 서명으로 RSA보다 효율적이면서도
 * 동등 이상의 보안을 제공하여 SSH 키, 암호화폐 등 다양한 분야에서 널리 사용됩니다.
 *
 * - JDK 15 이상이면 별도 라이브러리 없이 가능.
 * - JDK 11 이하라면:
 * ```
 *   dependencies {
 *     implementation("org.bouncycastle:bcprov-jdk18on:1.84")
 *   }
 * ```
 *
 * Ed25519 같은 **전자서명 알고리즘은 "암호화용"이 아니라 "무결성·신원증명용"**
 * 구조:
 *   - 개인키 → 서명(Sign)
 *   - 공개키 → 검증(Verify)
 *
 * ✔️ 메시지를 "되돌려서 원문 복호화"하는 개념 자체가 없음
 * ✔️ 서명 = 해시 + 수학적 증명값
 */
class Ed25519Signer {

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("Ed25519")
        return keyGen.generateKeyPair()
    }

    fun sign(privateKey: PrivateKey, message: ByteArray): ByteArray {
        val sig = Signature.getInstance("Ed25519")
        sig.initSign(privateKey)
        sig.update(message)
        return sig.sign()
    }

    fun verify(
        publicKey: PublicKey,
        message: ByteArray,
        signature: ByteArray
    ): Boolean {
        val sig = Signature.getInstance("Ed25519")
        sig.initVerify(publicKey)
        sig.update(message)
        return sig.verify(signature)
    }
}
