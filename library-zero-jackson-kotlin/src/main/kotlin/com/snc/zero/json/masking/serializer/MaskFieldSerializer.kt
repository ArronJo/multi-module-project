package com.snc.zero.json.masking.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.snc.zero.json.masking.serializer.abs.AbsMaskingSerializer

/**
 * 단건 필드 마스킹 처리
 */
class MaskFieldSerializer : AbsMaskingSerializer<Any>() {

    override fun serialize(value: Any?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeObject(maskIfNeeded(value))
    }
}
