package com.snc.zero.extension.list

fun <T> List<T>.get(index: Int, defaultValue: Any? = null): Any? {
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
