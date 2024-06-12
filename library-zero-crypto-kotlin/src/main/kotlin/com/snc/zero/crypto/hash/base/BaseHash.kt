package com.snc.zero.crypto.hash.base

import java.nio.charset.Charset

abstract class BaseHash: Digest {

    protected fun salt(
        digest: org.bouncycastle.crypto.digests.KeccakDigest,
        salt: String = "",
        charSet: Charset = Charsets.UTF_8
    ) {
        if (salt.isNotEmpty()) {
            val sb = salt.toByteArray(charSet)
            digest.update(sb, 0, sb.size)
        }
    }

    protected fun iteration(
        digest: org.bouncycastle.crypto.digests.KeccakDigest,
        ib: ByteArray,
        iterationCount: Int = 0
    ) {
        for (i in 1..iterationCount) {
            digest.update(ib, 0, ib.size)
        }
    }

    fun sha128(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 128, salt, iterationCount, charSet)
    }

    fun sha224(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 224, salt, iterationCount, charSet)
    }

    fun sha256(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 256, salt, iterationCount, charSet)
    }

    fun sha384(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 384, salt, iterationCount, charSet)
    }

    fun sha512(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 512, salt, iterationCount, charSet)
    }
}

fun interface Digest {
    fun digest(
        msg: String,
        bitLength: Int,
        salt: String,
        iterationCount: Int,
        charSet: Charset
    ): ByteArray
}