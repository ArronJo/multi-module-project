package com.snc.test.identification.deidentification

import com.snc.zero.identification.deidentification.DeIdentification
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DeIdentificationTest : BaseJUnit5Test() {

    @Test
    fun `이름 - 데이터 범주화(Data Suppression)`() {
        // given
        val name = "진가연"
        // when
        val v = DeIdentification.name(name)
        // then
        logger.debug { "name: $name -> $v" }
        assertEquals(v, "진씨")
    }

    @Test
    fun `주민등록번호 - 데이터 삭제 (Data Reduction)`() {
        // given
        val regNo = "891023-1234567"
        // when
        val v = DeIdentification.regNo(regNo)
        // then
        logger.debug { "regNo: $regNo -> $v" }
        assertEquals(v, "80년대생 남자")
    }

    @Test
    fun `나이 - 가명 처리 (Pseudonymization)`() {
        // given
        val age = 37
        // when
        val v = DeIdentification.age(age)
        // then
        logger.debug { "age: $age -> $v" }
        assertEquals(v, "30대")
    }

    @Test
    fun `주소 - 데이터 범주화(Data Suppression)`() {
        // given
        val addr = "제주특별자치도 제주시 광양4길 20 (이도일동)"
        // when
        val v = DeIdentification.address(addr)
        // then
        logger.debug { "addr: $addr -> $v" }
        assertEquals(v, "제주 거주")
    }
}