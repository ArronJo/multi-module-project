package com.snc.zero.crypto.hash.base

import java.nio.charset.Charset

open class BaseHash {

    protected fun salt(digest: org.bouncycastle.crypto.digests.KeccakDigest, salt: String = "", charSet: Charset = Charsets.UTF_8) {
        if (salt.isNotEmpty()) {
            val sb = salt.toByteArray(charSet)
            digest.update(sb, 0, sb.size)
        }
    }

    protected fun iteration(digest: org.bouncycastle.crypto.digests.KeccakDigest, ib: ByteArray, iterationCount: Int = 0) {
        for (i in 1..iterationCount) {
            digest.update(ib, 0, ib.size)
        }
    }
}