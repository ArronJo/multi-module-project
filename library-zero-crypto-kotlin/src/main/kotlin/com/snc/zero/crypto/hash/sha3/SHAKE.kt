package com.snc.zero.crypto.hash.sha3

import com.snc.zero.crypto.hash.base.BaseHash
import org.bouncycastle.crypto.digests.SHAKEDigest
import java.nio.charset.Charset

object SHAKE: BaseHash() {

    override fun digest(msg: String, bitLength: Int, salt: String, iterationCount: Int, charSet: Charset): ByteArray {
        checkBitLength(bitLength)
        val digest = SHAKEDigest(bitLength)
        val ib = msg.toByteArray(charSet)
        val ob = ByteArray(bitLength / 8)
        salt(digest, salt, charSet)
        digest.update(ib, 0, ib.size)
        iteration(digest, ib, iterationCount)
        digest.doOutput(ob, 0, ob.size)
        return ob
    }

    private fun checkBitLength(bitLength: Int): Int {
        when (bitLength) {
            128, 256 -> return bitLength
            else -> throw IllegalArgumentException("'bitLength' $bitLength not supported")
        }
    }
}