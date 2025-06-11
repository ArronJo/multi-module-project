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
        data2 = "279/jXnpcRoMTEaBSfd6+dYpocGcnDzT14exezEf/ubt3rzDBQoijvMCXsPNsLFCPXa70CfxwJc7GYnZtUQz/CNa3Y0mreLbI6WwhixWgQJZc1LR1jgkfZLvxe3fLCSrE6+1leav5gOXdpSYI5I8/gE="
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
