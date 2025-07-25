package com.snc.test.test.extensions.debug

import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.test.extensions.debug.displayInSeconds
import com.snc.zero.test.testcase.TestCaseOld
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.math.max

@Suppress("NonAsciiCharacters")
class DebugExtTest : BaseJUnit5Test() {

    @Test
    fun `DebugExt 테스트 - 1`() {
        TestCaseOld.create<Long, String, Unit>()
            .given {
                System.currentTimeMillis()
            }.`when` { data ->
                max(System.currentTimeMillis() - data, 0).displayInSeconds()
            }.then { result ->
                println("displayInSeconds: $result")
                assertNotEquals("", result)
            }
    }
}
