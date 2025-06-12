package com.snc.test.core.promise

import com.snc.zero.extension.random.randomInt
import com.snc.zero.core.promise.Promise
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

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
}
