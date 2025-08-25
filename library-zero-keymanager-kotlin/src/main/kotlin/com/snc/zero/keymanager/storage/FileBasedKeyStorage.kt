package com.snc.zero.keymanager.storage

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 키 저장소 인터페이스 - 나중에 Vault, KMS 등으로 교체 가능
 */
interface KeyStorage {
    fun saveKey(version: String, encryptedKey: String)
    fun loadKey(version: String): String?
    fun getAllKeyVersions(): List<String>
    fun deleteKey(version: String): Boolean
    fun keyExists(version: String): Boolean
}

/**
 * 파일 기반 키 저장소 구현
 *
 * // Vault 연동 시
 * class VaultKeyStorage(private val vaultClient: VaultClient) : KeyStorage {
 *     override fun saveKey(version: String, encryptedKey: String) {
 *         vaultClient.write("secret/app-keys/$version", mapOf("key" to encryptedKey))
 *     }
 *     // ... 구현
 * }
 */
class FileBasedKeyStorage(
    keyStorePath: String,
    masterPassword: String = System.getenv("MASTER_PASSWORD")
) : KeyStorage {

    private val keyStoreFile = File(keyStorePath)
    private val masterKey: SecretKeySpec = generateMasterKey(masterPassword)

    init {
        // 키 저장소 디렉토리 생성
        keyStoreFile.parentFile?.mkdirs()

        // 키 저장소 파일이 없으면 빈 저장소 생성
        if (!keyStoreFile.exists()) {
            saveEncryptedKeyStore(mutableMapOf())
        }
    }

    override fun saveKey(version: String, encryptedKey: String) {
        val keyStore = loadEncryptedKeyStore().toMutableMap()
        keyStore[version] = StoredKeyEntry(
            key = encryptedKey,
            createdAt = System.currentTimeMillis()
        )
        saveEncryptedKeyStore(keyStore)
    }

    override fun loadKey(version: String): String? {
        val keyStore = loadEncryptedKeyStore()
        return keyStore[version]?.key
    }

    override fun getAllKeyVersions(): List<String> {
        return loadEncryptedKeyStore().keys.toList().sorted()
    }

    override fun deleteKey(version: String): Boolean {
        val keyStore = loadEncryptedKeyStore().toMutableMap()
        val removed = keyStore.remove(version)
        if (removed != null) {
            saveEncryptedKeyStore(keyStore)
            return true
        }
        return false
    }

    override fun keyExists(version: String): Boolean {
        return loadEncryptedKeyStore().containsKey(version)
    }

    private fun generateMasterKey(password: String): SecretKeySpec {
        // PBKDF2로 마스터 키 생성 (실제로는 더 복잡한 키 유도 함수 사용 권장)
        val salt = "FixedSaltForDemo".toByteArray() // 실제로는 랜덤 솔트 사용
        val keyBytes = password.toByteArray() + salt
        val hash = MessageDigest.getInstance("SHA-256").digest(keyBytes)
        return SecretKeySpec(hash, "AES")
    }

    private fun loadEncryptedKeyStore(): Map<String, StoredKeyEntry> {
        if (!keyStoreFile.exists()) {
            return emptyMap()
        }

        return try {
            val encryptedData = keyStoreFile.readBytes()
            val decryptedJson = decryptData(encryptedData)
            Json.decodeFromString<Map<String, StoredKeyEntry>>(decryptedJson)
        } catch (e: Exception) {
            println("키 저장소 로드 실패: ${e.message}")
            emptyMap()
        }
    }

    private fun saveEncryptedKeyStore(keyStore: Map<String, StoredKeyEntry>) {
        try {
            val json = Json.encodeToString(keyStore)
            val encryptedData = encryptData(json)
            keyStoreFile.writeBytes(encryptedData)
        } catch (e: Exception) {
            throw RuntimeException("키 저장소 저장 실패: ${e.message}", e)
        }
    }

    private fun encryptData(data: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, masterKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray())

        // IV + 암호화된 데이터 결합
        return iv + encryptedBytes
    }

    private fun decryptData(encryptedData: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = encryptedData.sliceArray(0..11) // GCM IV는 12바이트
        val cipherText = encryptedData.sliceArray(12 until encryptedData.size)

        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, masterKey, spec)
        val decryptedBytes = cipher.doFinal(cipherText)

        return String(decryptedBytes)
    }
}

/**
 * 키 저장소 엔트리 데이터 클래스
 */
@Serializable
data class StoredKeyEntry(
    val key: String,
    val createdAt: Long
)

/**
 * 메인 키 관리자 클래스
 */
class RotatingKeyManager(
    private val keyStorage: KeyStorage
) {

    private val keyCache = ConcurrentHashMap<String, SecretKeySpec>()
    private val secureRandom = SecureRandom()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    companion object {
        // 편의 생성자 - 파일 기반 저장소 사용
        fun createFileBasedManager(
            keyStorePath: String,
            masterPassword: String
        ): RotatingKeyManager {
            val storage = FileBasedKeyStorage(keyStorePath, masterPassword)
            return RotatingKeyManager(storage)
        }
    }

    /**
     * 현재 날짜에 해당하는 키를 가져오거나 생성
     */
    fun getCurrentKey(): Pair<String, SecretKeySpec> {
        val today = LocalDate.now().format(dateFormatter)
        return getOrCreateKey(today)
    }

    /**
     * 특정 버전의 키 가져오기 (복호화용)
     */
    fun getKeyByVersion(version: String): SecretKeySpec {
        return keyCache[version] ?: loadKeyFromStorage(version)
            ?: throw IllegalArgumentException("키 버전 '$version'을 찾을 수 없습니다")
    }

    /**
     * 특정 날짜의 키를 가져오거나 생성
     */
    fun getOrCreateKey(version: String): Pair<String, SecretKeySpec> {
        val cachedKey = keyCache[version]
        if (cachedKey != null) {
            return version to cachedKey
        }

        val loadedKey = loadKeyFromStorage(version)
        if (loadedKey != null) {
            return version to loadedKey
        }

        // 키가 없으면 새로 생성
        return createNewKey(version)
    }

    /**
     * 새로운 키 생성
     */
    private fun createNewKey(version: String): Pair<String, SecretKeySpec> {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256, secureRandom)
        val newKey = keyGen.generateKey()
        val keySpec = SecretKeySpec(newKey.encoded, "AES")

        // 키를 Base64로 인코딩하여 저장
        val encodedKey = Base64.getEncoder().encodeToString(newKey.encoded)
        keyStorage.saveKey(version, encodedKey)

        // 캐시에 저장
        keyCache[version] = keySpec

        println("새로운 키 생성됨: $version")
        return version to keySpec
    }

    /**
     * 저장소에서 키 로드
     */
    private fun loadKeyFromStorage(version: String): SecretKeySpec? {
        val encodedKey = keyStorage.loadKey(version) ?: return null

        return try {
            val keyBytes = Base64.getDecoder().decode(encodedKey)
            val keySpec = SecretKeySpec(keyBytes, "AES")
            keyCache[version] = keySpec // 캐시에 저장
            keySpec
        } catch (e: Exception) {
            println("키 로드 실패 (버전: $version): ${e.message}")
            null
        }
    }

    /**
     * 사용 가능한 모든 키 버전 조회
     */
    fun getAllKeyVersions(): List<String> {
        return keyStorage.getAllKeyVersions()
    }

    /**
     * 오래된 키 정리 (선택적)
     */
    fun cleanupOldKeys(daysToKeep: Int = 90) {
        val allVersions = getAllKeyVersions()
        val cutoffDate = LocalDate.now().minusDays(daysToKeep.toLong())

        allVersions.forEach { version ->
            try {
                val versionDate = LocalDate.parse(version, dateFormatter)
                if (versionDate.isBefore(cutoffDate)) {
                    keyStorage.deleteKey(version)
                    keyCache.remove(version)
                    println("오래된 키 삭제됨: $version")
                }
            } catch (e: Exception) {
                // 날짜 형식이 아닌 버전은 건너뜀
            }
        }
    }

    /**
     * 캐시 클리어
     */
    fun clearCache() {
        keyCache.clear()
    }

    /**
     * 키 존재 여부 확인
     */
    fun keyExists(version: String): Boolean {
        return keyCache.containsKey(version) || keyStorage.keyExists(version)
    }
}
