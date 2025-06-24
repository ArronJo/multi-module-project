package com.snc.zero.extensions.collection

@Suppress("UNCHECKED_CAST")
inline fun <reified T> List<*>.asListOfType(): List<T>? =
    if (all { it is T }) {
        this as List<T>
    } else {
        null
    }
