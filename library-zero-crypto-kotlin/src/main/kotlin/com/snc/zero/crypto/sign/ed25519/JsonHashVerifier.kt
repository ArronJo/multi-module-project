package com.snc.zero.crypto.sign.ed25519

import com.fasterxml.jackson.core.type.TypeReference
import com.snc.zero.crypto.canonical.CanonicalJsonUtil
import com.snc.zero.crypto.sign.ed25519.Ed25519Signer.hmacSha256

object JsonHashVerifier {

    /**
     * DTO 없이 JSON 기반 HMAC 검증
     */
    fun verify(jsonBytes: ByteArray): Boolean {
        // 1️⃣ JSON → Map 변환
        val map: MutableMap<String, Any> =
            CanonicalJsonUtil.mapper.readValue(
                jsonBytes,
                object : TypeReference<MutableMap<String, Any>>() {}
            )

        // 2️⃣ 기존 hash 추출
        val originalHash = map["hash"] as? String
            ?: return false

        // 3️⃣ hash 데이터 빈 문자열
        map["hash"] = ""

        // 4️⃣ Canonical JSON 재생성
        val canonical =
            CanonicalJsonUtil.toCanonical(map)

        // 5️⃣ HMAC 재계산
        val expected =
            hmacSha256(canonical)

        // 6️⃣ 비교
        return expected == originalHash
    }
}
