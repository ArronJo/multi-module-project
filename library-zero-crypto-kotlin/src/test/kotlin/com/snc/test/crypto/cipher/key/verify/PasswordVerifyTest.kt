package com.snc.test.crypto.cipher.key.verify

import com.snc.zero.crypto.key.verify.PasswordVerify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class PasswordVerifyTest {

    @Test
    fun `hashPassword는 saltHex_colon_hashHex 포맷을 만든다`() {
        val stored = PasswordVerify.hashPassword("P@ssw0rd!")

        val parts = stored.split(":")
        Assertions.assertEquals(2, parts.size, "stored format must be salt:hash")

        val saltHex = parts[0]
        val hashHex = parts[1]

        // salt 16 bytes => 32 hex chars
        Assertions.assertEquals(32, saltHex.length)
        // dkLen 64 bytes => 128 hex chars
        Assertions.assertEquals(128, hashHex.length)

        Assertions.assertTrue(saltHex.all { it in "0123456789abcdef" }, "salt must be lowercase hex")
        Assertions.assertTrue(hashHex.all { it in "0123456789abcdef" }, "hash must be lowercase hex")
    }

    @Test
    fun `verifyPassword는 같은 비밀번호면 true`() {
        val pw = "P@ssw0rd!"
        val stored = PasswordVerify.hashPassword(pw)

        Assertions.assertTrue(PasswordVerify.verifyPassword(pw, stored))
    }

    @Test
    fun `verifyPassword는 다른 비밀번호면 false`() {
        val stored = PasswordVerify.hashPassword("P@ssw0rd!")

        Assertions.assertFalse(PasswordVerify.verifyPassword("wrong-password", stored))
    }

    @Test
    fun `verifyPassword는 저장 포맷이 깨졌으면 false`() {
        Assertions.assertFalse(PasswordVerify.verifyPassword("a", ""))
        Assertions.assertFalse(PasswordVerify.verifyPassword("a", "no_colon_here"))
        Assertions.assertFalse(PasswordVerify.verifyPassword("a", "zzzz:1234")) // salt hex invalid
        Assertions.assertFalse(PasswordVerify.verifyPassword("a", "00:zzzz")) // hash hex invalid
    }

    @Test
    fun `NFKC 정규화로 인해 시각적으로 다른 입력이 동일하게 검증될 수 있다`() {
        // 'é' (U+00E9) vs 'e' + '◌́' (U+0065 U+0301)
        val composed = "café"
        val decomposed = "cafe\u0301"

        val stored = PasswordVerify.hashPassword(composed)

        Assertions.assertTrue(
            PasswordVerify.verifyPassword(decomposed, stored),
            "NFKC normalize should make composed/decomposed forms match"
        )
    }

    @Test
    fun `같은 비밀번호라도 매번 다른 salt로 인해 해시 결과는 달라진다`() {
        val pw = "P@ssw0rd!"
        val a = PasswordVerify.hashPassword(pw)
        val b = PasswordVerify.hashPassword(pw)

        Assertions.assertNotEquals(a, b)
        // 그래도 둘 다 검증은 성공해야 함
        Assertions.assertTrue(PasswordVerify.verifyPassword(pw, a))
        Assertions.assertTrue(PasswordVerify.verifyPassword(pw, b))
    }
}
