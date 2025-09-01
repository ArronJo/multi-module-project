package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.cipher.aes.HashGenerator
import org.junit.jupiter.api.Assertions.assertTimeoutPreemptively
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.Random

@DisplayName("AES-GCM 암호화 테스트 (Iteration 퍼포먼스)")
class AESGCMIterationPerformanceTest {

    private val testPlaintext = "Hello AES-GCM!".toByteArray()
    private val testKey = AESGCM.generateKey(256)
    private val testIv = ByteArray(12).also { Random().nextBytes(it) }
    private val testSalt = ByteArray(16).also { Random().nextBytes(it) }

    private val iterationCandidates = listOf(
        100_000, // NIST 권장 최소값
        104_729, // 10만 근처 첫 번째 소수
        131_071, // 2^17 - 1 (메르센 소수)
        262_139, // 2^18 - 1 (메르센 소수)
        999_983 // 6자리 최대 소수
    )

    @Test
    @DisplayName("Hash iterations 값별 성능 테스트")
    fun `should measure performance for different hashIterations`() {
        iterationCandidates.forEach { iterations ->
            assertTimeoutPreemptively(Duration.ofSeconds(5)) {
                val start = System.currentTimeMillis()

                val hash = HashGenerator.createSecureHash(
                    plaintext = testPlaintext,
                    iv = testIv,
                    key = testKey,
                    salt = testSalt,
                    iterations = iterations,
                    timestamp = System.currentTimeMillis()
                )

                val elapsed = System.currentTimeMillis() - start
                println("Iterations=$iterations → took ${elapsed}ms, hash size=${hash.size}")

                // 최소한 해시가 잘 생성되었는지 검증
                assertTrue(hash.isNotEmpty())
            }
        }
    }
}
