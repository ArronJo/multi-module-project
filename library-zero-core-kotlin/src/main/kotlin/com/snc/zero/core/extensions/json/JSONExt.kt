package com.snc.zero.core.extensions.json

import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import java.io.StringWriter

fun Any.toJsonString(): String {
    val builder = GsonBuilder()
    val gson = builder.create()
    val jsonWriter = JsonWriter(StringWriter())
    jsonWriter.setIndent("  ")
    gson.toJson(this, this.javaClass, jsonWriter)
    return gson.toJson(this)
}

fun <T> String.toObject(clazz: Class<T>): T? {
    try {
        val builder = GsonBuilder()
        val gson = builder.create()
        return gson.fromJson(this, clazz)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
