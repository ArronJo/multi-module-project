package com.snc.zero.json.mask.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.snc.zero.json.mask.serializer.abs.AbsMaskSerializer

/**
 * 클래스 내 필드 마스킹 처리
 */
class MaskAllFieldsSerializer : AbsMaskSerializer<Any?>() {

    override fun serialize(value: Any?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        value?.let { obj ->
            obj::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                gen.writeFieldName(field.name)
                val fieldValue = field[obj]
                gen.writeObject(maskIfNeeded(fieldValue))
            }
        }
        gen.writeEndObject()
    }
}
