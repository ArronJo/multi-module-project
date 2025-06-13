package com.snc.zero.core.json

import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import com.snc.zero.logger.jvm.TLogging
import java.io.StringWriter

private val logger = TLogging.logger { }

/**
 * JSON 유틸리티
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class JsonConverter private constructor() {

    companion object {

        fun <T> toObject(jsonString: String, clazz: Class<T>): T? {
            try {
                val gson = GsonBuilder()
                    //.serializeNulls()     // null 포함
                    .create()
                return gson.fromJson(jsonString, clazz)
            } catch (_: Exception) {
                logger.error { "Exception while parsing $jsonString" }
            }
            return null
        }

        fun toJsonString(obj: Any): String {
            val gson = GsonBuilder()
                .serializeNulls() // null 포함
                .create()
            val jsonWriter = JsonWriter(StringWriter())
            jsonWriter.setIndent("  ")
            jsonWriter.serializeNulls = true
            gson.toJson(obj, obj.javaClass, jsonWriter)
            return gson.toJson(obj)
        }
    }
}
