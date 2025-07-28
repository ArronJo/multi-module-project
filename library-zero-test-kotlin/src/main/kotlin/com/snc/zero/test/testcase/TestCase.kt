package com.snc.zero.test.testcase

/**
 * BDD 스타일의 테스트 작성을 위한 DSL 클래스
 * given-when-then-catch 패턴을 지원합니다.
 * given에서 정의한 변수들을 when에서도 사용할 수 있습니다.
 *
 * 사용법:
 *  TestCase.given {
 *    val name = "test"
 *    val number = 42
 *    givenContext(name, number)  // 여러 변수를 담는 데이터 클래스
 *  }.`when` { (name, number) ->  // 구조 분해 할당으로 변수 접근
 *    "$name: $number".uppercase()
 *  }.then { result ->
 *    assertEquals("TEST: 42", result)
 *  }
 *
 *  또는 단일 값:
 *  TestCase.given {
 *    "hello"
 *  }.`when` { data ->
 *    data.uppercase()
 *  }.then { result ->
 *    assertEquals("HELLO", result)
 *  }
 */
class TestCase<T> private constructor(
    private val givenData: T? = null,
    private val givenException: Exception? = null
) {

    companion object {
        /**
         * 테스트의 시작점. given 데이터를 설정합니다.
         * @param data 테스트에 사용할 초기 데이터 (예외가 발생할 수 있음)
         * @return TestCase 인스턴스
         */
        fun <T> given(data: () -> T): TestCase<T> {
            return try {
                TestCase(givenData = data())
            } catch (e: Exception) {
                TestCase(givenException = e)
            }
        }
    }

    /**
     * when 단계. 주어진 데이터에 대해 실행할 동작을 정의합니다.
     * @param action 실행할 동작 함수
     * @return WhenResult 인스턴스
     */
    fun <R> `when`(action: (T) -> R): WhenResult<R> {
        // given 단계에서 예외가 발생한 경우
        if (givenException != null) {
            return WhenResult(exception = givenException)
        }

        // given 데이터가 null인 경우 (타입 안전성)
        if (givenData == null) {
            return WhenResult(exception = IllegalStateException("Given data is null"))
        }

        return try {
            val result = action(givenData)
            WhenResult(result = result)
        } catch (e: Exception) {
            WhenResult(exception = e)
        }
    }

    /**
     * given 단계에서 발생한 예외를 처리합니다.
     * @param handler 예외 처리 함수
     */
    fun catch(handler: (Exception) -> Unit) {
        if (givenException != null) {
            handler(givenException)
        }
    }
}

/**
 * when 단계의 결과 또는 예외를 담는 클래스
 */
class WhenResult<R> internal constructor(
    private val result: R? = null,
    private val exception: Exception? = null
) {

    /**
     * then 단계. 성공적인 결과에 대한 검증을 수행합니다.
     * @param assertion 검증할 함수
     */
    fun then(assertion: (R) -> Unit) {
        if (exception != null) {
            // 예외가 발생했지만 then이 호출된 경우, 예외를 다시 던짐
            throw exception
        }

        if (result != null) {
            assertion(result)
        }
    }

    /**
     * when 단계에서 발생한 예외를 처리합니다.
     * @param handler 예외 처리 함수
     */
    fun catch(handler: (Exception) -> Unit) {
        if (exception != null) {
            handler(exception)
        }
    }
}

// 여러 변수를 담기 위한 유틸리티 데이터 클래스들
data class GivenContext2<T1, T2>(val first: T1, val second: T2)
data class GivenContext3<T1, T2, T3>(val first: T1, val second: T2, val third: T3)
data class GivenContext4<T1, T2, T3, T4>(val first: T1, val second: T2, val third: T3, val fourth: T4)
data class GivenContext5<T1, T2, T3, T4, T5>(val first: T1, val second: T2, val third: T3, val fourth: T4, val fifth: T5)

// 더 많은 변수가 필요한 경우를 위한 맵 기반 컨텍스트
class GivenContextMap(private val values: Map<String, Any>) {
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T = values[key] as T
}

// 편의 함수들
fun <T1, T2> givenContext(p1: T1, p2: T2) = GivenContext2(p1, p2)
fun <T1, T2, T3> givenContext(p1: T1, p2: T2, p3: T3) = GivenContext3(p1, p2, p3)
fun <T1, T2, T3, T4> givenContext(p1: T1, p2: T2, p3: T3, p4: T4) = GivenContext4(p1, p2, p3, p4)
fun <T1, T2, T3, T4, T5> givenContext(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5) = GivenContext5(p1, p2, p3, p4, p5)

fun givenContextMap(vararg pairs: Pair<String, Any>) = GivenContextMap(pairs.toMap())
