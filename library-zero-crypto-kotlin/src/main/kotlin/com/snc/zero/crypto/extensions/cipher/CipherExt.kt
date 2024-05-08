package com.snc.zero.crypto.extensions.cipher

import com.snc.zero.crypto.cipher.Cipher

fun String.encrypt(algo:Cipher.Algo = Cipher.Algo.AES, key: String, iv: String, transform: String = "AES/CBC/PKCS5Padding"): String {
    return Cipher.with(algo)
        .key(key, iv)
        .transform(transform)
        .encrypt(this.toByteArray())
}

fun String.decrypt(algo:Cipher.Algo = Cipher.Algo.AES, key: String, iv: String, transform: String = "AES/CBC/PKCS5Padding"): String {
    return String(Cipher.with(algo)
        .key(key, iv)
        .transform(transform)
        .decrypt(this))
}
