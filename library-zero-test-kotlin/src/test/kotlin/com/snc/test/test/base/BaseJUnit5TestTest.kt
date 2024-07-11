package com.snc.test.test.base

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class BaseJUnit5TestTest : BaseJUnit5Test() {

    @Test
    fun `BaseJUnit5Test 테스트 - 1`() {
        println(Companion.count)
    }
}