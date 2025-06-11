package com.snc.zero.crypto.cipher.aes

import com.snc.zero.crypto.hash.Hash
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES (Advanced Encryption Standard)
 *
 * -Block
 *  : https://colevelup.tistory.com/51
 *  : https://blog.humminglab.io/posts/tls-cryptography-4-block-cipher-mode/
 *
 * -빌드 패턴
 * : https://refactoring.guru/ko/design-patterns/builder
 *
 * -지원 알고리즘
 * : https://developer.android.com/reference/javax/crypto/Cipher?sjid=13846553385149160591-AP
 *
 * -암호화 Block
 *   -AES-CBC 모드
 *     -보안성: CBC 모드는 각 블록의 암호화 전에 이전 블록의 암호화 결과(초기화 벡터로 시작)와 XOR 연산을 수행합니다. 이는 패턴의 숨김과 오류의 전파 효과를 갖습니다.
 *     -용도: 데이터 전체 무결성이 중요할 때 적합합니다. 파일 저장, 대량 데이터 전송에 자주 사용됩니다.
 *     -단점: 각 블록은 이전 블록의 암호화가 완료되어야 처리할 수 있기 때문에 병렬 처리가 불가능합니다.
 *   -AES-CFB 모드
 *     -보안성: CFB 모드는 스트림 암호의 형태를 취하며, 일부 데이터만 암호화하는 경우(예: 네트워크 스트림)에 유용합니다.
 *     -용도: 데이터가 실시간으로 전송되거나 암호화할 데이터의 양이 불규칙적일 때 적합합니다. 인터넷 트래픽 암호화, 실시간 음성/비디오 데이터 전송에 좋습니다.
 *     -단점: 블록 크기에 맞추어 입력을 조절해야 하며, 에러 전파가 일부 발생할 수 있습니다.
 *
 *   -ECB 취약점
 *     : https://lactea.kr/entry/ECB-%EC%84%A4%EB%AA%85-%EB%B0%8F-%EC%B7%A8%EC%95%BD%EC%A0%90#google_vignette
 *   -CBC 취약점
 *     : https://blog.encrypted.gg/793
 *     : https://learn.microsoft.com/ko-kr/dotnet/standard/security/vulnerabilities-cbc-mode
 *
 *   -안전하지 않은 암호화 문제 해결하기
 *     : https://support.google.com/faqs/answer/9450925?hl=ko
 *
 *   -권장 블럭
 *     : https://velog.io/@kafkaaaa/Block-Cipher-Mode-%EB%B8%94%EB%9F%AD-%EC%95%94%ED%98%B8%ED%99%94-%EB%AA%A8%EB%93%9C-ECB-CBC-CFB-OFB-CTR-GCM
 *     : CBC, CTR, GCM
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object AES {

    /*
        [Snyk] 암호화 알고리즘 취약점 결과

        [Medium] Inadequate Padding for AES encryption
        Info: AES with CBC mode and PKCS5Padding (AES/CBC/PKCS5Padding)
              used in javax.crypto.Cipher.getInstance is vulnerable to padding oracle attacks.
              Consider using Galois/Counter Mode (algorithm AES/GCM/NoPadding).
     */

    /*
    enum class Transform(val s: String) {
        // 우선수위가 높은 수록 안전한 모드이다.
        AES_GCM_NoPadding("AES/GCM/NoPadding"),         // [강추] 24년 기준 아직 취약점 없음
        AES_CTR_NoPadding("AES/CTR/NoPadding"),
        AES_CBC_PKCS5Padding("AES/CBC/PKCS5Padding"),
        AES_CFB_PKCS5Padding("AES/CFB/PKCS5Padding"),   // [비추] CTR 모드를 사용하는 편이 나음
        AES_ECB_PKCS5Padding("AES/ECB/PKCS5Padding")    // [비추] 취약점 존재
        ;
        fun getValue(): String { return s }
    }
     */

    fun encrypt(data: ByteArray, key: String, iv: String, transform: String = "AES/GCM/NoPadding"): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), "AES")
        val cipher = Cipher.getInstance(transform)
        val algoParameterSpec = parameterSpec(iv, transform)

        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            algoParameterSpec
        )
        return cipher.doFinal(data)
    }

    fun decrypt(enc: ByteArray, key: String, iv: String, transform: String = "AES/GCM/NoPadding"): ByteArray {
        val secretKey = SecretKeySpec(genKey(key), "AES")
        val cipher = Cipher.getInstance(transform)
        val algoParameterSpec = parameterSpec(iv, transform)

        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            algoParameterSpec
        )
        return cipher.doFinal(enc)
    }

    private fun parameterSpec(iv: String, transform: String): AlgorithmParameterSpec {
        var algoParameterSpec: AlgorithmParameterSpec = IvParameterSpec(genIv(iv))
        if (transform.split("/")[1] == "GCM") {
            // TLen value.  Must be one of {128, 120, 112, 104, 96}
            algoParameterSpec = GCMParameterSpec(128, iv.toByteArray())
        }
        return algoParameterSpec
    }

    private fun genKey(key: String): ByteArray {
        return Hash.with(Hash.Algo.SHA256).digest(key)
    }

    private fun genIv(iv: String): ByteArray {
        return Hash.with(Hash.Algo.SHAKE128).digest(iv)
    }
}
