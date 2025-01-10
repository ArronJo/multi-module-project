package com.snc.zero.crypto.extensions.cipher

import com.snc.zero.crypto.cipher.Cipher
import com.snc.zero.crypto.cipher.rsa.RSA

fun String.encrypt(
    algo: Cipher.Algo = Cipher.Algo.AES,
    key: String,
    iv: String,
    transform: String = "AES/CBC/PKCS5Padding"
): String {
    return Cipher.with(algo)
        .key(key, iv)
        .transform(transform)
        .encrypt(
            this.toByteArray()
        )
}

fun String.decrypt(
    algo: Cipher.Algo = Cipher.Algo.AES,
    key: String,
    iv: String,
    transform: String = "AES/CBC/PKCS5Padding"
): String {
    return String(
        Cipher.with(algo)
            .key(key, iv)
            .transform(transform)
            .decrypt(this)
    )
}

/**
 * 문자열 확장 함수: RSA 암호화
 */
fun String.encryptRSA(rsaUtil: RSA): String = rsaUtil.encrypt(this)

/**
 * 문자열 확장 함수: RSA 복호화
 */
fun String.decryptRSA(rsaUtil: RSA): String = rsaUtil.decrypt(this)
