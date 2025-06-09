package com.snc.test.extension.map

import com.snc.zero.extension.map.get
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

class HashMapExtTest : BaseJUnit5Test() {

    @Test
    fun `should return value when key exists`() {
        // given
        val map = hashMapOf<String, Any>(
            "name" to "ChatGPT",
            "age" to 3
        )

        // when
        val name = map.get("name", "Unknown")
        val age = map.get("age", 0)

        // then
        assertEquals("ChatGPT", name)
        assertEquals(3, age)
    }

    @Test
    fun `should return default when key does not exist`() {
        // given
        val map = hashMapOf<String, Any>(
            "language" to "Kotlin"
        )

        // when
        val value = map.get("framework", "Spring")

        // then
        assertEquals("Spring", value)
    }

    @Test
    fun `should return null if value is explicitly null`() {
        // given
        val map = hashMapOf<String, Any?>(
            "key1" to null
        ) as HashMap<String, *>

        // when
        val result = map.get("key1", "default")

        // then
        assertEquals("default", result)
    }
}
