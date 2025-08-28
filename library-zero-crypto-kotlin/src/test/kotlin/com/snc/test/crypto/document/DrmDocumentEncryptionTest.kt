package com.snc.test.crypto.document

import com.snc.zero.crypto.document.AccessAction
import com.snc.zero.crypto.document.DrmDocumentEncryption
import com.snc.zero.crypto.document.Permission
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

@Suppress("NonAsciiCharacters")
class DrmDocumentEncryptionTest {

    private lateinit var drmSystem: DrmDocumentEncryption
    private val testContent = "이것은 기밀 문서입니다. 무단 복제를 금지합니다.".toByteArray()
    private val testUserId = "user123"
    private val testDocumentId = "doc456"

    @BeforeEach
    fun setUp() {
        drmSystem = DrmDocumentEncryption()
    }

    @Nested
    inner class DrmUsageExample {
        @Test
        fun `DRM 시스템 사용 예제`() {
            val drm = DrmDocumentEncryption()

            // 1. 원본 문서와 사용자 정보
            val originalDocument = "중요한 기밀 문서 내용입니다.".toByteArray()
            val userId = "john.doe@company.com"
            val documentId = "confidential-doc-001"

            // 2. 암호화 키 생성
            val secretKey = drm.generateEncryptionKey()
            println("암호화 키 생성 완료")

            // 3. 문서 메타데이터 생성 (읽기, 인쇄 권한만 부여, 30일 후 만료)
            val originalFileDate = LocalDateTime.of(2024, 1, 15, 14, 30) // 원본 파일 생성일
            val metadata = drm.createDocumentMetadata(
                documentId = documentId,
                userId = userId,
                permissions = setOf(Permission.READ, Permission.PRINT),
                originalFileDate = originalFileDate,
                expirationDate = LocalDateTime.now().plusDays(30)
            )
            println("메타데이터 생성 완료: ${metadata.permissions}")

            // 4. DRM 보호 파일 생성
            val drmFile = drm.createDrmProtectedFile(originalDocument, metadata, secretKey)
            println("DRM 보호 파일 생성 완료")

            // 5. 파일을 디스크에 저장
            val buildDir = Paths.get("build")
            Files.createDirectories(buildDir) // build 폴더가 없으면 생성
            val filePath = buildDir.resolve("protected-document.drm")
            drm.saveDrmFile(drmFile, filePath)
            println("파일 저장 완료: $filePath")

            // 6. 파일 로드 및 복호화 시뮬레이션
            val loadedDrmFile = drm.loadDrmFile(filePath)

            var accessLog = drm.logAccess(documentId, userId, AccessAction.OPEN, true)
            println("접근 로그 기록: ${accessLog.timestamp}")

            val decryptedMetadata = drm.decryptMetadata(loadedDrmFile.encryptedMetadata, secretKey)

            // 7. 권한 검증
            val canRead = drm.validatePermissions(decryptedMetadata, userId, Permission.READ)
            val canWrite = drm.validatePermissions(decryptedMetadata, userId, Permission.WRITE)

            println("읽기 권한: $canRead")
            println("쓰기 권한: $canWrite")

            // 8. 권한이 있는 경우에만 문서 복호화
            if (canRead) {
                val decryptedDocument = drm.decryptDocument(loadedDrmFile.encryptedContent, secretKey)
                val documentContent = String(decryptedDocument)

                // 9. 무결성 검증
                val isIntegrityValid = drm.verifyFileIntegrity(decryptedDocument, loadedDrmFile.fileHash)

                println("문서 내용: $documentContent")
                println("무결성 검증: $isIntegrityValid")

                // 10. 접근 로그 기록
                val accessLog = drm.logAccess(documentId, userId, AccessAction.READ, true)
                println("접근 로그 기록: ${accessLog.timestamp}")
            } else {
                val accessLog = drm.logAccess(documentId, userId, AccessAction.READ, false)
                println("접근 거부됨: ${accessLog.timestamp}")
            }

            // 임시 파일 삭제
            //Files.deleteIfExists(filePath)
            println("임시 파일 삭제 완료")

            accessLog = drm.logAccess(documentId, userId, AccessAction.EDIT, true)
            println("접근 로그 기록: ${accessLog.timestamp}")
        }
    }

    @Nested
    @DisplayName("암호화키 생성 테스트")
    inner class EncryptionKeyGenerationTest {

        @Test
        fun `암호화 키가 정상적으로 생성되는지 확인`() {
            // Given & When
            val secretKey = drmSystem.generateEncryptionKey()

            // Then
            assertNotNull(secretKey)
            assertEquals("AES", secretKey.algorithm)
        }

        @Test
        fun `IV가 정상적으로 생성되는지 확인`() {
            // Given & When
            val iv = drmSystem.generateIV()

            // Then
            assertNotNull(iv)
            assertEquals(12, iv.size) // GCM IV 길이
        }
    }

    @Nested
    @DisplayName("메타데이터 생성 테스트")
    inner class MetadataGenerationTest {

        @Test
        fun `문서 메타데이터가 정상적으로 생성되는지 확인`() {
            // Given
            val permissions = setOf(Permission.READ, Permission.PRINT)
            val originalFileDate = LocalDateTime.of(2024, 1, 15, 10, 30)
            val expirationDate = LocalDateTime.now().plusDays(30)

            // When
            val metadata = drmSystem.createDocumentMetadata(
                documentId = testDocumentId,
                userId = testUserId,
                permissions = permissions,
                originalFileDate = originalFileDate,
                expirationDate = expirationDate
            )

            // Then
            assertEquals(testDocumentId, metadata.documentId)
            assertEquals(testUserId, metadata.userId)
            assertEquals(permissions, metadata.permissions)
            assertEquals(originalFileDate, metadata.originalFileDate)
            assertEquals(0, metadata.accessCount)
            assertNotNull(metadata.createdAt)
        }
    }

    @Nested
    @DisplayName("암호화 복호화 테스트")
    inner class EncryptionDecryptionTest {

        @Test
        fun `문서 암호화와 복호화가 정상적으로 작동하는지 확인`() {
            // Given
            val secretKey = drmSystem.generateEncryptionKey()
            val iv = drmSystem.generateIV()

            // When
            val encryptedContent = drmSystem.encryptDocument(testContent, secretKey, iv)
            val decryptedContent = drmSystem.decryptDocument(encryptedContent, secretKey)

            // Then
            assertNotNull(encryptedContent)
            assertArrayEquals(testContent, decryptedContent)
        }

        @Test
        fun `메타데이터 암호화와 복호화가 정상적으로 작동하는지 확인`() {
            // Given
            val secretKey = drmSystem.generateEncryptionKey()
            val permissions = setOf(Permission.READ, Permission.WRITE)
            val originalMetadata = drmSystem.createDocumentMetadata(
                documentId = testDocumentId,
                userId = testUserId,
                permissions = permissions,
                originalFileDate = LocalDateTime.of(2024, 1, 15, 10, 30)
            )

            // When
            val encryptedMetadata = drmSystem.encryptMetadata(originalMetadata, secretKey)
            val decryptedMetadata = drmSystem.decryptMetadata(encryptedMetadata, secretKey)

            // Then
            assertEquals(originalMetadata.documentId, decryptedMetadata.documentId)
            assertEquals(originalMetadata.userId, decryptedMetadata.userId)
            assertEquals(originalMetadata.permissions, decryptedMetadata.permissions)
        }
    }

    @Nested
    @DisplayName("DRM 파일 생성 테스트")
    inner class DrmFileGenerationTest {

        @Test
        fun `DRM 보호 파일이 정상적으로 생성되는지 확인`() {
            // Given
            val secretKey = drmSystem.generateEncryptionKey()
            val permissions = setOf(Permission.READ, Permission.PRINT)
            val metadata = drmSystem.createDocumentMetadata(
                documentId = testDocumentId,
                userId = testUserId,
                permissions = permissions,
                originalFileDate = LocalDateTime.of(2024, 1, 15, 10, 30)
            )

            // When
            val drmFile = drmSystem.createDrmProtectedFile(testContent, metadata, secretKey)

            // Then
            assertNotNull(drmFile)
            assertNotNull(drmFile.encryptedContent)
            assertNotNull(drmFile.encryptedMetadata)
            assertNotNull(drmFile.fileHash)
            assertEquals("1.0", drmFile.version)
        }
    }

    @Nested
    @DisplayName("권한 검증 테스트")
    inner class PermissionVerificationTest {

        @Test
        fun `올바른 권한으로 접근시 성공하는지 확인`() {
            // Given
            val permissions = setOf(Permission.READ, Permission.PRINT)
            val metadata = drmSystem.createDocumentMetadata(
                documentId = testDocumentId,
                userId = testUserId,
                permissions = permissions,
                originalFileDate = LocalDateTime.of(2024, 1, 15, 10, 30)
            )

            // When
            val hasReadPermission = drmSystem.validatePermissions(
                metadata,
                testUserId,
                Permission.READ
            )
            val hasPrintPermission = drmSystem.validatePermissions(
                metadata,
                testUserId,
                Permission.PRINT
            )
            val hasWritePermission = drmSystem.validatePermissions(
                metadata,
                testUserId,
                Permission.WRITE
            )

            // Then
            assertTrue(hasReadPermission)
            assertTrue(hasPrintPermission)
            assertFalse(hasWritePermission)
        }

        @Test
        fun `다른 사용자가 접근시 실패하는지 확인`() {
            // Given
            val permissions = setOf(Permission.READ)
            val metadata = drmSystem.createDocumentMetadata(
                documentId = testDocumentId,
                userId = testUserId,
                originalFileDate = LocalDateTime.of(2024, 1, 15, 10, 30),
                permissions = permissions
            )

            // When
            val hasPermission = drmSystem.validatePermissions(
                metadata,
                "otherUser",
                Permission.READ
            )

            // Then
            assertFalse(hasPermission)
        }

        @Test
        fun `만료된 문서 접근시 실패하는지 확인`() {
            // Given
            val permissions = setOf(Permission.READ)
            val expiredDate = LocalDateTime.now().minusDays(1)
            val metadata = drmSystem.createDocumentMetadata(
                documentId = testDocumentId,
                userId = testUserId,
                permissions = permissions,
                originalFileDate = LocalDateTime.of(2024, 1, 15, 10, 30),
                expirationDate = expiredDate
            )

            // When
            val hasPermission = drmSystem.validatePermissions(
                metadata,
                testUserId,
                Permission.READ
            )

            // Then
            assertFalse(hasPermission)
        }
    }

    @Nested
    @DisplayName("파일 무결성 검증 테스트")
    inner class FileIntegrityVerificationTest {

        @Test
        fun `파일 무결성 검증이 정상적으로 작동하는지 확인`() {
            // Given
            val originalHash = drmSystem.generateFileHash(testContent)
            val modifiedContent = "변조된 내용".toByteArray()

            // When
            val isOriginalValid = drmSystem.verifyFileIntegrity(testContent, originalHash)
            val isModifiedValid = drmSystem.verifyFileIntegrity(modifiedContent, originalHash)

            // Then
            assertTrue(isOriginalValid)
            assertFalse(isModifiedValid)
        }
    }

    @Nested
    @DisplayName("액세스 로그 테스트")
    inner class AccessLogTest {

        @Test
        fun `접근 로그가 정상적으로 생성되는지 확인`() {
            // Given & When
            val accessLog = drmSystem.logAccess(
                documentId = testDocumentId,
                userId = testUserId,
                action = AccessAction.READ,
                success = true
            )

            // Then
            assertEquals(testDocumentId, accessLog.documentId)
            assertEquals(testUserId, accessLog.userId)
            assertEquals(AccessAction.READ, accessLog.action)
            assertTrue(accessLog.success)
            assertNotNull(accessLog.timestamp)
        }
    }
}
