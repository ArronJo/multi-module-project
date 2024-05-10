package com.snc.zero.test.counter

class Counter {

    private val countMap = hashMapOf<String, Int>()

    fun put(key: String) {
        var value = countMap[key]
        if (null == value) {
            value = 0
        }
        countMap[key] = value + 1
    }

    fun get(key: String): Int {
        return countMap[key] ?: return 0
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