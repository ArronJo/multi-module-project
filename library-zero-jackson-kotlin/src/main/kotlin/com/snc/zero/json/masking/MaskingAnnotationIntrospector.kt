package com.snc.zero.json.masking

import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.snc.zero.json.masking.annotations.MaskAllFields
import com.snc.zero.json.masking.annotations.MaskField
import com.snc.zero.json.masking.serializer.MaskAllFieldsSerializer
import com.snc.zero.json.masking.serializer.MaskFieldSerializer

/**
 * Jackson JSON Annotation 인터셉터
 *
 * usage:
 *
 *  val objectMapper = ObjectMapper().apply {
 *      registerKotlinModule()
 *      setAnnotationIntrospector(MaskingAnnotationIntrospector())
 *      setSerializationInclusion(JsonInclude.Include.NON_NULL)
 *  }
 *
 *  val sensitiveData = SensitiveData("secret1", "secret2", 42, NestedObject("a",1))
 *  val partiallyMaskedData = PartiallyMaskedData("public", "secret")
 *
 *  logger.debug { objectMapper.writeValueAsString(sensitiveData) }
 *
 */
class MaskingAnnotationIntrospector : JacksonAnnotationIntrospector() {

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
