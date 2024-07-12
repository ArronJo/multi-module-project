package com.snc.test.identification.deidentification2

import com.snc.zero.identification.deidentification2.DeIdentification
import com.snc.zero.identification.faker.Faker
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DeIdentificationTest : BaseJUnit5Test() {

    companion object {
        private const val MSG_A_MAN_BORN_IN_THE_80S = "80년대생 남자"
        private const val MSG_A_WOMAN_BORN_IN_THE_80S = "80년대생 여자"
    }

    @Test
    fun `가명처리 (Pseudonymization) - 이름 1-1`() {
        // given
        val lastName = "김"
        val firstName = "가연"
        // when
        val v = DeIdentification.Pseudonymization.name(firstName, lastName, Faker.ProviderType.KOREAN, female = true)
        // then
        logger.debug { "name: ${lastName + firstName} -> ${v[1]}${v[0]}" }
        assertNotEquals("", v)
    }

    @Test
    fun `가명처리 (Pseudonymization) - 이름 1-2`() {
        // given
        val lastName = "김"
        val firstName = "가연"
        // when
        val v = DeIdentification.Pseudonymization.name(firstName, lastName, Faker.ProviderType.KOREAN)
        // then
        logger.debug { "name: ${lastName + firstName} -> ${v[1]}${v[0]}" }
        assertNotEquals("", v)
    }

    @Test
    fun `가명처리 (Pseudonymization) - 이름 1-3`() {
        // given
        val lastName = "김"
        val firstName = "가연"
        // when
        val v = DeIdentification.Pseudonymization.name(firstName, lastName)
        // then
        logger.debug { "name: ${lastName + firstName} -> ${v[1]}${v[0]}" }
        assertNotEquals("", v)
    }

    @Test
    fun `가명처리 (Pseudonymization) - 이름 2-1`() {
        // given
        val lastName = "Jones"
        val firstName = "Mary"
        // when
        val v = DeIdentification.Pseudonymization.name(firstName, lastName, Faker.ProviderType.ENGLISH, female = true)
        // then
        logger.debug { "name: $firstName $lastName -> ${v[0]} ${v[1]}" }
        assertNotEquals("", v)
    }

    @Test
    fun `가명처리 (Pseudonymization) - 이름 2-2`() {
        // given
        val lastName = "Jones"
        val firstName = "Mary"
        // when
        val v = DeIdentification.Pseudonymization.name(firstName, lastName, Faker.ProviderType.ENGLISH)
        // then
        logger.debug { "name: ${lastName + firstName} -> ${v[0]} ${v[1]}" }
        assertNotEquals("", v)
    }

    @Test
    fun `가명처리 (Pseudonymization) - 나이`() {
        // given
        val age = 37
        // when
        val v = DeIdentification.Pseudonymization.age(age)
        // then
        logger.debug { "age: $age -> $v" }
        assertEquals("30 대", v)
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 주민등록번호 1`() {
        // given
        val regNo1 = "891023-1234567"
        val regNo3 = "891023-3234567"
        val regNo5 = "891023-5234567"
        val regNo7 = "891023-7234567"
        val regNo9 = "891023-9234567"
        // when
        val v1 = DeIdentification.DataReduction.regNo(regNo1)
        val v3 = DeIdentification.DataReduction.regNo(regNo3)
        val v5 = DeIdentification.DataReduction.regNo(regNo5)
        val v7 = DeIdentification.DataReduction.regNo(regNo7)
        val v9 = DeIdentification.DataReduction.regNo(regNo9)
        // then
        logger.debug { "regNo: $regNo1 -> $v1" }
        assertEquals(MSG_A_MAN_BORN_IN_THE_80S, v1)

        logger.debug { "regNo: $regNo3 -> $v3" }
        assertEquals(MSG_A_MAN_BORN_IN_THE_80S, v3)

        logger.debug { "regNo: $regNo5 -> $v5" }
        assertEquals(MSG_A_MAN_BORN_IN_THE_80S, v5)

        logger.debug { "regNo: $regNo7 -> $v7" }
        assertEquals(MSG_A_MAN_BORN_IN_THE_80S, v7)

        logger.debug { "regNo: $regNo9 -> $v9" }
        assertEquals(MSG_A_MAN_BORN_IN_THE_80S, v9)
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 주민등록번호 2`() {
        // given
        val regNo2 = "891023-2234567"
        val regNo4 = "891023-4234567"
        val regNo6 = "891023-6234567"
        val regNo8 = "891023-8234567"
        val regNo0 = "891023-0234567"
        // when
        val v2 = DeIdentification.DataReduction.regNo(regNo2)
        val v4 = DeIdentification.DataReduction.regNo(regNo4)
        val v6 = DeIdentification.DataReduction.regNo(regNo6)
        val v8 = DeIdentification.DataReduction.regNo(regNo8)
        val v0 = DeIdentification.DataReduction.regNo(regNo0)
        // then
        logger.debug { "regNo: $regNo2 -> $v2" }
        assertEquals(MSG_A_WOMAN_BORN_IN_THE_80S, v2)

        logger.debug { "regNo: $regNo4 -> $v4" }
        assertEquals(MSG_A_WOMAN_BORN_IN_THE_80S, v4)

        logger.debug { "regNo: $regNo6 -> $v6" }
        assertEquals(MSG_A_WOMAN_BORN_IN_THE_80S, v6)

        logger.debug { "regNo: $regNo8 -> $v8" }
        assertEquals(MSG_A_WOMAN_BORN_IN_THE_80S, v8)

        logger.debug { "regNo: $regNo0 -> $v0" }
        assertEquals(MSG_A_WOMAN_BORN_IN_THE_80S, v0)
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 생년월일 1`() {
        // given
        val birth = "19891023"
        // when
        val v = DeIdentification.DataReduction.birth(birth)
        // then
        logger.debug { "birth: $birth -> $v" }
        assertEquals("1980년대생", v)
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 생년월일 2`() {
        // given
        val birth = "891023"
        // when
        val v = DeIdentification.DataReduction.birth(birth)
        // then
        logger.debug { "birth: $birth -> $v" }
        assertEquals("80년대생", v)
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 생년월일 3`() {
        // given
        val birth = "891023123123"
        // when
        val e = assertThrows(
            IllegalArgumentException::class.java
        ) {
            DeIdentification.DataReduction.birth(birth)
        }
        // then
        assertNotEquals("", e.message)
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 이름`() {
        // given
        val name = "진가연"
        // when
        val v = DeIdentification.DataSuppression.name(name)
        // then
        logger.debug { "name: $name -> $v" }
        assertEquals("진씨", v)
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 나이`() {
        // given
        val age = 37
        // when
        val v = DeIdentification.DataSuppression.age(age)
        // then
        logger.debug { "age: $age -> $v" }
        assertEquals("30~40세", v)
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 주소 1`() {
        // given
        val addr = "제주특별자치도 제주시 광양4길 20 (이도일동)"
        // when
        val v = DeIdentification.DataSuppression.address(addr)
        // then
        logger.debug { "addr: $addr -> $v" }
        assertEquals("제주 거주", v)
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 주소 2`() {
        // given
        val addr = "저세상도 이세상시 광양18길 28 (삽실팔동)"
        // when
        val v = DeIdentification.DataSuppression.address(addr)
        // then
        logger.debug { "addr: $addr -> $v" }
        assertEquals("거주지 미확인", v)
    }

    @Test
    fun `데이터 마스킹 (Data Masking) - 이름`() {
        // given
        val name = "진가연"
        // when
        val v = DeIdentification.DataMasking.name(name)
        // then
        logger.debug { "name: $name -> $v" }
        assertEquals("진◯◯", v)
    }

    @Test
    fun `name should not start with given first or last name initial`() {
        val (generatedFirstName, generatedLastName) = DeIdentification.Pseudonymization.name("John", "Doe")
        assertFalse(generatedFirstName.lowercase().startsWith("j"))
        assertFalse(generatedLastName.lowercase().startsWith("d"))
    }

    @Test
    fun `name should work for female names`() {
        val (generatedFirstName, generatedLastName) = DeIdentification.Pseudonymization.name("Emma", "Watson", female = true)
        assertFalse(generatedFirstName.lowercase().startsWith("e"))
        assertFalse(generatedLastName.lowercase().startsWith("w"))
        assertTrue(generatedFirstName.isNotEmpty())
        assertTrue(generatedLastName.isNotEmpty())
    }

    @Test
    fun `name should handle long names`() {
        val (generatedFirstName, generatedLastName) = DeIdentification.Pseudonymization.name("Constantinopolis", "Charlemagneson")
        assertFalse(generatedFirstName.lowercase().startsWith("c"))
        assertFalse(generatedLastName.lowercase().startsWith("c"))
    }

    @Test
    fun `name should handle names with special characters`() {
        val (generatedFirstName, generatedLastName) = DeIdentification.Pseudonymization.name("Æon", "Übel")
        assertFalse(generatedFirstName.lowercase().startsWith("æ"))
        assertFalse(generatedLastName.lowercase().startsWith("ü"))
    }

    @Test
    fun `name should handle single-character names`() {
        val (generatedFirstName, generatedLastName) = DeIdentification.Pseudonymization.name("A", "B")
        assertFalse(generatedFirstName.lowercase().startsWith("a"))
        assertFalse(generatedLastName.lowercase().startsWith("b"))
    }
}