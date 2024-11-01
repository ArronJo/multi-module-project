package com.snc.zero.core.extensions.text

import java.util.*
import kotlin.collections.HashMap

fun <T> Map<String, T>.toQueryString(): String {
    val sb = StringBuilder()
    for ((key, value) in this) {
        if (sb.isNotEmpty()) {
            sb.append("&")
        }
        sb.append(String.format(Locale.getDefault(), "%s=%s", key, value))
    }
    return sb.toString()
}

fun String.getQueryStringValue(key: String): String {
    val map = queryStringToMap()
    return map.getOrDefault(key, "")
}

fun String.queryStringToMap(): HashMap<String, String> {
    val sp = this.split('?')
    val str = if (sp.size > 1) {
        sp[1]
    } else {
        sp[0]
    }

    val map = HashMap<String, String>()
    val qs = str.split("&")
    for (s in qs) {
        val arr = s.split("=")
        map[arr[0]] = arr[1]
    }
    return map
}
