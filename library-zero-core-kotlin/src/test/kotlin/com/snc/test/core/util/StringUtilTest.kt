package com.snc.test.core.util

import com.snc.zero.core.util.StringUtil
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

class StringUtilTest : BaseJUnit5Test() {

    @Test
    fun `toString with empty array should return brackets`() {
        val result = StringUtil.toString(arrayOf())
        assertEquals("[]", result)
    }

    @Test
    fun `toString with one element`() {
        val result = StringUtil.toString(arrayOf("a"))
        assertEquals("[a]", result)
    }

    @Test
    fun `toString with multiple elements`() {
        val result = StringUtil.toString(arrayOf("a", "b", "c"))
        assertEquals("[a,b,c]", result)
    }

    @Test
    fun `join with default arguments`() {
        val result = StringUtil.join(arrayOf("x", "y", "z"))
        assertEquals("x, y, z", result)
    }

    @Test
    fun `join with custom separator and wrappers`() {
        val result = StringUtil.join(arrayOf("x", "y", "z"), separator = "-", prefix = "<", postfix = ">")
        assertEquals("<x-y-z>", result)
    }

    @Test
    fun `printJSON with array`() {
        val result = StringUtil.printJSON(arrayOf("a", "b", "c"))
        assertEquals("[a|b|c]", result)
    }
}
