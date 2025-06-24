package com.snc.zero.extensions.map

fun HashMap<String, *>.get(key: String, default: Any): Any {
    return get(key) ?: return default
}
