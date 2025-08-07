package com.snc.zero.json.mask

import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.snc.zero.json.mask.annotations.MaskAllFields
import com.snc.zero.json.mask.annotations.MaskField
import com.snc.zero.json.mask.serializer.MaskAllFieldsSerializer
import com.snc.zero.json.mask.serializer.MaskFieldSerializer

/**
 * Jackson JSON Annotation 인터셉터
 *
 * Usage)
 *
 *  val objectMapper = ObjectMapper().apply {
 *      registerKotlinModule()
 *      setAnnotationIntrospector(MaskAnnotationIntrospector())
 *      setSerializationInclusion(JsonInclude.Include.NON_NULL)
 *  }
 *
 *  val sensitiveData = SensitiveData("secret1", "secret2", 42, NestedObject("a",1))
 *  val partiallyMaskedData = PartiallyMaskedData("public", "secret")
 *
 *  logger.debug { objectMapper.writeValueAsString(sensitiveData) }
 *
 */
class MaskAnnotationIntrospector : JacksonAnnotationIntrospector() {

    override fun findSerializer(a: Annotated?): Any? {
        if (a?.hasAnnotation(MaskField::class.java) == true) {
            return MaskFieldSerializer()
        }
        if (a?.rawType?.isAnnotationPresent(MaskAllFields::class.java) == true) {
            return MaskAllFieldsSerializer()
        }
        return super.findSerializer(a)
    }
}
