package com.snc.test.json.mask

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.snc.zero.json.mask.serializer.abs.AbsMaskSerializer
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class AbsMaskSerializerTest : BaseJUnit5Test() {

    private lateinit var serializer: AbsMaskSerializer<String>

    @BeforeEach
    fun setUp() {
        serializer = object : AbsMaskSerializer<String>() {
            override fun serialize(
                value: String?,
                gen: JsonGenerator?,
                serializers: SerializerProvider?
            ) {
                // 테스트에서는 serialize를 사용하지 않기 때문에 비워둠
            }
        }
    }

    @Test
    fun `should mask 주민등록번호`() {
        val masked = serializer.maskIfNeeded("900101-1234567")
        assertEquals("900101-1******", masked)
    }

    @Test
    fun `should mask 전화번호`() {
        val masked = serializer.maskIfNeeded("010-1234-5678")
        assertEquals("010-****-5678", masked)
    }

    @Test
    fun `should mask 계좌번호`() {
        val masked = serializer.maskIfNeeded("123456-789012-34")
        assertEquals("12****-******-34", masked)
    }

    @Test
    fun `should mask 신용카드번호 (16자리)`() {
        val masked = serializer.maskIfNeeded("1234-5678-9012-3456")
        assertEquals("1234-****-****-3456", masked)
    }

    @Test
    fun `should mask 신용카드번호 (15자리)`() {
        val masked = serializer.maskIfNeeded("1234-567890-12345")
        assertEquals("1234-******-12345", masked)
    }

    @Test
    fun `should not mask 일반 문자열`() {
        val original = "HelloWorld123!"
        val masked = serializer.maskIfNeeded(original)
        assertEquals(original, masked)
    }

    @Test
    fun `should mask String 컬렉션`() {
        val list = listOf("900101-1234567", "010-1234-5678")
        val masked = serializer.maskIfNeeded(list) as List<*>
        assertEquals(listOf("900101-1******", "010-****-5678"), masked)
    }

    @Test
    fun `should mask Map String, String`() {
        val input = mapOf(
            "ssn" to "900101-1234567",
            "phone" to "010-1234-5678"
        )
        val masked = serializer.maskIfNeeded(input) as Map<*, *>
        assertEquals("900101-1******", masked["ssn"])
        assertEquals("010-****-5678", masked["phone"])
    }

    data class Person(val name: String, val ssn: String)

    @Test
    fun `should mask data class property (주민등록번호)`() {
        val person = Person("홍길동", "900101-1234567")
        val masked = serializer.maskIfNeeded(person) as Person
        assertEquals("900101-1******", masked.ssn)
        assertEquals("홍길동", masked.name)
    }
}
