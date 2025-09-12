package com.snc.test.similarity.matcher

import com.snc.zero.similarity.data.FileStringDataProvider
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

@Suppress("NonAsciiCharacters")
class FileStringDataProviderTest : BaseJUnit5Test() {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var testFilePath: String
    private lateinit var emptyFilePath: String
    private lateinit var nonExistentFilePath: String

    @BeforeEach
    fun setup() {
        // 테스트용 파일들 준비
        val testFile = tempDir.resolve("test-data.txt").toFile()
        testFile.writeText("""
            첫번째 라인
            두번째 라인
            
            네번째 라인 (위에 빈 줄)
            다섯번째 라인    
        """.trimIndent())
        testFilePath = testFile.absolutePath

        val emptyFile = tempDir.resolve("empty-data.txt").toFile()
        emptyFile.writeText("")
        emptyFilePath = emptyFile.absolutePath

        nonExistentFilePath = tempDir.resolve("non-existent.txt").toString()
    }

    @Nested
    inner class FunctionFileRead {

        @Test
        @DisplayName("정상적인 파일에서 데이터를 읽어온다")
        fun `정상 파일 데이터 읽기`() {
            // Given
            val provider = FileStringDataProvider(testFilePath)

            // When
            val data = provider.getData()

            // Then
            assertFalse(provider.isEmpty())
            assertEquals(4, provider.size())
            assertEquals(4, data.size)
            assertEquals("첫번째 라인", data[0])
            assertEquals("두번째 라인", data[1])
            assertEquals("네번째 라인 (위에 빈 줄)", data[2])
            assertEquals("다섯번째 라인", data[3])
        }

        @Test
        @DisplayName("빈 파일은 빈 리스트를 반환한다")
        fun `빈 파일 처리`() {
            // Given
            val provider = FileStringDataProvider(emptyFilePath)

            // When
            val data = provider.getData()

            // Then
            assertTrue(provider.isEmpty())
            assertEquals(0, provider.size())
            assertTrue(data.isEmpty())
        }

        @Test
        @DisplayName("존재하지 않는 파일은 빈 리스트를 반환한다")
        fun `존재하지 않는 파일 처리`() {
            // Given
            val provider = FileStringDataProvider(nonExistentFilePath)

            // When
            val data = provider.getData()

            // Then
            assertTrue(provider.isEmpty())
            assertEquals(0, provider.size())
            assertTrue(data.isEmpty())
        }
    }

    @Nested
    inner class FunctionCache {

        @Test
        @DisplayName("데이터는 한 번 읽혀진 후 캐시된다")
        fun `데이터 캐시 동작`() {
            // Given
            val provider = FileStringDataProvider(testFilePath)

            // When - 첫 번째 호출
            val firstCall = provider.getData()

            // 파일을 수정
            val testFile = File(testFilePath)
            testFile.writeText("수정된 내용")

            // 두 번째 호출 (캐시된 데이터 반환)
            val secondCall = provider.getData()

            // Then
            assertEquals(firstCall, secondCall) // 캐시된 데이터와 동일
            assertEquals("첫번째 라인", secondCall[0]) // 원래 데이터 유지
        }

        @Test
        @DisplayName("캐시를 새로고침하면 변경된 파일 내용을 읽는다")
        fun `캐시 새로고침 기능`() {
            // Given
            val provider = FileStringDataProvider(testFilePath)
            val originalData = provider.getData()

            // When - 파일 내용 변경
            val testFile = File(testFilePath)
            testFile.writeText("새로운 첫 줄\n새로운 둘째 줄")

            // 캐시 새로고침
            provider.refreshCache()
            val refreshedData = provider.getData()

            // Then
            assertNotEquals(originalData, refreshedData)
            assertEquals(2, refreshedData.size)
            assertEquals("새로운 첫 줄", refreshedData[0])
            assertEquals("새로운 둘째 줄", refreshedData[1])
        }

        @Test
        @DisplayName("clearData 호출 시 캐시가 빈 리스트로 설정된다")
        fun `clearData로 캐시 초기화`() {
            // Given
            val provider = FileStringDataProvider(testFilePath)
            assertFalse(provider.isEmpty()) // 초기에는 데이터가 있음

            // When
            provider.clearData()

            // Then
            assertTrue(provider.isEmpty())
            assertEquals(0, provider.size())
            assertTrue(provider.getData().isEmpty())
        }
    }

    @Nested
    inner class UnsupportedOperation {

        @Test
        @DisplayName("데이터 추가는 지원되지 않는다")
        fun `데이터 추가 불가능`() {
            // Given
            val provider = FileStringDataProvider(testFilePath)

            // When & Then
            assertThrows(UnsupportedOperationException::class.java) {
                provider.addData("새 데이터")
            }

            assertThrows(UnsupportedOperationException::class.java) {
                provider.addData(listOf("데이터1", "데이터2"))
            }
        }

        @Test
        @DisplayName("인코딩을 지정하여 파일을 읽을 수 있다")
        fun `인코딩 지정 기능`() {
            // Given
            val utf8File = tempDir.resolve("utf8-data.txt").toFile()
            utf8File.writeText("한글 데이터\n영어 Data", Charsets.UTF_8)

            // When
            val provider = FileStringDataProvider(utf8File.absolutePath, "UTF-8")
            val data = provider.getData()

            // Then
            assertEquals(2, data.size)
            assertEquals("한글 데이터", data[0])
            assertEquals("영어 Data", data[1])
        }
    }
}
