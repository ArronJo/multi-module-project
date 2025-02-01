package com.snc.test.json.localdatetime

import com.snc.zero.json.localdatetime.serializer.LocalDateTimeAsDateSerializer
import com.snc.zero.test.base.BaseJUnit5Test
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
}
