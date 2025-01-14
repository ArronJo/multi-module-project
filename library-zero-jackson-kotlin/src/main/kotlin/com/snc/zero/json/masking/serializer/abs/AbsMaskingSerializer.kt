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
        val cardPattern =
            """^(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})$|^(\d{4})[-\s]?(\d{6})[-\s]?(\d{5})$""".toRegex()

        return when {
            idPattern.matches(str) -> {
                val matchResult = idPattern.find(str)
                if (matchResult != null) {
                    val (birthDate, personalNum) = matchResult.destructured
                    "$birthDate-${personalNum.take(1)}******"
                } else {
                    str
                }
            }

            phonePattern.matches(str) -> {
                val matchResult = phonePattern.find(str)
                if (matchResult != null) {
                    val (prefix, _, last) = matchResult.destructured
                    "$prefix-****-$last"
                } else {
                    str
                }
            }

            accountPattern.matches(str) -> {
                val matchResult = accountPattern.find(str)
                if (matchResult != null) {
                    val (first, middle, last) = matchResult.destructured
                    "${first.take(2)}${"*".repeat(first.length - 2)}-" +
                        "${"*".repeat(middle.length)}-" +
                        "${"*".repeat(last.length - 2)}${last.takeLast(2)}"
                } else {
                    str
                }
            }

            cardPattern.matches(str) -> {
                val matchResult = cardPattern.find(str)
                if (matchResult != null) {
                    val groups = matchResult.groupValues.drop(1).filter { it.isNotEmpty() }
                    when (groups.size) {
                        4 -> "${groups[0]}-****-****-${groups[3]}" // 16자리 카드
                        3 -> "${groups[0]}-******-${groups[2]}" // 15자리 카드 (American Express)
                        else -> str
                    }
                } else {
                    str
                }
            }

            else -> str
        }
    }
}
