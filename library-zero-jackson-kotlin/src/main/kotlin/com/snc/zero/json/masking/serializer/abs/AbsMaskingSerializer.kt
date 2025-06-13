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
        // Kept the Two Main Patterns
        // - \d{4}[-\s]?\d{4}[-\s]?\d{4}[-\s]?\d{4} → Matches standard 16-digit card numbers.
        // - \d{4}[-\s]?\d{6}[-\s]?\d{5} → Matches 15-digit card numbers.
        val cardPattern = """^(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})$|^(\d{4})[-\s]?(\d{6})[-\s]?(\d{5})$""".toRegex()

        if (idPattern.matches(str)) {
            val matchResult = idPattern.find(str)
            if (matchResult != null) {
                val (birthDate, personalNum) = matchResult.destructured
                return "$birthDate-${personalNum.take(1)}******"
            }
        }

        if (phonePattern.matches(str)) {
            val matchResult = phonePattern.find(str)
            if (matchResult != null) {
                val (prefix, _, last) = matchResult.destructured
                return "$prefix-****-$last"
            }
        }

        if (cardPattern.matches(str)) {
            val matchResult = cardPattern.find(str)
            if (matchResult != null) {
                // 두 개의 패턴이 OR(|)로 묶여 있으므로 두 그룹 패턴 중 하나만 매칭됨
                return if (matchResult.groupValues[1].isNotEmpty()) {
                    // 16자리 카드번호: 그룹 1~4 사용
                    "${matchResult.groupValues[1]}-****-****-${matchResult.groupValues[4]}"
                } else if (matchResult.groupValues[5].isNotEmpty()) {
                    // 15자리 카드번호: 그룹 5~7 사용
                    "${matchResult.groupValues[5]}-******-${matchResult.groupValues[7]}"
                } else {
                    str
                }
            }
        }

        if (accountPattern.matches(str)) {
            val matchResult = accountPattern.find(str)
            if (matchResult != null) {
                val (first, middle, last) = matchResult.destructured
                return "${first.take(2)}${"*".repeat(first.length - 2)}-" +
                    "${"*".repeat(middle.length)}-" +
                    "${"*".repeat(last.length - 2)}${last.takeLast(2)}"
            }
        }

        return str
    }
}
