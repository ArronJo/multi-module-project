package com.snc.test.core.json

import com.google.gson.annotations.SerializedName
import com.snc.zero.core.json.JsonConverter
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class JsonConverterTest : BaseJUnit5Test() {

    data class NoticeListDto(
        @SerializedName("_embedded") var embedded: String,
        @SerializedName("_links") var links: String?,
        val noticeDto: NoticeDto
    )

    data class NoticeDto(
        @SerializedName("title") var title: String,
        @SerializedName("body") var body: String?,
    )

    @Test
    fun `JsonConverter toJsonString 테스트 1`() {
        // given
        val data = NoticeListDto(embedded = "abc", links = "XYZ", noticeDto = NoticeDto(title = "T", body = "B"))
        // when
        val v1 = JsonConverter.toJsonString(data)
        // then
        logger.debug { "JsonConverter toJsonString 1 결과: $data -> $v1" }
        //assertEquals(v1, false)
    }

    @Test
    fun `JsonConverter toObject 테스트 1`() {
        // given
        val data = "{\"_embedded\":\"abc\",\"_links\":\"XYZ\",\"noticeDto\":{\"title\":\"T\",\"body\":\"B\"}}"
        // when
        val v1 = JsonConverter.toObject(data, NoticeListDto::class.java)
        // then
        logger.debug { "JsonConverter toObject 1 결과: $data -> $v1" }
        //assertEquals(v1, false)
    }

    data class Person(val name: String, val age: Int?)

    @Test
    fun `toJsonString should convert object to JSON string`() {
        val person = Person("Alice", 30)
        val json = JsonConverter.toJsonString(person)

        assertTrue(json.contains("Alice"))
        assertTrue(json.contains("30"))
    }

    @Test
    fun `toJsonString should include nulls when serializeNulls is enabled`() {
        val person = Person("Bob", null)
        val json = JsonConverter.toJsonString(person)

        assertTrue(json.contains("Bob"))
        assertTrue(json.contains("null"))
    }

    @Test
    fun `toObject should parse valid JSON into object`() {
        val json = """{ "name": "Charlie", "age": 25 }"""
        val person = JsonConverter.toObject(json, Person::class.java)

        assertNotNull(person)
        assertEquals("Charlie", person?.name)
        assertEquals(25, person?.age)
    }

    @Test
    fun `toObject should return null for invalid JSON`() {
        val invalidJson = """{ name: "Invalid", age: }""" // malformed
        val person = JsonConverter.toObject(
            invalidJson,
            Person::class.java
        )

        assertNull(person)
    }

    @Test
    fun `toObject should return null for mismatched class type`() {
        val json = """{ "name": "Mismatch", "age": "notAnInt" }""" // age type mismatch
        val person = JsonConverter.toObject(
            json,
            Person::class.java
        )

        assertNull(person)
    }
}
