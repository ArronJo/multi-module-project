package com.snc.zero.test.testcase

/**
 * BDD 스타일의 테스트 작성을 위한 DSL 클래스
 * given-when-then-catch 패턴을 지원합니다.
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
