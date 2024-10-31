package com.snc.zero.logger

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

val logger = TLogging.logger { }

class TLoggerTest : BaseJUnit5Test() {

    @Test
    fun `kotlin logging level test`() {
        logger.debug { "debug level" }
        logger.info { "info level" }
        logger.warn { "warning level" }
        logger.error { "error level" }
    }
}
