package com.snc.test.extension.collection

import com.snc.zero.extension.collection.asListOfType
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class CollectionExtTest : BaseJUnit5Test() {

    @Test
    fun `데이터 타입 체크 1`() {
        // given
        val v = listOf("1", "2")
        // when
        val type1 = v.asListOfType<String>()
        val type2 = v.asListOfType<Int>()
        // then
        logger.debug { "Type1 : $type1" }
        logger.debug { "Type2 : $type2" }
    }
}
