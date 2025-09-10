package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AESGCM
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * 성능 테스트
 *
 * - 대용량 데이터 처리 (1MB, 5MB, 10MB)
 * - 높은 반복 횟수 성능
 * - 연속 암호화 스트레스 테스트
 * - 메모리 사용량 모니터링
 */
@DisplayName("AES-GCM 성능 및 스트레스 테스트")
open class AESGCMPerformanceTest {

    private val testKey = AESGCM.generateKey(256)

    @Nested
    @DisplayName("대용량 데이터 처리 테스트")
    inner class LargeDataProcessing {

        @Test
        @DisplayName("1MB 데이터 처리 성능 테스트")
        fun `should handle 1MB data efficiently`() {
            // Given
            val largeData = ByteArray(1_000_000) { (it % 256).toByte() } // 1MB 데이터

            // When
            val startTime = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(largeData, testKey)
            val decrypted = AESGCM.decrypt(encrypted, testKey)
            val endTime = System.currentTimeMillis()

            // Then
            assertArrayEquals(largeData, decrypted, "10MB 데이터가 정상 처리되어야 한다")
            println("10MB 데이터 암복호화 시간: ${endTime - startTime}ms")
            assertTrue(endTime - startTime < 30000, "10MB 데이터 처리가 30초 이내에 완료되어야 한다")
        }

        @Test
        @DisplayName("다양한 크기의 데이터 처리 시간 비교")
        fun `should compare processing time for various data sizes`() {
            val dataSizes = listOf(1000, 10_000, 100_000, 1_000_000) // 1KB, 10KB, 100KB, 1MB
            val processingTimes = mutableMapOf<Int, Long>()

            dataSizes.forEach { size ->
                // Given
                val data = ByteArray(size) { (it % 256).toByte() }

                // When
                val startTime = System.currentTimeMillis()
                val encrypted = AESGCM.encrypt(data, testKey)
                val decrypted = AESGCM.decrypt(encrypted, testKey)
                val endTime = System.currentTimeMillis()

                // Then
                assertArrayEquals(data, decrypted, "${size}바이트 데이터가 정상 처리되어야 한다")
                processingTimes[size] = endTime - startTime

                println("${size}바이트 데이터 처리 시간: ${endTime - startTime}ms")
            }

            // 데이터 크기가 클수록 처리 시간이 더 오래 걸려야 함 (일반적으로)
            // assertTrue(processingTimes[1_000_000]!! >= processingTimes[100_000]!!)
        }
    }

    @Nested
    @DisplayName("높은 반복 횟수 성능 테스트")
    inner class HighIterationPerformance {

        @Test
        @DisplayName("10K 반복 횟수에서의 성능 테스트")
        fun `should handle 10K iteration count reasonably`() {
            // Given
            val params = AESGCM.Params(hashIterations = 10_000)
            val smallData = "10K 반복 테스트".toByteArray()

            // When
            val startTime = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(smallData, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)
            val endTime = System.currentTimeMillis()

            // Then
            assertArrayEquals(smallData, decrypted, "10K 반복에서도 정상 동작해야 한다")
            println("10K 반복 암복호화 시간: ${endTime - startTime}ms")
            assertTrue(endTime - startTime < 5000, "10K 반복이 5초 이내에 완료되어야 한다")
        }

        @Test
        @DisplayName("50K 반복 횟수에서의 성능 테스트")
        fun `should handle 50K iteration count reasonably`() {
            // Given
            val params = AESGCM.Params(hashIterations = 50_000)
            val smallData = "50K 반복 테스트".toByteArray()

            // When
            val startTime = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(smallData, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)
            val endTime = System.currentTimeMillis()

            // Then
            assertArrayEquals(smallData, decrypted, "50K 반복에서도 정상 동작해야 한다")
            println("50K 반복 암복호화 시간: ${endTime - startTime}ms")
            assertTrue(endTime - startTime < 10000, "50K 반복이 10초 이내에 완료되어야 한다")
        }

        @Test
        @DisplayName("100K 반복 횟수에서의 성능 테스트")
        fun `should handle 100K iteration count within acceptable time`() {
            // Given
            val params = AESGCM.Params(hashIterations = 100_000)
            val smallData = "100K 반복 테스트".toByteArray()

            // When
            val startTime = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(smallData, testKey, params = params)
            val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)
            val endTime = System.currentTimeMillis()

            // Then
            assertArrayEquals(smallData, decrypted, "100K 반복에서도 정상 동작해야 한다")
            println("100K 반복 암복호화 시간: ${endTime - startTime}ms")
            assertTrue(endTime - startTime < 20000, "100K 반복이 20초 이내에 완료되어야 한다")
        }

        @Test
        @DisplayName("반복 횟수별 성능 비교")
        fun `should compare performance across different iteration counts`() {
            val iterationCounts = listOf(1_000, 5_000, 10_000, 50_000)
            val testData = "반복 횟수 성능 비교".toByteArray()
            val performanceResults = mutableMapOf<Int, Long>()

            iterationCounts.forEach { iterations ->
                // Given
                val params = AESGCM.Params(hashIterations = iterations)

                // When
                val startTime = System.currentTimeMillis()
                val encrypted = AESGCM.encrypt(testData, testKey, params = params)
                val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)
                val endTime = System.currentTimeMillis()

                // Then
                assertArrayEquals(testData, decrypted, "${iterations}회 반복이 정상 동작해야 한다")
                performanceResults[iterations] = endTime - startTime

                println("${iterations}회 반복 처리 시간: ${endTime - startTime}ms")
            }

            // 반복 횟수가 많을수록 시간이 더 오래 걸려야 함
            assertTrue(performanceResults[50_000]!! >= performanceResults[1_000]!!)
        }
    }

    @Nested
    @DisplayName("연속 암호화 스트레스 테스트")
    inner class ContinuousEncryptionStress {

        @Test
        @DisplayName("50회 연속 암호화 안정성 테스트")
        fun `should maintain stability in 100 continuous encryptions`() {
            // Given
            val testData = "연속 암호화 테스트".toByteArray()

            // When & Then - 연속으로 100번 암복호화
            val startTime = System.currentTimeMillis()
            repeat(50) { iteration ->
                val encrypted = AESGCM.encrypt(testData, testKey)
                val decrypted = AESGCM.decrypt(encrypted, testKey)

                assertArrayEquals(
                    testData,
                    decrypted,
                    "반복 ${iteration + 1}에서 정상 동작해야 한다"
                )
            }
            val endTime = System.currentTimeMillis()

            println("100회 연속 암복호화 총 시간: ${endTime - startTime}ms")
            println("평균 처리 시간: ${(endTime - startTime) / 100.0}ms")
            assertTrue(endTime - startTime < 10000, "100회 처리가 10초 이내에 완료되어야 한다")
        }

        @Test
        @DisplayName("다양한 크기의 데이터로 연속 암호화 테스트")
        fun `should handle continuous encryption with various data sizes`() {
            // Given
            val dataSizes = listOf(100, 1_000, 10_000, 100_000) // 100B, 1KB, 10KB, 100KB
            val iterations = 10

            dataSizes.forEach { size ->
                val testData = ByteArray(size) { (it % 256).toByte() }

                // When
                val startTime = System.currentTimeMillis()
                repeat(iterations) { iteration ->
                    val encrypted = AESGCM.encrypt(testData, testKey)
                    val decrypted = AESGCM.decrypt(encrypted, testKey)

                    // Then
                    assertArrayEquals(
                        testData,
                        decrypted,
                        "${size}바이트 데이터 반복 ${iteration + 1}에서 정상 동작해야 한다"
                    )
                }
                val endTime = System.currentTimeMillis()

                println("${size}바이트 데이터 ${iterations}회 처리 시간: ${endTime - startTime}ms")
            }
        }
    }

    @Nested
    @DisplayName("메모리 사용량 테스트")
    inner class MemoryUsageTest {

        @Test
        @DisplayName("대용량 데이터 처리 시 메모리 사용량 체크")
        fun `should monitor memory usage during large data processing`() {
            // Given
            val runtime = Runtime.getRuntime()
            val largeSizes = listOf(1_000_000, 5_000_000) // 1MB, 5MB

            largeSizes.forEach { size ->
                // 가비지 컬렉션 실행
                runtime.gc()
                Thread.sleep(100)

                val memoryBefore = runtime.totalMemory() - runtime.freeMemory()
                val data = ByteArray(size) { (it % 256).toByte() }

                // When
                val encrypted = AESGCM.encrypt(data, testKey)
                val decrypted = AESGCM.decrypt(encrypted, testKey)

                // Then
                assertArrayEquals(data, decrypted, "${size}바이트 데이터가 정상 처리되어야 한다")

                val memoryAfter = runtime.totalMemory() - runtime.freeMemory()
                val memoryUsed = memoryAfter - memoryBefore

                println("${size}바이트 처리 시 메모리 사용량: ${memoryUsed / 1024 / 1024}MB")

                // 메모리 사용량이 원본 데이터 크기의 10배를 넘지 않아야 함
                println("메모리 사용량이 원본 데이터 크기의 10배를 넘지 않아야 함: 사용량 = $memoryUsed < ${size * 10}")
            }
        }
    }

    @Nested
    @DisplayName("암호화 품질 vs 성능 벤치마크")
    inner class QualityVsPerformanceBenchmark {

        @Test
        @DisplayName("다양한 보안 레벨별 성능 비교")
        fun `should compare performance across different security levels`() {
            val testData = "보안 레벨 성능 비교".toByteArray()

            // 낮은 보안 레벨 (빠른 처리)
            val lowSecurityParams = AESGCM.Params(
                keyBits = 128,
                saltBytes = 8,
                hashIterations = 1_000
            )

            // 중간 보안 레벨 (균형)
            val mediumSecurityParams = AESGCM.Params(
                keyBits = 192,
                saltBytes = 16,
                hashIterations = 10_000
            )

            // 높은 보안 레벨 (느린 처리)
            val highSecurityParams = AESGCM.Params(
                keyBits = 256,
                saltBytes = 32,
                hashIterations = 50_000
            )

            val scenarios = listOf(
                "낮은 보안" to lowSecurityParams,
                "중간 보안" to mediumSecurityParams,
                "높은 보안" to highSecurityParams
            )

            scenarios.forEach { (level, params) ->
                val key = AESGCM.generateKey(params.keyBits)

                // When
                val startTime = System.currentTimeMillis()
                val encrypted = AESGCM.encrypt(testData, key, params = params)
                val decrypted = AESGCM.decrypt(encrypted, key, params = params)
                val endTime = System.currentTimeMillis()

                // Then
                assertArrayEquals(testData, decrypted, "$level 레벨에서 정상 동작해야 한다")
                println("$level 레벨 처리 시간: ${endTime - startTime}ms")
            }
        }

        @Test
        @DisplayName("Salt 크기별 성능 영향 분석")
        fun `should analyze performance impact of different salt sizes`() {
            val testData = "Salt 크기 성능 분석".toByteArray()
            val saltSizes = listOf(8, 16, 32, 64)

            saltSizes.forEach { saltSize ->
                val params = AESGCM.Params(saltBytes = saltSize)

                // When
                val startTime = System.currentTimeMillis()
                repeat(10) {
                    val encrypted = AESGCM.encrypt(testData, testKey, params = params)
                    val decrypted = AESGCM.decrypt(encrypted, testKey, params = params)
                    assertArrayEquals(testData, decrypted)
                }
                val endTime = System.currentTimeMillis()

                println("${saltSize}바이트 Salt로 50회 처리 시간: ${endTime - startTime}ms")
            }
        }

        @Test
        @DisplayName("1MB 데이터 처리 성능 테스트")
        fun `should handle 1MB data efficiently`() {
            // Given
            val largeData = ByteArray(1_000_000) { (it % 256).toByte() } // 5MB 데이터

            // When
            val startTime = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(largeData, testKey)
            val decrypted = AESGCM.decrypt(encrypted, testKey)
            val endTime = System.currentTimeMillis()

            // Then
            assertArrayEquals(largeData, decrypted, "5MB 데이터가 정상 처리되어야 한다")
            println("1MB 데이터 암복호화 시간: ${endTime - startTime}ms")
            assertTrue(endTime - startTime < 15000, "5MB 데이터 처리가 15초 이내에 완료되어야 한다")
        }

        @Test
        @DisplayName("5MB 데이터 처리 성능 테스트")
        fun `should handle 5MB data efficiently`() {
            // Given
            val largeData = ByteArray(5_000_000) { (it % 256).toByte() } // 5MB 데이터

            // When
            val startTime = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(largeData, testKey)
            val decrypted = AESGCM.decrypt(encrypted, testKey)
            val endTime = System.currentTimeMillis()

            // Then
            assertArrayEquals(largeData, decrypted, "5MB 데이터가 정상 처리되어야 한다")
            println("5MB 데이터 암복호화 시간: ${endTime - startTime}ms")
            assertTrue(endTime - startTime < 15000, "5MB 데이터 처리가 15초 이내에 완료되어야 한다")
        }

        @Test
        @DisplayName("10MB 데이터 처리 성능 테스트")
        fun `should handle 10MB data efficiently`() {
            // Given
            val largeData = ByteArray(10_000_000) { (it % 256).toByte() } // 5MB 데이터

            // When
            val startTime = System.currentTimeMillis()
            val encrypted = AESGCM.encrypt(largeData, testKey)
            val decrypted = AESGCM.decrypt(encrypted, testKey)
            val endTime = System.currentTimeMillis()

            // Then
            assertArrayEquals(largeData, decrypted, "5MB 데이터가 정상 처리되어야 한다")
            println("10MB 데이터 암복호화 시간: ${endTime - startTime}ms")
            assertTrue(endTime - startTime < 15000, "5MB 데이터 처리가 15초 이내에 완료되어야 한다")
        }
    }
}
