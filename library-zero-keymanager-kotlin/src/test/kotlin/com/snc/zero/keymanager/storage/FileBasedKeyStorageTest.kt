package com.snc.zero.keymanager.storage

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Base64
import javax.crypto.spec.SecretKeySpec

@Suppress("NonAsciiCharacters")
class FileBasedKeyStorageTest : BaseJUnit5Test() {

    @Nested
    inner class DefaultTest {

        @Test
        fun `기본 테스트`() {
            // 키 매니저 생성
            val keyManager = RotatingKeyManager.createFileBasedManager(
                keyStorePath = "./build/keys/file_keys/encrypted_keys.dat",
                masterPassword = "your-master-password-change-this"
            )

            // 현재 키 가져오기 (없으면 자동 생성)
            val (currentVersion, currentKey) = keyManager.getCurrentKey()
            val currentKeyAsString = secretKeyToString(currentKey)
            println("현재 키 버전: $currentVersion,  currentKey:$currentKey")

            println("현재 키 문자열: $currentKeyAsString")
            val rebuiltKey = stringToSecretKey(currentKeyAsString)
            println("복원된 키와 원본 키가 동일한가? ${currentKey == rebuiltKey}")

            // 특정 날짜 키 생성
            val (testVersion, testKey) = keyManager.getOrCreateKey("20241201")
            println("테스트 키 버전: $testVersion,  testKey:$testKey")

            // 모든 키 버전 조회
            println("사용 가능한 키 버전들: ${keyManager.getAllKeyVersions()}")

            // 키 존재 확인
            println("20241201 키 존재: ${keyManager.keyExists("20241201")}")

            // 오래된 키 정리 (30일 이상된 키 삭제)
            keyManager.cleanupOldKeys(30)

            keyManager.clearCache()
        }
    }

    fun secretKeyToString(secretKey: SecretKeySpec): String {
        // getEncoded() 메서드로 키의 바이트 배열 얻기
        val encodedKey: ByteArray? = secretKey.encoded

        // Base64 인코딩을 사용하여 문자열로 변환 (keyAsString)
        return Base64.getEncoder().encodeToString(encodedKey)
    }

    fun stringToSecretKey(keyAsString: String): SecretKeySpec {
        // 1. 문자열을 Base64 디코딩하여 바이트 배열로 복원
        val decodedKey: ByteArray = Base64.getDecoder().decode(keyAsString)
        // 2. 바이트 배열과 알고리즘을 사용해 SecretKeySpec 재구성 (reBuildKey)
        return SecretKeySpec(decodedKey, "AES")
    }
}
