package com.snc.test.json.localdatetime

import com.snc.zero.json.localdatetime.serializer.LocalDateTimeAsDateSerializer
import com.snc.zero.test.base.BaseJUnit5Test
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class LocalDateTimeTest : BaseJUnit5Test() {

    @Serializable
    data class MyData(
        @Serializable(with = LocalDateTimeAsDateSerializer::class)
        val date: LocalDateTime
    )

    @Test
    fun `LocalDateTime 테스트 1`() {
        val myData = MyData(LocalDateTime(2023, 11, 6, 20, 0))
        val json = Json.encodeToString(myData)
        println(json) // 출력: {"date":"2023-11-06"}
        assertEquals("{\"date\":\"2023-11-06\"}", json)
    }

    @Serializable
    data class TestDto(
        @Serializable(with = LocalDateTimeAsDateSerializer::class)
        val date: LocalDateTime
    )

    private val json = Json

    @Test
    fun `should serialize LocalDateTime to yyyy-MM-dd string`() {
        val dto = TestDto(LocalDateTime(2024, 6, 12, 15, 30))
        val serialized = json.encodeToString(dto)

        assertEquals("""{"date":"2024-06-12"}""", serialized)
    }

    @Test
    fun `should deserialize yyyy-MM-dd string to LocalDateTime`() {
        val input = """{"date":"2024-06-12"}"""
        val dto = json.decodeFromString<TestDto>(input)

        assertEquals(2024, dto.date.year)
        assertEquals(6, dto.date.monthNumber)
        assertEquals(12, dto.date.dayOfMonth)
        assertEquals(0, dto.date.hour)
        assertEquals(0, dto.date.minute)
    }

    @Test
    fun `should throw exception for invalid date format`() {
        val invalidJson = """{"date":"2024/06/12"}"""
        assertThrows(Exception::class.java) {
            json.decodeFromString<TestDto>(invalidJson)
        }
    }
}
