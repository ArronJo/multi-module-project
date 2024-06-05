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
    fun `데이터 삭제 (Data Reduction) - 주민등록번호1`() {
        // given
        val regNo = "891023-1234567"
        // when
        val v = DeIdentification.DataReduction.regNo(regNo)
        // then
        logger.debug { "regNo: $regNo -> $v" }
        assertEquals(v, "80년대생 남자")
    }

    @Test
    fun `데이터 삭제 (Data Reduction) - 주민등록번호 2`() {
        // given
        val regNo = "891023-2234567"
        // when
        val v = DeIdentification.DataReduction.regNo(regNo)
        // then
        logger.debug { "regNo: $regNo -> $v" }
        assertEquals(v, "80년대생 남자")
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
    fun `데이터 범주화(Data Suppression) - 주소`() {
        // given
        val addr = "제주특별자치도 제주시 광양4길 20 (이도일동)"
        // when
        val v = DeIdentification.DataSuppression.address(addr)
        // then
        logger.debug { "addr: $addr -> $v" }
        assertEquals(v, "제주 거주")
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