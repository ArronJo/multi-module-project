package com.snc.zero.test.testcase

class TestCase<G, W, T> private constructor() {
    private var givenData: G? = null
    private var whenResult: W? = null
    private var caughtException: Throwable? = null

    fun given(block: () -> G): TestCase<G, W, T> {
        givenData = block()
        return this
    }

    fun whens(block: (G) -> W): TestCase<G, W, T> {
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
        fun <G, W, T> create(): TestCase<G, W, T> {
            return TestCase()
        }
    }
}
