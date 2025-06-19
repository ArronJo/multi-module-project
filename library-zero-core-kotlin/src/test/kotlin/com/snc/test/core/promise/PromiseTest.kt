package com.snc.test.core.promise

import com.snc.zero.extension.random.randomInt
import com.snc.zero.core.promise.Promise
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
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

    @Test
    fun `resolve 이후에는 reject가 무시된다`() {
        val promise = Promise<Int>()
        val resolved = AtomicBoolean(false)
        val rejected = AtomicBoolean(false)

        promise
            .then { resolved.set(true) }
            .catch { rejected.set(true) }

        promise.resolve(42)
        promise.reject(RuntimeException("Should not be called"))

        assertTrue(resolved.get())
        assertFalse(rejected.get())
    }

    @Test
    fun `reject 이후에는 resolve가 무시된다`() {
        val promise = Promise<Int>()
        val resolved = AtomicBoolean(false)
        val rejected = AtomicBoolean(false)

        promise
            .then { resolved.set(true) }
            .catch { rejected.set(true) }

        promise.reject(RuntimeException("Failure"))
        promise.resolve(42)

        assertFalse(resolved.get())
        assertTrue(rejected.get())
    }

    @Test
    fun `resolve 이후 등록된 then은 즉시 호출된다`() {
        val promise = Promise<String>()
        promise.resolve("done")

        var result: String? = null
        promise.then { result = it }

        assertEquals("done", result)
    }

    @Test
    fun `reject 이후 등록된 catch는 즉시 호출된다`() {
        val promise = Promise<String>()
        val error = RuntimeException("failure")
        promise.reject(error)

        var caught: Throwable? = null
        promise.catch { caught = it }

        assertEquals(error, caught)
    }

    @Test
    fun `resolve 이후 등록된 finally는 즉시 호출된다`() {
        val promise = Promise<String>()
        val called = AtomicBoolean(false)
        promise.resolve("done")

        promise.finally { called.set(true) }

        assertTrue(called.get())
    }

    @Test
    fun `reject 이후 등록된 finally는 즉시 호출된다`() {
        val promise = Promise<String>()
        val called = AtomicBoolean(false)
        promise.reject(RuntimeException("fail"))

        promise.finally { called.set(true) }

        assertTrue(called.get())
    }

    @Test
    fun `resolve는 한 번만 실행된다`() {
        val promise = Promise<Int>()
        var callCount = 0
        promise.then { callCount++ }

        promise.resolve(1)
        promise.resolve(2) // 무시됨

        assertEquals(1, callCount)
    }

    @Test
    fun `reject는 한 번만 실행된다`() {
        val promise = Promise<Int>()
        var callCount = 0
        promise.catch { callCount++ }

        promise.reject(RuntimeException("1"))
        promise.reject(RuntimeException("2")) // 무시됨

        assertEquals(1, callCount)
    }

    @Test
    fun `resolve 이후 then catch finally가 순서대로 모두 호출된다`() {
        val logs = mutableListOf<String>()
        val promise = Promise<String>()

        promise
            .then { logs.add("then: $it") }
            .catch { logs.add("catch") }
            .finally { logs.add("finally") }

        promise.resolve("OK")

        assertEquals(listOf("then: OK", "finally"), logs)
    }

    @Test
    fun `reject 이후 then catch finally가 순서대로 모두 호출된다`() {
        val logs = mutableListOf<String>()
        val error = IllegalArgumentException("Oops")
        val promise = Promise<String>()

        promise
            .then { logs.add("then") }
            .catch { logs.add("catch: ${it.message}") }
            .finally { logs.add("finally") }

        promise.reject(error)

        assertEquals(listOf("catch: Oops", "finally"), logs)
    }

    @Test
    fun `should resolve with null value for nullable type`() {
        var result: String? = "initial"
        var finallyCalled = false

        val promise = Promise<String?>()

        promise
            .then {
                result = it // null이어야 함
            }
            .finally {
                finallyCalled = true
            }

        promise.resolve(null)

        assertNull(result, "then 블록에서 전달된 값이 null이어야 합니다")
        assertTrue(finallyCalled, "finally 블록이 호출되어야 합니다")
    }

    @Test
    fun `should resolve with null when T is nullable`() {
        val promise = Promise<String?>()

        var thenCalledWith: String? = "initial"
        var finallyCalled = false

        promise
            .then { thenCalledWith = it }
            .finally { finallyCalled = true }

        promise.resolve(null)

        assertNull(thenCalledWith, "then 블록은 null을 전달받아야 함")
        assertTrue(finallyCalled, "finally 블록은 호출되어야 함")
    }

    @Test
    fun `should not call catch when rejected with null`() {
        val promise = Promise<String?>()

        var catchCalled = false
        var finallyCalled = false

        promise
            .catch {
                catchCalled = true
            }
            .finally {
                finallyCalled = true
            }

        promise.reject(null)

        assertFalse(catchCalled, "catch 블록은 null 예외일 경우 호출되지 않아야 함")
        assertTrue(finallyCalled, "finally 블록은 호출되어야 함")
    }
}
