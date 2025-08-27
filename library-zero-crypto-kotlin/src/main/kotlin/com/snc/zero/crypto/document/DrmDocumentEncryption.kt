package com.snc.zero.crypto.document

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * DRM 문서 암호화 및 보안 관리 시스템
 */

/**
 * DRM 문서 암호화 및 보안 관리 시스템
 */
class DrmDocumentEncryption {

    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 16
        private const val KEY_LENGTH = 256
    }

    /**
     * 1단계: AES 암호화 키 생성
     */
    fun generateEncryptionKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(KEY_LENGTH)
        return keyGenerator.generateKey()
    }

    /**
     * 2단계: 초기화 벡터(IV) 생성
     */
    fun generateIV(): ByteArray {
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        return iv
    }

    /**
     * 3단계: 문서 메타데이터 생성
     */
    fun createDocumentMetadata(
        documentId: String,
        userId: String,
        permissions: Set<Permission>,
        originalFileDate: LocalDateTime,
        expirationDate: LocalDateTime? = null
    ): DocumentMetadata {
        return DocumentMetadata(
            documentId = documentId,
            userId = userId,
            permissions = permissions,
            originalFileDate = originalFileDate,
            createdAt = LocalDateTime.now(),
            expirationDate = expirationDate,
            accessCount = 0
        )
    }

    /**
     * 4단계: 문서 내용 암호화
     */
    fun encryptDocument(
        content: ByteArray,
        secretKey: SecretKey,
        iv: ByteArray
    ): EncryptedContent {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)

        val encryptedData = cipher.doFinal(content)

        return EncryptedContent(
            data = encryptedData,
            iv = iv,
            algorithm = TRANSFORMATION
        )
    }

    /**
     * 5단계: 메타데이터 암호화
     */
    fun encryptMetadata(
        metadata: DocumentMetadata,
        secretKey: SecretKey
    ): EncryptedMetadata {
        val metadataJson = metadata.toJson()
        val iv = generateIV()
        val encryptedContent = encryptDocument(metadataJson.toByteArray(), secretKey, iv)

        return EncryptedMetadata(
            encryptedData = encryptedContent.data,
            iv = encryptedContent.iv
        )
    }

    /**
     * 6단계: DRM 보호 파일 생성
     */
    fun createDrmProtectedFile(
        originalContent: ByteArray,
        metadata: DocumentMetadata,
        secretKey: SecretKey
    ): DrmProtectedFile {
        val contentIv = generateIV()
        val encryptedContent = encryptDocument(originalContent, secretKey, contentIv)
        val encryptedMetadata = encryptMetadata(metadata, secretKey)

        val fileHash = generateFileHash(originalContent)

        return DrmProtectedFile(
            encryptedContent = encryptedContent,
            encryptedMetadata = encryptedMetadata,
            fileHash = fileHash,
            version = "1.0"
        )
    }

    /**
     * 7단계: 파일 해시 생성 (무결성 검증용)
     */
    fun generateFileHash(content: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(content)
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    /**
     * 8단계: 문서 복호화
     */
    fun decryptDocument(
        encryptedContent: EncryptedContent,
        secretKey: SecretKey
    ): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, encryptedContent.iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)

        return cipher.doFinal(encryptedContent.data)
    }

    /**
     * 9단계: 메타데이터 복호화
     */
    fun decryptMetadata(
        encryptedMetadata: EncryptedMetadata,
        secretKey: SecretKey
    ): DocumentMetadata {
        val encryptedContent = EncryptedContent(
            data = encryptedMetadata.encryptedData,
            iv = encryptedMetadata.iv,
            algorithm = TRANSFORMATION
        )

        val decryptedBytes = decryptDocument(encryptedContent, secretKey)
        val metadataJson = String(decryptedBytes)

        return DocumentMetadata.fromJson(metadataJson)
    }

    /**
     * 10단계: 권한 검증
     */
    fun validatePermissions(
        metadata: DocumentMetadata,
        userId: String,
        requiredPermission: Permission
    ): Boolean {
        // 사용자 ID 확인
        if (metadata.userId != userId) {
            return false
        }

        // 만료일 확인
        metadata.expirationDate?.let { expiration ->
            if (LocalDateTime.now().isAfter(expiration)) {
                return false
            }
        }

        // 권한 확인
        return metadata.permissions.contains(requiredPermission)
    }

    /**
     * 11단계: 파일 무결성 검증
     */
    fun verifyFileIntegrity(
        originalContent: ByteArray,
        expectedHash: String
    ): Boolean {
        val actualHash = generateFileHash(originalContent)
        return actualHash == expectedHash
    }

    /**
     * 12단계: DRM 파일을 디스크에 저장
     */
    fun saveDrmFile(
        drmFile: DrmProtectedFile,
        filePath: Path
    ) {
        val fileData = drmFile.toByteArray()
        Files.write(filePath, fileData)
    }

    /**
     * 13단계: DRM 파일을 디스크에서 로드
     */
    fun loadDrmFile(filePath: Path): DrmProtectedFile {
        val fileData = Files.readAllBytes(filePath)
        return DrmProtectedFile.fromByteArray(fileData)
    }

    /**
     * 14단계: 접근 로그 기록
     */
    fun logAccess(
        documentId: String,
        userId: String,
        action: AccessAction,
        success: Boolean
    ): AccessLog {
        return AccessLog(
            documentId = documentId,
            userId = userId,
            action = action,
            timestamp = LocalDateTime.now(),
            success = success,
            ipAddress = "127.0.0.1" // 실제 구현시 실제 IP 주소 사용
        )
    }
}

/**
 * 권한 열거형
 */
enum class Permission {
    READ,
    WRITE,
    PRINT,
    COPY,
    SHARE
}

/**
 * 접근 행동 열거형
 */
enum class AccessAction {
    OPEN,
    READ,
    EDIT,
    PRINT,
    COPY,
    SHARE
}

/**
 * 문서 메타데이터 데이터 클래스
 */
data class DocumentMetadata(
    val documentId: String,
    val userId: String,
    val permissions: Set<Permission>,
    val originalFileDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val expirationDate: LocalDateTime?,
    val accessCount: Int
) {
    fun toJson(): String {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val permissionsStr = permissions.joinToString(",") { it.name }
        val expirationStr = expirationDate?.format(formatter) ?: ""

        return """
            {
                "documentId": "$documentId",
                "userId": "$userId",
                "permissions": "$permissionsStr",
                "originalFileDate": "${originalFileDate.format(formatter)}",
                "createdAt": "${createdAt.format(formatter)}",
                "expirationDate": "$expirationStr",
                "accessCount": $accessCount
            }
        """.trimIndent()
    }

    companion object {
        fun fromJson(json: String): DocumentMetadata {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val cleanJson = json.replace(Regex("\\s+"), "")

            val documentId = extractJsonValue(cleanJson, "documentId")
            val userId = extractJsonValue(cleanJson, "userId")
            val permissionsStr = extractJsonValue(cleanJson, "permissions")
            val originalFileDateStr = extractJsonValue(cleanJson, "originalFileDate")
            val createdAtStr = extractJsonValue(cleanJson, "createdAt")
            val expirationStr = extractJsonValue(cleanJson, "expirationDate")
            val accessCountStr = extractJsonValue(cleanJson, "accessCount")
            val accessCount = if (accessCountStr.isNotEmpty()) accessCountStr.toInt() else 0

            val permissions = if (permissionsStr.isNotEmpty()) {
                permissionsStr.split(",").map { Permission.valueOf(it) }.toSet()
            } else {
                emptySet()
            }

            val originalFileDate = LocalDateTime.parse(originalFileDateStr, formatter)
            val createdAt = LocalDateTime.parse(createdAtStr, formatter)
            val expirationDate = if (expirationStr.isNotEmpty()) {
                LocalDateTime.parse(expirationStr, formatter)
            } else {
                null
            }

            return DocumentMetadata(
                documentId = documentId,
                userId = userId,
                permissions = permissions,
                originalFileDate = originalFileDate,
                createdAt = createdAt,
                expirationDate = expirationDate,
                accessCount = accessCount
            )
        }

        private fun extractJsonValue(json: String, key: String): String {
            val pattern = """"$key"\s*:\s*"([^"]*?)"""".toRegex()
            val match = pattern.find(json)
            if (match != null) {
                return match.groupValues[1]
            }

            // 숫자 값인 경우 (따옴표 없음)
            val numberPattern = """"$key"\s*:\s*(\d+)""".toRegex()
            val numberMatch = numberPattern.find(json)
            return numberMatch?.groupValues?.get(1) ?: ""
        }
    }
}

/**
 * 암호화된 내용 데이터 클래스
 */
data class EncryptedContent(
    val data: ByteArray,
    val iv: ByteArray,
    val algorithm: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedContent

        if (!data.contentEquals(other.data)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (algorithm != other.algorithm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + algorithm.hashCode()
        return result
    }
}

/**
 * 암호화된 메타데이터 데이터 클래스
 */
data class EncryptedMetadata(
    val encryptedData: ByteArray,
    val iv: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedMetadata

        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (!iv.contentEquals(other.iv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedData.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        return result
    }
}

/**
 * DRM 보호 파일 데이터 클래스
 */
data class DrmProtectedFile(
    val encryptedContent: EncryptedContent,
    val encryptedMetadata: EncryptedMetadata,
    val fileHash: String,
    val version: String
) {
    fun toByteArray(): ByteArray {
        // 실제 구현에서는 적절한 직렬화 방식 사용 (예: Protocol Buffers, JSON 등)
        val contentData = Base64.getEncoder().encodeToString(encryptedContent.data)
        val contentIv = Base64.getEncoder().encodeToString(encryptedContent.iv)
        val metadataData = Base64.getEncoder().encodeToString(encryptedMetadata.encryptedData)
        val metadataIv = Base64.getEncoder().encodeToString(encryptedMetadata.iv)

        val serializedData = """
            {
                "version": "$version",
                "fileHash": "$fileHash",
                "encryptedContent": {
                    "data": "$contentData",
                    "iv": "$contentIv",
                    "algorithm": "${encryptedContent.algorithm}"
                },
                "encryptedMetadata": {
                    "data": "$metadataData",
                    "iv": "$metadataIv"
                }
            }
        """.trimIndent()

        return serializedData.toByteArray()
    }

    companion object {
        fun fromByteArray(data: ByteArray): DrmProtectedFile {
            val json = String(data)

            // JSON 파싱 (실제 구현에서는 Jackson, Gson 등 사용 권장)
            val version = extractJsonValue(json, "version")
            val fileHash = extractJsonValue(json, "fileHash")

            val contentData = Base64.getDecoder().decode(extractNestedJsonValue(json, "encryptedContent", "data"))
            val contentIv = Base64.getDecoder().decode(extractNestedJsonValue(json, "encryptedContent", "iv"))
            val algorithm = extractNestedJsonValue(json, "encryptedContent", "algorithm")

            val metadataData = Base64.getDecoder().decode(extractNestedJsonValue(json, "encryptedMetadata", "data"))
            val metadataIv = Base64.getDecoder().decode(extractNestedJsonValue(json, "encryptedMetadata", "iv"))

            val encryptedContent = EncryptedContent(contentData, contentIv, algorithm)
            val encryptedMetadata = EncryptedMetadata(metadataData, metadataIv)

            return DrmProtectedFile(encryptedContent, encryptedMetadata, fileHash, version)
        }

        private fun extractJsonValue(json: String, key: String): String {
            val pattern = """"$key"\s*:\s*"([^"]*?)"""".toRegex()
            return pattern.find(json)?.groupValues?.get(1) ?: ""
        }

        private fun extractNestedJsonValue(json: String, parentKey: String, childKey: String): String {
            val parentPattern = """"$parentKey"\s*:\s*\{([^}]*)\}""".toRegex()
            val parentMatch = parentPattern.find(json)?.groupValues?.get(1) ?: ""
            return extractJsonValue(parentMatch, childKey)
        }
    }
}

/**
 * 접근 로그 데이터 클래스
 */
data class AccessLog(
    val documentId: String,
    val userId: String,
    val action: AccessAction,
    val timestamp: LocalDateTime,
    val success: Boolean,
    val ipAddress: String
)
