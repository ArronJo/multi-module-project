package com.snc.test.crypto.decoder.base62

import com.snc.zero.crypto.encoder.base62.Base62
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class Base62Test : BaseJUnit5Test() {

    @Test
    fun `Decode To Base62`() {
        // given
        val data = "hbWbdDh6qZs9HKMROfLt7E3ZruZ08wTiKbvkwhrDXN96CoC3qPFfMep6fezRcZf"
        // when
        val v = Base62.decode(data.toByteArray())
        // then
        logger.debug { "Base62 decoded: $v" }
        assertEquals(String(v), "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds")
    }
}
