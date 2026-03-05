package com.snc.zero.crypto.password.verify

import org.bouncycastle.crypto.generators.SCrypt
import java.security.SecureRandom
import java.text.Normalizer

/**
 * 키 관리
 * -참고:  https://github.com/better-auth/better-auth
 * ---
 * 실제 사용 흐름
 * [ 회원가입 ]
 * val stored = BetterAuthPassword.hashPassword("user123!")
 * // → "a3f1c2...:9bc2fe..."
 * // DB users 테이블 password 컬럼에 저장
 * ---
 * [ 로그인 ]
 * val input = "user123!"           // 사용자 입력
 * val stored = db.getPassword(userId)  // DB에서 꺼낸 값
 * val ok = BetterAuthPassword.verifyPassword(input, stored)
 * // → true  ✅
 * ---
 * [DB 저장 시 주의사항]
 * 컬럼 타입VARCHAR(200) 이상 (saltHex:dkHex = 160자)]
 * ---
 * [이 코드가 안전한 이유]
 * - scrypt = 느린 해시 (bcrypt/argon2 계열)
 *   → 브루트포스 공격 시 GPU로도 초당 수십 번밖에 못 시도
 *   → 일반 SHA256은 초당 수십억 번 가능 → 절대 사용 금지
 *
 * - N=16384, R=16 → better-auth 와 동일한 강도
 * - salt 16바이트 랜덤 → Rainbow Table 무력화
 * - constantTimeEquals → 타이밍 공격 방어
 * - Normalizer.NFKC → "ａ" vs "a" 같은 유니코드 트릭 방어
 */
object PasswordVerify {

    private const val SALT_LEN = 16
    private const val DK_LEN = 64

    // scrypt params (better-auth 동일)
    private const val N = 16384
    private const val R = 16
    private const val P = 1

    private val rng = SecureRandom()

    /** 저장 포맷: "{saltHex}:{derivedKeyHex}" */
    fun hashPassword(password: String): String {
        val normalized = Normalizer.normalize(password, Normalizer.Form.NFKC)

        val salt = ByteArray(SALT_LEN).also { rng.nextBytes(it) }
        val dk = SCrypt.generate(
            normalized.toByteArray(Charsets.UTF_8),
            salt,
            N, R, P,
            DK_LEN
        )

        return "${salt.toHex()}:${dk.toHex()}"
    }

    fun verifyPassword(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 2) return false

        val salt = parts[0].hexToBytesOrNull() ?: return false
        val expected = parts[1].hexToBytesOrNull() ?: return false

        val normalized = Normalizer.normalize(password, Normalizer.Form.NFKC)
        val actual = SCrypt.generate(
            normalized.toByteArray(Charsets.UTF_8),
            salt,
            N, R, P,
            expected.size
        )

        return constantTimeEquals(actual, expected)
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var diff = 0
        for (i in a.indices) diff = diff or (a[i].toInt() xor b[i].toInt())
        return diff == 0
    }

    private fun ByteArray.toHex(): String {
        val out = CharArray(this.size * 2)
        val hex = "0123456789abcdef".toCharArray()
        var j = 0
        for (b in this) {
            val v = b.toInt() and 0xFF
            out[j++] = hex[v ushr 4]
            out[j++] = hex[v and 0x0F]
        }
        return String(out)
    }

    private fun String.hexToBytesOrNull(): ByteArray? {
        val s = this.lowercase()
        if (s.length % 2 != 0) return null
        val out = ByteArray(s.length / 2)
        for (i in out.indices) {
            val hi = s[i * 2].digitToIntOrNull(16) ?: return null
            val lo = s[i * 2 + 1].digitToIntOrNull(16) ?: return null
            out[i] = ((hi shl 4) or lo).toByte()
        }
        return out
    }
}
