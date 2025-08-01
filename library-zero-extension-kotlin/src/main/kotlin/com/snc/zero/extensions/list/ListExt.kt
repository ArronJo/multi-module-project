package com.snc.zero.extensions.list

fun <T> List<T>.getOrDefault(index: Int, defaultValue: Any?): Any? {
    try {
        if (this.isEmpty()) {
            return defaultValue
        }
        return this[index]
    } catch (_: Exception) {
        return defaultValue
    }
}

fun <T> List<T>.getShuffledItem(): T {
    return this.shuffled()[0]
}
