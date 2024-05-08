package com.snc.zero.crypto.extensions.hash

import com.snc.zero.crypto.hash.Hash
import java.nio.charset.Charset

fun String.toHmacSHA224(key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.HmacSHA224).key(key).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toHmacSHA256(key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.HmacSHA256).key(key).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toHmacSHA384(key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.HmacSHA384).key(key).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toHmacSHA512(key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.HmacSHA512).key(key).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toSha224(salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.SHA224).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toSha256(salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.SHA256).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toSha384(salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.SHA384).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toSha512(salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.SHA512).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toShake128(salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.SHAKE128).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}

fun String.toShake256(salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
    return Hash.with(Hash.Algo.SHAKE256).salt(salt).iterationCount(iterationCount).charSet(charSet).digest(this)
}