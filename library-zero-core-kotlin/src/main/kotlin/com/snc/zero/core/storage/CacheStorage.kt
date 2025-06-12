package com.snc.zero.core.storage

import com.snc.zero.core.date.DateTimeParser

/**
 * 메모리 캐시 저장소
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object CacheStorage {

    private val cache = HashMap<String, String>()

    private fun key(group: String = "", key: String): String {
        if (group.isNotEmpty()) {
            return "$group#$key"
        }
        return key
    }

    fun putString(group: String = "", key: String, value: String) {
        val groupKey = key(group, key)
        cache[groupKey] = "$value;" + DateTimeParser.today("yyyyMMddHHmmss")
    }

    fun getString(group: String = "", key: String, defaultValue: String = ""): String {
        val groupKey = key(group, key)
        if (!cache.contains(groupKey)) {
            return defaultValue
        }
        return (cache[groupKey] as String).split(";")[0]
    }

    fun putInt(group: String, key: String, value: Int) {
        putString(group, key, "$value")
    }

    fun getInt(group: String, key: String, defaultValue: Int): Int {
        return getString(group, key, "$defaultValue").toInt()
    }

    fun remove(key: String): Boolean {
        if (cache.contains(key)) {
            cache.remove(key)
            return true
        }
        return false
    }
}
