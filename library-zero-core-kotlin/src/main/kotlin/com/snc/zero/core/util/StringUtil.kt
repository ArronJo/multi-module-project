package com.snc.zero.core.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * 문자열 유틸리티
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class StringUtil private constructor() {

    companion object {

        fun toString(arr: Array<String>): String {
            val buff = StringBuilder()
            buff.append("[")
            for (v in arr) {
                buff.append(v).append(",")
            }
            if (buff.length > 1) {
                buff.deleteCharAt(buff.length - 1)
            }
            buff.append("]")
            return buff.toString()
        }

        fun join(arr: Array<String>, separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = ""): String {
            return arr.joinToString(prefix = prefix, separator = separator, postfix = postfix)
        }

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
