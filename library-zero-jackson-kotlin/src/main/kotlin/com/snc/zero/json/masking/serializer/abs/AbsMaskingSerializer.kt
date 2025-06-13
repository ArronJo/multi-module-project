package com.snc.zero.json.masking.serializer.abs

import com.fasterxml.jackson.databind.JsonSerializer
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

abstract class AbsMaskingSerializer<T> : JsonSerializer<T>() {

    fun maskIfNeeded(obj: Any?): Any? {
        return when {
            obj == null -> null
            obj::class.isSubclassOf(String::class) -> masking(obj as String)
            obj::class.isSubclassOf(Number::class) -> obj
            obj::class.isSubclassOf(Boolean::class) -> obj
            obj::class.isSubclassOf(Char::class) -> obj
            obj::class.isSubclassOf(Enum::class) -> obj
            obj::class.isSubclassOf(Collection::class) -> (obj as Collection<*>).map { maskIfNeeded(it) }
            obj::class.isSubclassOf(Map::class) -> (obj as Map<*, *>).mapValues { maskIfNeeded(it.value) }
            obj::class.isSubclassOf(Array::class) -> (obj as Array<*>).map { maskIfNeeded(it) }.toTypedArray()
            this::class.java.isArray -> Array(java.lang.reflect.Array.getLength(this)) { i ->
                maskIfNeeded(java.lang.reflect.Array.get(this, i))
            }

            else -> makeMaskedObject(obj)
        }
    }

    private fun makeMaskedObject(obj: Any): Any {
        val kClass = obj::class
        val properties = kClass.memberProperties.associateBy { it.name }
        val constructor = kClass.primaryConstructor ?: return obj
        val args = constructor.parameters.associateWith { param ->
            val property = properties[param.name]
            when (val value = property?.getter?.call(obj)) {
                is String -> masking(value)
                is Collection<*> -> value.map { if (it is String) masking(it) else it }
                is Map<*, *> -> value.mapValues { if (it.value is String) masking(it.value as String) else it.value }
                else -> value
            }
        }
        return constructor.callBy(args)
    }
    private fun masking(str: String): String {
        val idPattern = """^(\d{6})[-]?(\d{7})$""".toRegex()
        val phonePattern = """^(01[016789])[-]?(\d{3,4})[-]?(\d{4})$""".toRegex()
        val accountPattern = """^(\d{2,6})[-]?(\d{2,6})[-]?(\d{2,6})$""".toRegex()
        val cardPattern = """^(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})$|^(\d{4})[-\s]?(\d{6})[-\s]?(\d{5})$""".toRegex()

        return when {
            idPattern.matches(str) -> idPattern.find(str)?.let { match ->
                val (birthDate, personalNum) = match.destructured
                "$birthDate-${personalNum.take(1)}******"
            }

            phonePattern.matches(str) -> phonePattern.find(str)?.let { match ->
                val (prefix, _, last) = match.destructured
                "$prefix-****-$last"
            }

            cardPattern.matches(str) -> maskCard(str, cardPattern)

            accountPattern.matches(str) -> accountPattern.find(str)?.let { match ->
                val (first, middle, last) = match.destructured
                "${first.take(2)}${"*".repeat(first.length - 2)}-" +
                    "${"*".repeat(middle.length)}-" +
                    "${"*".repeat(last.length - 2)}${last.takeLast(2)}"
            }

            else -> str
        } ?: str
    }

    private fun maskCard(str: String, pattern: Regex): String {
        val matchResult = pattern.find(str) ?: return str
        return when {
            matchResult.groupValues[1].isNotEmpty() -> {
                // 16자리 카드
                "${matchResult.groupValues[1]}-****-****-${matchResult.groupValues[4]}"
            }
            matchResult.groupValues[5].isNotEmpty() -> {
                // 15자리 카드
                "${matchResult.groupValues[5]}-******-${matchResult.groupValues[7]}"
            }
            else -> str
        }
    }
}
