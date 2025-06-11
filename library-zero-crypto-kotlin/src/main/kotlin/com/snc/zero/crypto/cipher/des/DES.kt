package com.snc.zero.crypto.cipher.des

import com.snc.zero.crypto.encoder.Encoder
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * DES
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object DES {

    fun genKey(): SecretKey {
        // key값 구함
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("DES")
        keyGenerator.init(SecureRandom())
        return keyGenerator.generateKey()
    }

    /**
     * 원문과 암호문 길이가 동일한 알고리즘
     * byte 길이가 동일할 뿐 byte를 문자로 표기시 동일할 순 없다.
     */
    @Throws(Exception::class)
    fun encrypt(input: ByteArray, key: SecretKey): ByteArray {
        // key값으로 암호화함(CFB or OFB)
        val cipher: Cipher = Cipher.getInstance("DES/CFB/NoPadding")
        //Cipher cipher = Cipher.getInstance("DES/OFB/NoPadding")

        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(input)
    }

    @Throws(Exception::class)
    fun decrypt(enc: ByteArray, key: SecretKey): ByteArray {
        // key값으로 암호화함(CFB or OFB)
        val cipher: Cipher = Cipher.getInstance("DES/CFB/NoPadding")
        //Cipher cipher = Cipher.getInstance("DES/OFB/NoPadding")

        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(enc)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val secretKey = genKey()
        val input = "해쉬 할 내용을 입력해 주세요. ex)plainText hoho"
        val cipherTextBytes = encrypt(input.toByteArray(), secretKey)
        val dec = decrypt(cipherTextBytes, secretKey)

        println("encSSN: plainTextString[$input]")
        println("encSSN: plainTextBytes.length[${input.toByteArray(charset("UTF-8")).size}]")
        println("encSSN: cipherTextBytes.length[${cipherTextBytes.size}]")
        println("encSSN: cipherTextBytes.base64[${Encoder.with(Encoder.Algo.BASE64).encode(cipherTextBytes)}]")
        println("encSSN: cipherTextBytes.base62[${Encoder.with(Encoder.Algo.BASE62).encode(cipherTextBytes)}]")
        println("encSSN: decodeTextString[$dec]")
    }
}
