package com.snc.zero.core.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class PrintUtil {

    companion object {
        fun print(arr: Array<String>): String {
            return arr.joinToString(prefix = "[", separator = "|", postfix = "]")
        }

        fun printJSON(input: String): String {
            val clean = input.trim().removeSurrounding("\"")
            val parsed: JsonElement = Json.parseToJsonElement(clean)
            val prettyJson = Json { prettyPrint = true }
            return prettyJson.encodeToString(JsonElement.serializer(), parsed)
        }
    }
}
