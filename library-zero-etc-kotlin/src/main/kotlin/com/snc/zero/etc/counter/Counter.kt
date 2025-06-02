package com.snc.zero.etc.counter

import kotlin.collections.iterator

class Counter {
    private val countMap = hashMapOf<String, Int>()

    fun put(key: String) {
        val value = countMap[key]
        if (null == value) {
            countMap[key] = 1
            return
        }
        countMap[key] = value + 1
    }

    fun get(key: String): Int {
        return countMap.getOrDefault(key, 0)
    }

    fun get(): HashMap<String, Int> {
        return countMap
    }

    fun size(): Int {
        return countMap.size
    }

    fun total(): Int {
        var cnt = 0
        for (obj in countMap) {
            cnt += get(obj.key)
        }
        return cnt
    }

    override fun toString(): String {
        return countMap.toString()
    }
}
