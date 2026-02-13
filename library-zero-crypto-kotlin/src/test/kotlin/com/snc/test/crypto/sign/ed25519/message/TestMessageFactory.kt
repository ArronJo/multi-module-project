package com.snc.test.crypto.sign.ed25519.message

import com.snc.zero.crypto.canonical.CanonicalJsonUtil
import com.snc.zero.crypto.sign.ed25519.Ed25519Signer.hmacSha256
import java.time.LocalDateTime

object TestMessageFactory {

    fun create(): ByteArray {
        val message = PaymentMessage(
            dateTime = LocalDateTime.now(),
            location = "서울 강남구",
            user = "홍길동",
            menu = "아메리카노 Tall",
            purpose = "업무 미팅",
            amount = 4500
        )

        // ✅ hash 제외 Canonical
        val canonical = CanonicalJsonUtil
            .toCanonical(message.withoutHash())

        // ✅ HMAC 생성
        val hmac = hmacSha256(canonical)

        // ✅ hash 세팅
        message.hash = hmac

        // ✅ 최종 서명용 Canonical
        val signedJson =
            CanonicalJsonUtil.toCanonical(message)
        println("signedMessage: $signedJson")

        return signedJson.toByteArray(Charsets.UTF_8)
    }
}
