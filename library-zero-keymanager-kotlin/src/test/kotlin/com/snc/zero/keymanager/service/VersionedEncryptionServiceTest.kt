package com.snc.zero.keymanager.service

import com.snc.zero.keymanager.storage.RotatingKeyManager
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class VersionedEncryptionServiceTest : BaseJUnit5Test() {

    @Nested
    inner class DefaultDemo {

        @Test
        fun `기본 사용법 데모`() {
            println("=== 키 버전 관리 암호화 시스템 데모 ===\n")

            // 1. 키 매니저 생성
            println("1. 키 매니저 초기화")
            val keyManager = RotatingKeyManager.createFileBasedManager(
                keyStorePath = "./build/keys/demo_keys/encrypted_keys.dat",
                masterPassword = "demo-master-password-2024"
            )

            // 2. 암호화 서비스 생성
            println("2. 암호화 서비스 초기화")
            val encryptionService = VersionedEncryptionService(keyManager)

            // 3. 테스트 데이터 암호화
            println("\n3. 데이터 암호화 테스트")
            val testData = listOf(
                "사용자 개인정보 데이터 1",
                "중요한 비즈니스 정보",
                "암호화가 필요한 민감 데이터"
            )

            val encryptedResults = mutableListOf<EncryptedData>()
            testData.forEachIndexed { index, data ->
                val encrypted = encryptionService.encrypt(data)
                encryptedResults.add(encrypted)
                println("암호화 완료 [$index]: 키버전=${encrypted.keyVersion}, 길이=${encrypted.data.length}")
            }

            // 4. 복호화 테스트
            println("\n4. 데이터 복호화 테스트")
            val decryptedData = encryptionService.decryptBatch(encryptedResults)
            decryptedData.forEachIndexed { index, data ->
                println("복호화 완료 [$index]: $data")
            }

            // 5. 다른 날짜 키로 암호화
            println("\n5. 특정 키 버전으로 암호화 테스트")
            val oldVersionKey = "20241201"
            keyManager.getOrCreateKey(oldVersionKey) // 해당 키가 없으면 생성

            val oldEncrypted = encryptionService.encryptWithVersion("과거 키로 암호화된 데이터", oldVersionKey)
            println("과거 키로 암호화: 키버전=${oldEncrypted.keyVersion}")

            val oldDecrypted = encryptionService.decrypt(oldEncrypted)
            println("과거 키로 복호화: $oldDecrypted")

            // 6. JSON 직렬화 테스트
            println("\n6. JSON 직렬화/역직렬화 테스트")
            val jsonString = encryptionService.encryptedDataToJson(encryptedResults[0])
            println("JSON 직렬화: ${jsonString.take(100)}...")

            val fromJson = encryptionService.encryptedDataFromJson(jsonString)
            val jsonDecrypted = encryptionService.decrypt(fromJson)
            println("JSON 역직렬화 후 복호화: $jsonDecrypted")

            // 7. 데이터 재암호화 테스트 (키 마이그레이션)
            println("\n7. 데이터 재암호화 테스트")
            val currentVersion = keyManager.getCurrentKey().first
            println("currentVersion: $currentVersion")
            val newVersion = "20241225" // 새로운 키 버전
            keyManager.getOrCreateKey(newVersion) // 새 키 생성

            println("원본 암호화 데이터: 키버전=${oldEncrypted.keyVersion}")
            val reEncrypted = encryptionService.reEncryptWithNewKey(oldEncrypted, newVersion)
            println("재암호화된 데이터: 키버전=${reEncrypted.keyVersion}")

            val reDecrypted = encryptionService.decrypt(reEncrypted)
            println("재암호화 후 복호화: $reDecrypted")

            // 8. 배치 재암호화 테스트
            println("\n8. 배치 재암호화 테스트")
            println("기존 데이터 키 버전들: ${encryptedResults.map { it.keyVersion }}")

            val batchReEncrypted = encryptionService.reEncryptBatch(encryptedResults, newVersion)
            println("재암호화 후 키 버전들: ${batchReEncrypted.map { it.keyVersion }}")

            val batchReDecrypted = encryptionService.decryptBatch(batchReEncrypted)
            println("배치 재암호화 후 복호화 결과:")
            batchReDecrypted.forEachIndexed { index, data ->
                println("  [$index]: $data")
            }

            // 9. 키 정보 출력
            println("\n9. 현재 키 정보")
            val allVersions = keyManager.getAllKeyVersions()
            println("사용 가능한 키 버전들: $allVersions")

            println("\n=== 데모 완료 ===")
        }

        @Test
        fun `간단한 헬퍼 클래스 데모`() {
            println("\n=== SimpleEncryptionHelper 데모 ===\n")

            // 1. 간단한 암호화 헬퍼 생성
            println("1. SimpleEncryptionHelper 초기화")
            val helper = SimpleEncryptionHelper.create(
                keyStorePath = "./build/keys/simple_demo_keys/encrypted_keys.dat",
                masterPassword = "simple-demo-password"
            )

            // 2. 간단한 텍스트 암호화
            println("\n2. 간단한 텍스트 암호화/복호화")
            val testTexts = listOf(
                "안드로이드 사용자 데이터",
                "모바일 앱 설정 정보",
                "사용자 선호도 데이터"
            )

            val encryptedJsons = mutableListOf<String>()
            testTexts.forEachIndexed { index, text ->
                val encryptedJson = helper.encryptText(text)
                encryptedJsons.add(encryptedJson)
                println("암호화 [$index]: $text -> JSON 길이: ${encryptedJson.length}")
            }

            // 3. 복호화
            println("\n3. 저장된 JSON에서 복호화")
            encryptedJsons.forEachIndexed { index, json ->
                val decrypted = helper.decryptText(json)
                println("복호화 [$index]: $decrypted")
            }

            // 4. 실제 사용 시나리오 - 사용자 설정 저장/로드
            println("\n4. 실제 사용 시나리오 - 사용자 설정")

            // 사용자 설정 데이터 (JSON 형태)
            val userSettings = """{
        "userId": "user123",
        "email": "user@example.com",
        "preferences": {
            "theme": "dark",
            "notifications": true,
            "language": "ko"
        },
        "sensitiveData": "개인 식별 정보"
    }"""

            // 설정 암호화 및 저장 시뮬레이션
            val encryptedSettings = helper.encryptText(userSettings)
            println("사용자 설정 암호화 완료 (저장용): ${encryptedSettings.take(50)}...")

            // 설정 로드 및 복호화 시뮬레이션
            val loadedSettings = helper.decryptText(encryptedSettings)
            println("사용자 설정 복호화 완료:")
            println(loadedSettings)

            println("\n=== SimpleEncryptionHelper 데모 완료 ===")
        }

        @Test
        fun `키 마이그레이션 시나리오 데모`() {
            println("\n=== 키 마이그레이션 시나리오 데모 ===\n")

            val keyManager = RotatingKeyManager.createFileBasedManager(
                keyStorePath = "./build/keys/migration_demo_keys/encrypted_keys.dat",
                masterPassword = "migration-demo-password"
            )
            val encryptionService = VersionedEncryptionService(keyManager)

            // 1. 과거 데이터 시뮬레이션 (구 키로 암호화된 데이터)
            println("1. 과거 데이터 생성 (구 키 사용)")
            val oldVersion = "20241201"
            keyManager.getOrCreateKey(oldVersion)

            val legacyData = listOf(
                "2024년 12월 1일 암호화된 고객 데이터",
                "구버전 키로 저장된 주문 정보",
                "레거시 시스템 데이터"
            )

            val legacyEncrypted = legacyData.map { data ->
                encryptionService.encryptWithVersion(data, oldVersion)
            }

            println("과거 데이터 암호화 완료:")
            legacyEncrypted.forEach { encrypted ->
                println("  키버전: ${encrypted.keyVersion}, 타임스탬프: ${encrypted.timestamp}")
            }

            // 2. 새로운 키 버전 생성
            println("\n2. 새로운 키 버전으로 마이그레이션")
            val newVersion = "20241225"
            keyManager.getOrCreateKey(newVersion)

            // 3. 단일 데이터 재암호화
            println("\n3. 단일 데이터 재암호화")
            val singleMigrated = encryptionService.reEncryptWithNewKey(legacyEncrypted[0], newVersion)
            println("마이그레이션 전: 키버전=${legacyEncrypted[0].keyVersion}")
            println("마이그레이션 후: 키버전=${singleMigrated.keyVersion}")
            println("데이터 검증: ${encryptionService.decrypt(singleMigrated)}")

            // 4. 배치 재암호화 (실제 운영환경에서 사용할 방식)
            println("\n4. 배치 재암호화 (운영 환경 방식)")
            println("마이그레이션 대상 데이터: ${legacyEncrypted.size}개")

            val startTime = System.currentTimeMillis()
            val batchMigrated = encryptionService.reEncryptBatch(legacyEncrypted, newVersion)
            val endTime = System.currentTimeMillis()

            println("배치 마이그레이션 완료:")
            println("  처리 시간: ${endTime - startTime}ms")
            println("  처리된 데이터: ${batchMigrated.size}개")

            // 5. 마이그레이션 결과 검증
            println("\n5. 마이그레이션 결과 검증")
            val verificationResults = encryptionService.decryptBatch(batchMigrated)
            verificationResults.forEachIndexed { index, data ->
                val isValid = data == legacyData[index]
                println("  데이터 [$index]: ${if (isValid) "✓ 정상" else "✗ 오류"}")
            }

            // 6. 키 버전 통계
            println("\n6. 키 마이그레이션 통계")
            val beforeVersions = legacyEncrypted.groupBy { it.keyVersion }.mapValues { it.value.size }
            val afterVersions = batchMigrated.groupBy { it.keyVersion }.mapValues { it.value.size }

            println("마이그레이션 전 키 분포: $beforeVersions")
            println("마이그레이션 후 키 분포: $afterVersions")

            println("\n=== 키 마이그레이션 데모 완료 ===")
        }
    }
}
