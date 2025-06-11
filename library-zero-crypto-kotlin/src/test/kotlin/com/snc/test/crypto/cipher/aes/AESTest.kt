package com.snc.test.crypto.cipher.aes

import com.snc.zero.crypto.cipher.aes.AES.decrypt
import com.snc.zero.crypto.cipher.aes.AES.encrypt
import com.snc.zero.crypto.encoder.base62.Base62
import com.snc.zero.crypto.encoder.base64.Base64
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class AESTest : BaseJUnit5Test() {

    @Test
    fun `AES 테스트`() {
        val key = "이것은 키 입니다."
        val iv = "initialization vector. IV must be 16 bytes length. (128 bit),"
        val message = "빨리 오픈하고 양양 가즈아~~~!!!"
        val enc: ByteArray = encrypt(message.toByteArray(), key, iv)
        val dec = String(decrypt(enc, key, iv))
        println("plain[${message.length}] = $message")
        println("enc[${enc.size}] = $enc")
        println("Base64 = ${Base64.encode(enc)}")
        println("Base62 = ${Base62.encode(enc)}")
        println("dec = $dec")
        assertEquals("빨리 오픈하고 양양 가즈아~~~!!!", message)
    }
}
