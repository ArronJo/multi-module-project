package com.snc.test.json.masking

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.snc.zero.json.masking.MaskingAnnotationIntrospector
import com.snc.zero.json.masking.annotations.MaskAllFields
import com.snc.zero.json.masking.annotations.MaskField
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class MaskingAnnotationTest : BaseJUnit5Test() {

    @MaskAllFields
    data class SensitiveData(
        val field1: String,
        val field2: String,
        val field3: Int,
        val nestedObject: NestedObject
    )

    data class PartiallyMaskedData(
        val publicField: String,
        @MaskField
        val sensitiveField: String
    )

    @MaskAllFields
    data class ComplexData(
        val stringField: String,
        val intField: Int,
        val doubleField: Double,
        val booleanField: Boolean,
        val listField: List<String>,
        val mapField: Map<String, Any>,
        val nestedObject: NestedObject,
        val enumField: SampleEnum
    )

    data class NestedObject(
        val nestedString: String,
        val nestedInt: Int
    )

    enum class SampleEnum {
        VALUE1
    }

    @Test
    fun `MaskedModule 테스트`() {
        val objectMapper = ObjectMapper().apply {
            registerKotlinModule()
            setAnnotationIntrospector(MaskingAnnotationIntrospector())
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }

        val complexData = ComplexData(
            stringField = "secret",
            intField = 42,
            doubleField = 3.14,
            booleanField = true,
            listField = listOf("900101-1234567", "010-1234-5678"),
            mapField = mapOf("key1" to 1, "key2" to "123456-78-901234", "key3" to "1234-5678-9012-3456"),
            nestedObject = NestedObject("9001011234567", 100),
            enumField = SampleEnum.VALUE1
        )
        logger.debug { complexData.toString() }

        logger.debug { objectMapper.writeValueAsString(complexData) }

        val sensitiveData = SensitiveData("secret1", "secret2", 42, NestedObject("a", 1))
        val partiallyMaskedData = PartiallyMaskedData("public", "secret")

        logger.debug { objectMapper.writeValueAsString(sensitiveData) }
        // 출력: {"field1":"*****","field2":"*****","field3":0}

        logger.debug { objectMapper.writeValueAsString(partiallyMaskedData) }
        // 출력: {"publicField":"public","sensitiveField":"*****"}
    }
}
