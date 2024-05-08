package com.snc.zero.test.base

import com.snc.zero.test.timer.TestTimer
import org.junit.jupiter.api.*

open class BaseTest {

    /*
        - Slf4j의 문제점
        현재 로그 수준이 INFO 일때 로그는 남겨지지 않지만 로그에서 호출하는 객체의 메서드는 그대로 호출이 된다.
        만약, 해당 메서드가 비용이 있는 메서드라면? 곧바로 성능의 저하로 이어진다.

        val logger = LoggerFactory.getLogger(this::class.java)

        logger.debug("{}", foo.veryExpensiveMethod())


        Slf4j 의 여러가지 문제점을 해결한 라이브러리
        - kotlin-logging (Slf4j 문제점 개선)
        : https://velog.io/@glencode/kotlin-logging%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%ED%9A%A8%EA%B3%BC%EC%A0%81%EC%9C%BC%EB%A1%9C-%EB%A1%9C%EA%B9%85%ED%95%98%EA%B8%B0

          해당 라이브러리는 성능 저하를 피하기 위해 조건문을 사용할 필요도, 문자열의 연산을 사용할 필요도 없다.

        - 사용방법
        private val logger = KotlinLogging.logger { }

        logger.debug { "Hello ${person} ${person} ${person}" }
     */

    companion object {
        var count = 0

        // 해당 클래스에 있는 TestCase 실행 전
        @JvmStatic
        @BeforeAll
        fun setupBeforeAll() {
            println()
        }

        // 해당 클래스에 있는 TestCase 실행 후
        @JvmStatic
        @AfterAll
        fun setupAfterAll() {
            println()
        }
    }

    private val timer = TestTimer()

    // 각 TestCase 실행 전
    @BeforeEach
    open fun beforeEach(testInfo: TestInfo) {
        println("\nTask Case ${++count} -> ${testInfo.displayName}")
        timer.start()
    }

    // 각 TestCase 실행 전
    @AfterEach
    open fun afterEach(testInfo: TestInfo) {
        println("Task Result $count elapse: ${timer.stop()}\n")
    }
}