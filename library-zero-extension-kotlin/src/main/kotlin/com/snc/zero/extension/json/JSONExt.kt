package com.snc.zero.extension.json

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonWriter
import java.io.StringWriter
import kotlin.jvm.Throws

fun Any.toJsonString(): String {
    val builder = GsonBuilder()
    val gson = builder.create()
    val jsonWriter = JsonWriter(StringWriter())
    jsonWriter.setIndent("  ")
    gson.toJson(this, this.javaClass, jsonWriter)
    return gson.toJson(this)
}

@Throws(JsonSyntaxException::class)
fun <T> String.toObject(clazz: Class<T>): T? {
    val builder = GsonBuilder()
    val gson = builder.create()
    return gson.fromJson(this, clazz)
}
