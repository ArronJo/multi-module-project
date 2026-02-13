package com.snc.zero.crypto.canonical

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

/**
 * Canonical Json Util
 *
 * dependencies {
 *     implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
 *     implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")
 * }
 */
object CanonicalJsonUtil {

    val mapper: ObjectMapper =
        JsonMapper.builder()
            // Kotlin 지원
            .addModule(KotlinModule.Builder().build())
            // Java Time 지원
            .addModule(JavaTimeModule())
            // null 제거
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            // ✅ 필드명 알파벳 정렬 (정답)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            // ✅ Map Key 정렬
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            // pretty print 제거
            .disable(SerializationFeature.INDENT_OUTPUT)
            .build()

    fun toCanonical(obj: Any): String {
        return mapper.writeValueAsString(obj)
    }
}
