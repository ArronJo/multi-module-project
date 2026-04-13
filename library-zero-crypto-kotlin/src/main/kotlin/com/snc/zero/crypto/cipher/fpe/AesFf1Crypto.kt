package com.snc.zero.crypto.cipher.fpe

import org.bouncycastle.crypto.AlphabetMapper
import org.bouncycastle.crypto.fpe.FPEFF1Engine
import org.bouncycastle.crypto.params.FPEParameters
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.util.BasicAlphabetMapper
import java.nio.charset.StandardCharsets

/**
 * Bouncy Castle의 FPEFF1Engine을 이용한 Format-Preserving Encryption(FPE) 구현체.
 * NIST SP 800-38G 표준을 따릅니다.
 * : https://en.wikipedia.org/wiki/Format-preserving_encryption
 * @property secretKeyString AES 키 (16, 24, 32 바이트)
 * @property tweak 암호화 보안 강화를 위한 트윅 (최소 16 바이트 이상의 트윅을 사용)
 * 암호학에서 **Tweak(트위크)**은 동일한 키와 동일한 평문을 사용하더라도 **서로 다른 암호문이 나오도록 유도하는 '추가적인 입력값'**을 의미합니다.
 */
class AesFf1Crypto(
    secretKeyString: String,
    private val tweak: String,
    alphabet: String = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
) {
    private val engine = FPEFF1Engine()
    private val keyParam: KeyParameter
    private val alphabetMapper: AlphabetMapper

    init {
        // AES 키 설정
        val keyBytes = secretKeyString.toByteArray(StandardCharsets.UTF_8)
        require(keyBytes.size in listOf(16, 24, 32)) { "AES key must be 128, 192, or 256 bits (16, 24, or 32 bytes)" }
        keyParam = KeyParameter(keyBytes)

        // 사용자 정의 또는 기본 알파벳(Radix 62) 설정
        alphabetMapper = BasicAlphabetMapper(alphabet)
    }

    /**
     * 문자열 암호화: 원본의 길이와 문자의 종류를 유지합니다.
     */
    fun encrypt(plainText: String): String {
        val input = alphabetMapper.convertToIndexes(plainText.toCharArray())
        val fpeParams = FPEParameters(keyParam, alphabetMapper.radix, tweak.toByteArray(StandardCharsets.UTF_8))
        engine.init(true, fpeParams)

        val output = ByteArray(input.size)
        engine.processBlock(input, 0, input.size, output, 0)

        return String(alphabetMapper.convertToChars(output))
    }

    /**
     * 문자열 복호화: 암호화된 값을 원래의 값으로 되돌립니다.
     */
    fun decrypt(cipherText: String): String {
        val input = alphabetMapper.convertToIndexes(cipherText.toCharArray())
        val fpeParams = FPEParameters(keyParam, alphabetMapper.radix, tweak.toByteArray(StandardCharsets.UTF_8))
        engine.init(false, fpeParams)

        val output = ByteArray(input.size)
        engine.processBlock(input, 0, input.size, output, 0)

        return String(alphabetMapper.convertToChars(output))
    }

    companion object {
        private val SECURE_RANDOM = java.security.SecureRandom()
        private const val DEFAULT_TWEAK_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

        /**
         * 보안에 적합한 랜덤 Tweak 문자열을 생성합니다.
         * @param length 생성할 Tweak의 길이 (최소 기본 16자)
         * @return 랜덤하게 생성된 Tweak 문자열
         */
        fun generateTweak(length: Int = 16): String {
            val sb = StringBuilder(length)
            repeat(length) {
                val index = SECURE_RANDOM.nextInt(DEFAULT_TWEAK_ALPHABET.length)
                sb.append(DEFAULT_TWEAK_ALPHABET[index])
            }
            return sb.toString()
        }
    }
}
