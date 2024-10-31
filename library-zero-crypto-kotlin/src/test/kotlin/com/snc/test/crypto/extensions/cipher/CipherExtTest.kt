package com.snc.test.crypto.extensions.cipher

import com.snc.zero.crypto.extensions.cipher.decrypt
import com.snc.zero.crypto.extensions.cipher.encrypt
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

class CipherExtTest : BaseJUnit5Test() {

    private val key = "as"
    private val iv = "va"
    private var data1 = ""
    private var data2 = ""

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        data1 = "qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty|qwerty"
        data2 = "DSgrqTvXG5vEWah1iLfxhSh_mnAIZ8Z9_S-lkp1Ut-IzRlS55xk78viRoPA9v4tdCAKPGMtcSRmQ20DmLfIsRFClqvewaN3-BC8UD3tpDNRkDZm0e8-hmmDDugIKq8rwlDYIOskwW2DKfD1Kt2tJUQ=="
    }

    @Test
    fun `AES Encrypt`() {
        // given
        // when
        val v = data1.encrypt(key = key, iv = iv)
        // then
        logger.debug { "AES encrypt: $v" }
        assertEquals(data2, v)
    }

    @Test
    fun `AES Decrypt`() {
        // given
        // when
        val v = data2.decrypt(key = key, iv = iv)
        // then
        logger.debug { "AES decrypt: $v" }
        assertEquals(data1, v)
    }
}
