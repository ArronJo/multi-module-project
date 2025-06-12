package com.snc.test.crypto.encoder.base62

import com.snc.zero.crypto.encoder.base62.Base62Int
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

class Base62IntTest : BaseJUnit5Test() {

    @Test
    fun `Encode To Base62 - 1`() {
        println("62^0 : 0                   : " + Base62Int.encode(0))
        println("62^1 : 62                  : " + Base62Int.encode(62))
        println("62^2 : 3,844               : " + Base62Int.encode(3844))
        println("62^3 : 238,328             : " + Base62Int.encode(238328))
        println("62^4 : 14,776,336          : " + Base62Int.encode(14776336))
        println("62^5 : 916,132,832         : " + Base62Int.encode(916132832))
        println("62^6 : 56,800,235,584      : " + Base62Int.encode(56800235584))
        println("62^7 : 3,521,614,606,208   : " + Base62Int.encode(3521614606208))
        println(" ~max: 218,340,105,584,895 : " + Base62Int.encode(218340105584895))
        println("62^8 : 218,340,105,584,896 : " + Base62Int.encode(218340105584896))
        println("MAX  : 9,223,372,036,854,775,807 : " + Base62Int.encode(Long.MAX_VALUE))
        println()
        println()

        println("DTC회원(12자리)   : " + Base62Int.encode(102312345678L))
        println("DTC회원(12자리): " + Base62Int.decode(Base62Int.encode(102312345678L)))

        println("주민등록번호(13자리): " + Base62Int.encode(7911231234567L))
        println("주민등록번호(13자리): " + Base62Int.decode(Base62Int.encode(7911231234567L)))
        println()
        println()

        println("날짜: " + Base62Int.encode(Base62Int.today("yyyyMMddHHmmssSSS").toLong()))
        println("날짜: " + Base62Int.encode(Base62Int.today().toLong()))
        println()
        println()
    }
}
