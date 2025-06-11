package com.snc.test.crypto.encoder.base62

import com.snc.zero.crypto.encoder.base62.Base62
import com.snc.zero.crypto.encoder.base62.Base62.CharacterSets
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class Base62Test : BaseJUnit5Test() {

    @Test
    fun `Encode To Base62 - 1`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Base62.encode(data.toByteArray())
        // then
        logger.debug { "Base62 encoded: $v" }
        assertEquals("hbWbdDh6qZs9HKMROfLt7E3ZruZ08wTiKbvkwhrDXN96CoC3qPFfMep6fezRcZf", String(v))
    }

    @Test
    fun `Encode To Base62 - 2`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Base62.encode(data.toByteArray(), CharacterSets.INVERTED)
        // then
        logger.debug { "Base62 encoded: $v" }
        assertEquals("HBwBDdH6QzS9hkmroFlT7e3zRUz08WtIkBVKWHRdxn96cOc3QpfFmEP6FEZrCzF", String(v))
    }
}
