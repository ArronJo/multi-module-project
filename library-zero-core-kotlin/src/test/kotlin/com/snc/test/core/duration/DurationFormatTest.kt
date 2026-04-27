package com.snc.test.core.duration

import com.snc.zero.core.duration.DurationFormat
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class DurationFormatTest : BaseJUnit5Test() {

    @Test
    fun `날짜 format 테스트 1`() {
        val elapsed = DurationFormat.formatDuration(1136329)
        logger.debug { "elapsed = $elapsed" }
        assertEquals("18m 56s 329ms", elapsed)
    }
}
