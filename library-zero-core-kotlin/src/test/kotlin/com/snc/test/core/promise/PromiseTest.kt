package com.snc.test.core.promise

import com.snc.zero.extension.random.randomInt
import com.snc.zero.core.promise.Promise
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PromiseTest : BaseJUnit5Test() {

    @Test
    fun `Promise 테스트 성공 - 1`() {
        val promise = Promise.create { resolve, reject ->
            // 비동기 작업
            Thread {
                Thread.sleep(1000)

                if (Math.random() > 0.5) {
                    resolve("성공!")
                } else {
                    reject(Exception("실패!"))
                }
            }.start()
        }

        promise
            .then { result ->
                logger.debug { "결과: $result" }
            }
            .catch { error ->
                logger.error { "에러: ${error.message}" }
            }
            .finally {
                logger.debug { "작업 완료" }
            }

        Thread.sleep(2000)
    }

    @Test
    fun `Promise 테스트 성공 - 2`() {
        Promise.create { resolve, reject ->
            Thread {
                Thread.sleep(1000)

                resolve("성공!")
            }.start()
        }.then { result ->
            logger.debug { "결과: $result" }
        }.catch { error ->
            logger.debug { "에러: ${error.message}" }
        }.finally {
            logger.debug { "작업 완료" }
        }

        Thread.sleep(2000)
    }

    @Test
    fun `Promise 테스트 실패 - 1`() {
        Promise.create { resolve, reject ->
            Thread {
                Thread.sleep(1000)

                if (randomInt(0, 1) > 10) {
                    resolve("")
                } else {
                    reject(Exception("실패!"))
                }
            }.start()

        }.catch { error ->
            logger.debug { "에러: ${error.message}" }
        }.finally {
            logger.debug { "작업 완료" }
        }

        Thread.sleep(2000)
    }

    @Test
    fun `should call then with resolved value`() {
        val result = AtomicReference<String>()

        Promise.create { resolve, _ ->
            resolve("Success")
        }.then {
            result.set(it)
        }

        assertEquals("Success", result.get())
    }

    @Test
    fun `should call catch with rejected error`() {
        val error = AtomicReference<Throwable>()

        Promise.create<String> { _, reject ->
            reject(IllegalArgumentException("Bad input"))
        }.catch {
            error.set(it)
        }

        assertTrue(error.get() is IllegalArgumentException)
        assertEquals("Bad input", error.get().message)
    }

    @Test
    fun `should call finally after resolve`() {
        val finallyCalled = AtomicBoolean(false)

        Promise.create<String> { resolve, _ ->
            resolve("OK")
        }.finally {
            finallyCalled.set(true)
        }

        assertTrue(finallyCalled.get())
    }

    @Test
    fun `should call finally after reject`() {
        val finallyCalled = AtomicBoolean(false)

        Promise.create<String> { _, reject ->
            reject(RuntimeException("Failure"))
        }.finally {
            finallyCalled.set(true)
        }

        assertTrue(finallyCalled.get())
    }

    @Test
    fun `should catch exception thrown in executor`() {
        val error = AtomicReference<Throwable>()

        Promise.create<String> { _, _ ->
            throw IllegalStateException("Executor failed")
        }.catch {
            error.set(it)
        }

        assertTrue(error.get() is IllegalStateException)
        assertEquals("Executor failed", error.get().message)
    }

    @Test
    fun `should chain then and finally`() {
        val result = AtomicReference<String>()
        val finallyCalled = AtomicBoolean(false)

        Promise.create<String> { resolve, _ ->
            resolve("Chain")
        }.then {
            result.set(it)
        }.finally {
            finallyCalled.set(true)
        }

        assertEquals("Chain", result.get())
        assertTrue(finallyCalled.get())
    }

    @Test
    fun `should chain catch and finally`() {
        val error = AtomicReference<Throwable>()
        val finallyCalled = AtomicBoolean(false)

        Promise.create<String> { _, reject ->
            reject(Exception("Chain error"))
        }.catch {
            error.set(it)
        }.finally {
            finallyCalled.set(true)
        }

        assertEquals("Chain error", error.get()?.message)
        assertTrue(finallyCalled.get())
    }
}
