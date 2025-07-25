package com.snc.zero.test.testcase

/**
 * 사용법:
 * TestCaseOld.create<String, String, Unit>()
 *   .given {
 *     "a"
 *   }.`when` { data ->
 *     data.uppercase()
 *   }.then { result ->
 *     assertEquals("A", result)
 *   }
 */
class TestCaseOld<G, W, T> private constructor() {
    private var givenData: G? = null
    private var whenResult: W? = null
    private var caughtException: Throwable? = null

    fun given(block: () -> G): TestCaseOld<G, W, T> {
        givenData = block()
        return this
    }

    fun `when`(block: (G) -> W): TestCaseOld<G, W, T> {
        try {
            whenResult = block(givenData!!)
        } catch (e: Throwable) {
            caughtException = e
        }
        return this
    }

    fun then(block: (W) -> T) {
        if (caughtException != null) {
            throw AssertionError("Unexpected exception: ${caughtException!!.message}")
        }
        block(whenResult!!)
    }

    fun catch(block: (Throwable) -> T) {
        if (caughtException == null) {
            throw AssertionError("Expected exception, but none was thrown")
        }
        block(caughtException!!)
    }

    companion object {
        fun <G, W, T> create(): TestCaseOld<G, W, T> {
            return TestCaseOld()
        }

        fun <G, W, T> given(block: () -> G): TestCaseOld<G, W, T> {
            val testCase = TestCaseOld<G, W, T>()
            testCase.given(block)
            return testCase
        }
    }
}
