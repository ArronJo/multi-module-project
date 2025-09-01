package com.snc.zero.keymanager.service

import com.snc.zero.keymanager.storage.RotatingKeyManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 암호화된 데이터와 메타데이터를 포함하는 데이터 클래스
 */
@Serializable
data class EncryptedData(
    val data: String, // Base64 인코딩된 암호화 데이터
    val iv: String, // Base64 인코딩된 초기화 벡터
    val keyVersion: String, // 사용된 키 버전
    val timestamp: Long = System.currentTimeMillis() // 암호화 시점
)

/**
 * 키 버전 관리가 포함된 암호화/복호화 서비스
 */
class VersionedEncryptionService(
    private val keyManager: RotatingKeyManager
) {

    private val cipher = Cipher.getInstance("AES/GCM/NoPadding")

    /**
     * 데이터 암호화 (현재 키 사용)
     */
    fun encrypt(plainText: String): EncryptedData {
        val (keyVersion, secretKey) = keyManager.getCurrentKey()
        return encryptWithVersion(plainText, keyVersion, secretKey)
    }

    /**
     * 특정 키 버전으로 데이터 암호화
     */
    fun encryptWithVersion(plainText: String, keyVersion: String): EncryptedData {
        val secretKey = keyManager.getKeyByVersion(keyVersion)
        return encryptWithVersion(plainText, keyVersion, secretKey)
    }

    /**
     * 내부 암호화 구현
     */
    private fun encryptWithVersion(plainText: String, keyVersion: String, secretKey: SecretKeySpec): EncryptedData {
        synchronized(cipher) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            return EncryptedData(
                data = Base64.getEncoder().encodeToString(encryptedBytes),
                iv = Base64.getEncoder().encodeToString(iv),
                keyVersion = keyVersion
            )
        }
    }

    /**
     * 데이터 복호화
     */
    fun decrypt(encryptedData: EncryptedData): String {
        val secretKey = keyManager.getKeyByVersion(encryptedData.keyVersion)

        synchronized(cipher) {
            val ivBytes = Base64.getDecoder().decode(encryptedData.iv)
            val encryptedBytes = Base64.getDecoder().decode(encryptedData.data)

            val spec = GCMParameterSpec(128, ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes, Charsets.UTF_8)
        }
    }

    /**
     * 여러 데이터 일괄 복호화 (성능 최적화)
     */
    fun decryptBatch(encryptedDataList: List<EncryptedData>): List<String> {
        // 키 버전별로 그룹화하여 키 로딩 최소화
        val groupedByVersion = encryptedDataList.groupBy { it.keyVersion }
        val results = mutableMapOf<Int, String>() // 원래 인덱스 유지

        groupedByVersion.forEach { (version, dataList) ->
            val secretKey = keyManager.getKeyByVersion(version)

            dataList.forEachIndexed { _, encData ->
                val originalIndex = encryptedDataList.indexOf(encData)
                results[originalIndex] = decryptSingle(encData, secretKey)
            }
        }

        // 원래 순서대로 결과 반환
        return encryptedDataList.indices.map { results.getOrDefault(it, "") }
    }

    /**
     * 단일 데이터 복호화 (키가 이미 로드된 경우)
     */
    private fun decryptSingle(encryptedData: EncryptedData, secretKey: SecretKeySpec): String {
        synchronized(cipher) {
            val ivBytes = Base64.getDecoder().decode(encryptedData.iv)
            val encryptedBytes = Base64.getDecoder().decode(encryptedData.data)

            val spec = GCMParameterSpec(128, ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes, Charsets.UTF_8)
        }
    }

    /**
     * 암호화된 데이터를 JSON 문자열로 직렬화
     */
    fun encryptedDataToJson(encryptedData: EncryptedData): String {
        return Json.encodeToString(encryptedData)
    }

    /**
     * JSON 문자열을 암호화된 데이터로 역직렬화
     */
    fun encryptedDataFromJson(json: String): EncryptedData {
        return Json.decodeFromString(json)
    }

    /**
     * 데이터 재암호화 (키 마이그레이션용)
     */
    fun reEncryptWithNewKey(encryptedData: EncryptedData, newKeyVersion: String): EncryptedData {
        val plainText = decrypt(encryptedData)
        return encryptWithVersion(plainText, newKeyVersion)
    }

    /**
     * 여러 데이터 일괄 재암호화
     */
    fun reEncryptBatch(encryptedDataList: List<EncryptedData>, newKeyVersion: String): List<EncryptedData> {
        return encryptedDataList.map { encData ->
            val plainText = decrypt(encData)
            encryptWithVersion(plainText, newKeyVersion)
        }
    }
}

/**
 * Android/모바일 환경을 위한 간단한 헬퍼 클래스
 */
class SimpleEncryptionHelper private constructor(
    private val encryptionService: VersionedEncryptionService
) {

    companion object {
        fun create(keyStorePath: String, masterPassword: String): SimpleEncryptionHelper {
            val keyManager = RotatingKeyManager.createFileBasedManager(keyStorePath, masterPassword)
            val encryptionService = VersionedEncryptionService(keyManager)
            return SimpleEncryptionHelper(encryptionService)
        }
    }

    /**
     * 간단한 텍스트 암호화 (JSON 문자열 반환)
     */
    fun encryptText(plainText: String): String {
        val encrypted = encryptionService.encrypt(plainText)
        return encryptionService.encryptedDataToJson(encrypted)
    }

    /**
     * 간단한 텍스트 복호화 (JSON 문자열 입력)
     */
    fun decryptText(encryptedJson: String): String {
        val encrypted = encryptionService.encryptedDataFromJson(encryptedJson)
        return encryptionService.decrypt(encrypted)
    }
}
