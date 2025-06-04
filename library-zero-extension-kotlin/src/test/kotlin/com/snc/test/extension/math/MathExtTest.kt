package com.snc.test.extension.math

import com.snc.zero.extension.math.roundToDecimalPlaces
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class MathExtTest : BaseJUnit5Test() {

    @Test
    fun `Math round 테스트 1-1`() {
        // given
        val number = 3.55555
        // when
        val vm1 = number.roundToDecimalPlaces(-1)
        val v0 = number.roundToDecimalPlaces(0)
        val v1 = number.roundToDecimalPlaces(1)
        val v2 = number.roundToDecimalPlaces(2)
        val v3 = number.roundToDecimalPlaces(3)
        val v4 = number.roundToDecimalPlaces(4)
        val v5 = number.roundToDecimalPlaces(5)

        logger.debug { "Rounded to -1 decimal places: $vm1" } // 0.0
        logger.debug { "Rounded to 0 decimal places: $v0" } // 4.0
        logger.debug { "Rounded to 1 decimal places: $v1" } // 3.6
        logger.debug { "Rounded to 2 decimal places: $v2" } // 3.56
        logger.debug { "Rounded to 3 decimal places: $v3" } // 3.556
        logger.debug { "Rounded to 4 decimal places: $v4" } // 3.5556
        logger.debug { "Rounded to 5 decimal places: $v5" } // 3.55555

        // then
        assertEquals(0.0, vm1)
        assertEquals(4.0, v0)
        assertEquals(3.6, v1)
        assertEquals(3.56, v2)
        assertEquals(3.556, v3)
        assertEquals(3.5556, v4)
        assertEquals(3.55555, v5)
    }
}
