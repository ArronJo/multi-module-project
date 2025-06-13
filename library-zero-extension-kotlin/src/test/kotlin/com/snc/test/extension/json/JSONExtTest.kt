package com.snc.test.extension.json

import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.snc.zero.extension.json.toJsonString
import com.snc.zero.extension.json.toObject
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class JSONExtTest : BaseJUnit5Test() {

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
    fun `JSON toJsonString 테스트 1`() {
        // given
        val data = NoticeListDto(
            embedded = "abc",
            links = null,
            noticeDto = NoticeDto(title = "T", body = "B")
        )
        // when
        val v1 = data.toJsonString()
        // then
        logger.debug { "JSON toJsonString 1 결과: $data -> $v1" }
    }

    @Test
    fun `JSON toObject 테스트 1`() {
        // given
        val data = "{\"_embedded\":\"abc\",\"noticeDto\":{\"title\":\"T\",\"body\":\"B\"}}"
        // when
        val v1 = data.toObject(NoticeListDto::class.java)
        // then
        logger.debug { "JSON toObject 1 결과: $data -> $v1" }
    }

    @Test
    fun `JSON toObject 테스트 2`() {
        assertThrows(JsonSyntaxException::class.java) {
            val data = "{\"_embedded\":\"abc\",},}"
            data.toObject(NoticeListDto::class.java)
        }
    }
}
