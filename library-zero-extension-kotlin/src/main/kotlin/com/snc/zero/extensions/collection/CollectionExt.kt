package com.snc.zero.extensions.collection

@Suppress("UNCHECKED_CAST")
inline fun <reified T> List<*>.asListOfType(): List<T>? =
    if (all { it is T }) {
        this as List<T>
    } else {
        null
    }

fun <T> Iterable<T>.toArrayList(): ArrayList<T> {
    if (this is Collection) {
        return when (size) {
            0 -> ArrayList()
            1 -> ArrayList<T>().apply {
                add(if (this@toArrayList is List) get(0) else iterator().next())
            }
            else -> ArrayList(this)
        }
    }
    return ArrayList<T>().apply { addAll(this@toArrayList) }
}
