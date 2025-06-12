package com.snc.test.core.json

import com.google.gson.annotations.SerializedName
import com.snc.zero.core.json.JsonConverter
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
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
}
