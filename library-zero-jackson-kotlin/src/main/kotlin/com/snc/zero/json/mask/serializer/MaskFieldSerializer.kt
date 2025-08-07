package com.snc.zero.json.mask.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.snc.zero.json.mask.serializer.abs.AbsMaskSerializer

/**
 * 단건 필드 마스킹 처리
 */
class MaskFieldSerializer : AbsMaskSerializer<Any>() {

    override fun serialize(value: Any?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeObject(maskIfNeeded(value))
    }
}
