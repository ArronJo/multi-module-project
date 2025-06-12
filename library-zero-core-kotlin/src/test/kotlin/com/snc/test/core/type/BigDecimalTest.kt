package com.snc.test.core.type

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.math.BigDecimal

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class BigDecimalTest : BaseJUnit5Test() {

    /**
     Java에서 BigDecimal 클래스를 사용할 때 new BigDecimal() 생성자와 BigDecimal.valueOf() 메소드를 통해
     값이 설정될 때 결과가 다를 수 있는 주요 원인은 두 방법이 데이터를 처리하는 방식의 차이 때문입니다.
     특히 정밀도와 타입 변환 과정에서 차이가 발생할 수 있습니다.

     `new BigDecimal() 생성자`
     `new BigDecimal()` 생성자는 문자열 또는 다른 숫자 형을 인수로 받아 `BigDecimal` 객체를 생성합니다.
     특히 문자열을 인수로 사용할 때는 해당 문자열이 나타내는 정확한 값을 가진 `BigDecimal` 객체를 생성합니다.

     예를 들어:

     ```java
     BigDecimal bd1 = new BigDecimal("100000000");
     ```

     이 코드는 문자열 "100000000"을 정확히 나타내는 `BigDecimal` 객체를 생성합니다.

     `BigDecimal.valueOf() 메소드`
     `BigDecimal.valueOf()` 메소드는 주로 `long` 또는 `double` 타입의 인수를 받아 `BigDecimal` 객체를 생성합니다.
     이 메소드는 특히 `double` 타입을 인수로 받았을 때, `double`의 부정확성 때문에 정밀한 값의 오류가 발생할 수 있습니다.

     예를 들어:

     ```java
     BigDecimal bd2 = BigDecimal.valueOf(100000000.0);
     ```

     이 코드는 double 값 `100000000.0`을 `BigDecimal`로 변환합니다.
     double 값이 이미 부동 소수점 방식으로 표현되기 때문에, 이론적으로 소수점 아래 정밀도에 작은 오류가 생길 수 있습니다.
     하지만 정수로 표현된 `double` 값은 일반적으로 정확하게 변환됩니다.

     따라서, 정확한 값을 유지하기 위해 `BigDecimal`를 사용할 때는 가능하면 `new BigDecimal(String)` 생성자를 사용하는 것이 좋습니다.
     이 방법이 문자열로 표현된 정확한 값을 `BigDecimal`로 변환할 수 있는 가장 안전한 방법입니다.

     예시
     100_000_000을 설정하는 경우, 두 메소드 모두 정확한 값을 반환할 것입니다. 예를 들어:

     ```java
     BigDecimal bd1 = new BigDecimal("100000000");
     BigDecimal bd2 = BigDecimal.valueOf(100000000L);
     ```

     이 두 방법 모두 정확히 `100000000` 값을 `BigDecimal` 객체로 생성합니다.
     `BigDecimal.valueOf(long)`는 long 값에서도 정확한 변환을 제공합니다.

     `문제는 BigDecimal.valueOf(double)`를 사용할 때 발생할 수 있으며, 주로 소수점 이하 값이 포함된 경우입니다.

     이에 따라, 특정 상황에서 결과가 다르게 나온다면 사용하는 데이터 타입과 정밀도, 그리고 데이터 표현 방식을 확인해야 합니다.
     */
    @Test
    fun `참조 자료형 BigDecimal 테스트`() {
        // given
        val i = 100_000_000 // 100000000
        val l0: Long = i.toLong()
        val u0: Double = i.toDouble()
        // when
        val bv1 = BigDecimal.valueOf(u0) // "1.0E+8"
        val bv2 = BigDecimal.valueOf(l0)
        val bn1 = BigDecimal(u0) // "100000000"
        Thread.sleep(100)
        // then
        logger.debug { "BigDecimal.valueOf(Double) 결과 : toString: $bv1, longValueExact: ${bv1.longValueExact()}, toDouble: ${bv1.toDouble()}" }
        logger.debug { "BigDecimal.valueOf(Long) 결과 : toString: $bv2, longValueExact: ${bv2.longValueExact()}, toDouble: ${bv2.toDouble()}" }
        logger.debug { "new BigDecimal(Double) 결과 : toString: $bn1, longValueExact: ${bn1.longValueExact()}, toDouble: ${bn1.toDouble()}" }
    }
}
