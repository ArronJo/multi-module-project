package com.snc.test.identification.deidentification2

import com.snc.zero.identification.deidentification2.DeIdentification
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DeIdentificationTest : BaseJUnit5Test() {

    @Test
    fun `가명처리 (Pseudonymization) - 이름`() {
        // given
        val name = "진가연"
        // when
        val v = DeIdentification.Pseudonymization.name(name)
        // then
        logger.debug { "name: $name -> $v" }
        assertNotEquals(v, "")
    }

    @Test
    fun `가명처리 (Pseudonymization) - 나이`() {
        // given
        val age = 37
        // when
        val v = DeIdentification.Pseudonymization.age(age)
        // then
        logger.debug { "age: $age -> $v" }
        assertEquals(v, "30 대")
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
        assertEquals(v1, "80년대생 남자")

        logger.debug { "regNo: $regNo3 -> $v3" }
        assertEquals(v3, "80년대생 남자")

        logger.debug { "regNo: $regNo5 -> $v5" }
        assertEquals(v5, "80년대생 남자")

        logger.debug { "regNo: $regNo7 -> $v7" }
        assertEquals(v7, "80년대생 남자")

        logger.debug { "regNo: $regNo9 -> $v9" }
        assertEquals(v9, "80년대생 남자")
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
        assertEquals(v2, "80년대생 여자")

        logger.debug { "regNo: $regNo4 -> $v4" }
        assertEquals(v2, "80년대생 여자")

        logger.debug { "regNo: $regNo6 -> $v6" }
        assertEquals(v2, "80년대생 여자")

        logger.debug { "regNo: $regNo8 -> $v8" }
        assertEquals(v2, "80년대생 여자")

        logger.debug { "regNo: $regNo0 -> $v0" }
        assertEquals(v2, "80년대생 여자")
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 생년월일 1`() {
        // given
        val birth = "19891023"
        // when
        val v = DeIdentification.DataReduction.birth(birth)
        // then
        logger.debug { "birth: $birth -> $v" }
        assertEquals(v, "1980년대생")
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 생년월일 2`() {
        // given
        val birth = "891023"
        // when
        val v = DeIdentification.DataReduction.birth(birth)
        // then
        logger.debug { "birth: $birth -> $v" }
        assertEquals(v, "80년대생")
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
        assertNotEquals(e.message, "")
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 이름`() {
        // given
        val name = "진가연"
        // when
        val v = DeIdentification.DataSuppression.name(name)
        // then
        logger.debug { "name: $name -> $v" }
        assertEquals(v, "진씨")
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 나이`() {
        // given
        val age = 37
        // when
        val v = DeIdentification.DataSuppression.age(age)
        // then
        logger.debug { "age: $age -> $v" }
        assertEquals(v, "30~40세")
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 주소 1`() {
        // given
        val addr = "제주특별자치도 제주시 광양4길 20 (이도일동)"
        // when
        val v = DeIdentification.DataSuppression.address(addr)
        // then
        logger.debug { "addr: $addr -> $v" }
        assertEquals(v, "제주 거주")
    }

    @Test
    fun `데이터 범주화(Data Suppression) - 주소 2`() {
        // given
        val addr = "저세상도 이세상시 광양18길 28 (삽실팔동)"
        // when
        val v = DeIdentification.DataSuppression.address(addr)
        // then
        logger.debug { "addr: $addr -> $v" }
        assertEquals(v, "거주지 미확인")
    }

    @Test
    fun `데이터 마스킹 (Data Masking) - 이름`() {
        // given
        val name = "진가연"
        // when
        val v = DeIdentification.DataMasking.name(name)
        // then
        logger.debug { "name: $name -> $v" }
        assertEquals(v, "진◯◯")
    }

}