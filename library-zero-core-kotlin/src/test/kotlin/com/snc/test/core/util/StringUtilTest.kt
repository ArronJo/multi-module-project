package com.snc.test.core.util

import com.snc.zero.core.util.StringUtil
import com.snc.zero.test.base.BaseJUnit5Test
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
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
        val result = StringUtil.print(arrayOf("a", "b", "c"))
        assertEquals("[a|b|c]", result)
    }

    @Test
    fun `print JsonString`() {
        val str =
            """"{ "user_agent": { "family": "Chrome Mobile WebView", "major": "56", "minor": "0", "patch": "2924" }, "os": { "family": "Android", "major": "7", "minor": "0", "patch": "", "patch_minor": "" }, "device": { "family": "Samsung SM-P585N0" } }""""
        println(StringUtil.printJSON(str))
    }

    @Test
    fun `print Serializable 1`() {
        val person = Person("Alice", 30)

        val prettyJson = Json {
            prettyPrint = true
        }

        val jsonString = prettyJson.encodeToString(person)
        println(jsonString)
    }

    @Test
    fun `print Serializable 2`() {
        val jsonInput = """{"name":"Alice","age":30}"""

        val json = Json { ignoreUnknownKeys = true }
        val obj = json.decodeFromString<Person>(jsonInput)

        val prettyJson = Json {
            prettyPrint = true
        }

        val prettyString = prettyJson.encodeToString(obj)
        println(prettyString)
    }

    @Serializable
    data class Person(val name: String, val age: Int)
}
