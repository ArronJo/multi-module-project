package com.snc.test.core.util

import com.snc.zero.core.util.PrintUtil
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class PrintUtilTest : BaseJUnit5Test() {

    @Test
    fun `array 출력`() {
        val result = PrintUtil.print(arrayOf("a", "b", "c"))
        assertEquals("[a|b|c]", result)
    }

    @Test
    fun `JsonString 출력`() {
        val str =
            """"{ "user_agent": { "family": "Chrome Mobile WebView", "major": "56", "minor": "0", "patch": "2924" }, "os": { "family": "Android", "major": "7", "minor": "0", "patch": "", "patch_minor": "" }, "device": { "family": "Samsung SM-P585N0" } }""""
        println(PrintUtil.printJSON(str))
    }
}
