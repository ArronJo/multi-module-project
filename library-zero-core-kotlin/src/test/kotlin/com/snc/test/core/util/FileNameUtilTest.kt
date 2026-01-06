package com.snc.test.core.util

import com.snc.zero.core.util.FileNameUtil.Companion.sanitizeFileName
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class FileNameUtilTest : BaseJUnit5Test() {

    @Nested
    inner class NullAndEmptyStringHandling {

        @Test
        fun `빈 문자열 입력시 빈 문자열 반환`() {
            val result = sanitizeFileName("")
            assertEquals("", result)
        }

        @Test
        fun `공백만 있는 문자열 입력시 빈 문자열 반환`() {
            val result = sanitizeFileName("   ")
            assertEquals("", result)
        }
    }

    @Nested
    inner class QuoteRemoval {

        @Test
        fun `앞뒤 쌍따옴표 제거`() {
            val result = sanitizeFileName("\"example.txt\"")
            assertEquals("example.txt", result)
        }

        @Test
        fun `앞뒤 작은따옴표 제거`() {
            val result = sanitizeFileName("'example.txt'")
            assertEquals("example.txt", result)
        }

        @Test
        fun `중간에 있는 따옴표도 제거`() {
            val result = sanitizeFileName("exam\"ple'file.txt")
            assertEquals("examplefile.txt", result)
        }
    }

    @Nested
    inner class SpecialCharacterHandling {

        @Test
        fun `공백을 언더스코어로 치환`() {
            val result = sanitizeFileName("my file name.txt", additional = true)
            assertEquals("my_file_name.txt", result)
        }

        @Test
        fun `쉼표를 언더스코어로 치환`() {
            val result = sanitizeFileName("file,name.txt", additional = true)
            assertEquals("file_name.txt", result)
        }

        @Test
        fun `여러 특수문자를 한번에 치환`() {
            val result = sanitizeFileName("my:file;name!.txt", additional = true)
            assertEquals("my_file_name_.txt", result)
        }
    }

    @Nested
    inner class FileExtensionHandling {

        @Test
        fun `확장자가 있는 파일명 처리`() {
            val result = sanitizeFileName("my file.txt")
            assertEquals("my file.txt", result)
        }

        @Test
        fun `확장자가 없는 파일명 처리`() {
            val result = sanitizeFileName("my file")
            assertEquals("my file", result)
        }

        @Test
        fun `여러 점이 있는 파일명에서 마지막 점을 확장자로 인식`() {
            val result = sanitizeFileName("my.backup.file.txt")
            assertEquals("my.backup.file.txt", result)
        }

        @Test
        fun `점으로 시작하는 파일명 처리`() {
            val result = sanitizeFileName(".gitignore")
            assertEquals(".gitignore", result)
        }
    }

    @Nested
    inner class ComplexScenarios {

        @Test
        fun `앞뒤 공백과 따옴표가 함께 있는 경우`() {
            val result = sanitizeFileName("  \"my file.txt\"  ")
            assertEquals("my file.txt", result)
        }

        @Test
        fun `모든 처리 규칙이 적용되는 복잡한 케이스1`() {
            val result = sanitizeFileName("  \"my___file:::(test)!!.backup.txt\"  ")
            assertEquals("my___file___(test)!!.backup.txt", result)
        }

        @Test
        fun `모든 처리 규칙이 적용되는 복잡한 케이스 2`() {
            val result = sanitizeFileName("  \"my___file:::(test)!!.backup.txt\"  ", additional = true)
            assertEquals("my___file____test___.backup.txt", result)
        }
    }

    @Nested
    inner class SpecialCharacterRemoval {

        @Test
        fun `Windows 금지 문자 제거`() {
            val result = sanitizeFileName("file<>name.txt")
            assertFalse(result.contains("<"))
            assertFalse(result.contains(">"))
        }

        @Test
        fun `슬래시 문자 제거`() {
            val result = sanitizeFileName("my/file\\name.pdf")
            assertFalse(result.contains("/"))
            assertFalse(result.contains("\\"))
        }

        @Test
        fun `파이프와 물음표 제거`() {
            val result = sanitizeFileName("file|name?.jpg")
            assertFalse(result.contains("|"))
            assertFalse(result.contains("?"))
        }

        @Test
        fun `콜론과 별표 제거`() {
            val result = sanitizeFileName("file:name*.doc")
            assertFalse(result.contains(":"))
            assertFalse(result.contains("*"))
        }
    }

    @Nested
    inner class WindowsReservedNames {

        @Test
        fun `CON 예약어 처리`() {
            val result = sanitizeFileName("CON.txt")
            assertNotEquals("CON.txt", result)
            assertTrue(result.startsWith("_"))
        }

        @Test
        fun `PRN 예약어 처리`() {
            val result = sanitizeFileName("PRN.doc")
            assertNotEquals("PRN.doc", result)
        }

        @Test
        fun `COM1 예약어 처리`() {
            val result = sanitizeFileName("COM1.txt")
            assertNotEquals("COM1.txt", result)
        }

        @Test
        fun `LPT1 예약어 처리`() {
            val result = sanitizeFileName("LPT1.pdf")
            assertNotEquals("LPT1.pdf", result)
        }

        @Test
        fun `소문자 예약어도 처리`() {
            val result = sanitizeFileName("con.txt")
            assertNotEquals("con.txt", result)
        }
    }

    @Nested
    inner class WhitespaceAndDotHandling {

        @Test
        fun `앞뒤 공백 제거`() {
            val result = sanitizeFileName("  file.txt  ")
            assertEquals("file.txt", result)
        }

        @Test
        fun `앞뒤 점 제거`() {
            val result = sanitizeFileName(".file.txt.")
            assertTrue(result.startsWith("."))
            assertFalse(result.endsWith("."))
        }

        @Test
        fun `공백과 점 동시 제거`() {
            val result = sanitizeFileName("  .file.doc.  ")
            assertEquals("file.doc", result)
        }
    }

    @Nested
    inner class LengthLimit {

        @Test
        fun `255자 초과시 잘림`() {
            val longName = "a".repeat(300) + ".txt"
            val result = sanitizeFileName(longName)
            assertTrue(result.length <= 255)
        }

        @Test
        fun `확장자 포함 길이 제한`() {
            val longName = "a".repeat(260) + ".txt"
            val result = sanitizeFileName(longName)
            assertTrue(result.endsWith(".txt"))
            assertTrue(result.length <= 255)
        }

        @Test
        fun `커스텀 길이 제한`() {
            val result = sanitizeFileName("verylongfilename.txt", maxLength = 10)
            assertTrue(result.length <= 10)
        }
    }

    @Nested
    inner class EmptyValueHandling {

        @Test
        fun `빈 문자열은 unnamed 반환`() {
            val result = sanitizeFileName("")
            assertEquals("", result)
        }

        @Test
        fun `공백만 있으면 unnamed 반환`() {
            val result = sanitizeFileName("   ")
            assertEquals("", result)
        }

        @Test
        fun `점만 있으면 unnamed 반환`() {
            val result = sanitizeFileName(".")
            assertEquals("", result)
        }

        @Test
        fun `특수문자만 있으면 대체 후 처리`() {
            val result = sanitizeFileName("<>?*")
            assertNotEquals("", result)
        }
    }

    @Nested
    inner class ReplacementCharacterOption {

        @Test
        fun `기본 대체 문자는 언더스코어`() {
            val result = sanitizeFileName("file<name.txt")
            assertTrue(result.contains("_"))
        }

        @Test
        fun `커스텀 대체 문자 사용`() {
            val result = sanitizeFileName("file<name.txt", replacement = "-")
            assertTrue(result.contains("-"))
            assertFalse(result.contains("_"))
        }
    }

    @Nested
    inner class ExtensionPreservation {

        @Test
        fun `확장자 유지`() {
            val result = sanitizeFileName("file<>name.txt")
            assertTrue(result.endsWith(".txt"))
        }

        @Test
        fun `다중 점이 있는 파일명`() {
            val result = sanitizeFileName("file.name.test.txt")
            assertTrue(result.endsWith(".txt"))
        }

        @Test
        fun `확장자 없는 파일`() {
            val result = sanitizeFileName("filename")
            assertFalse(result.contains("."))
        }
    }

    @Nested
    inner class KoreanFileName {

        @Test
        fun `한글 파일명 유지`() {
            val result = sanitizeFileName("파일명.txt")
            assertTrue(result.contains("파일명"))
        }

        @Test
        fun `한글과 특수문자 혼합`() {
            val result = sanitizeFileName("파일<>명.txt")
            assertTrue(result.contains("파일"))
            assertTrue(result.contains("명"))
            assertFalse(result.contains("<"))
        }
    }

    @Nested
    inner class IntegrationScenarios {

        @Test
        fun `복잡한 파일명 정리`() {
            val result = sanitizeFileName("  my<file>:name|test?.txt  ")
            assertFalse(result.contains("<"))
            assertFalse(result.contains(">"))
            assertFalse(result.contains(":"))
            assertFalse(result.contains("|"))
            assertFalse(result.contains("?"))
            assertTrue(result.endsWith(".txt"))
        }

        @Test
        fun `모든 규칙 적용`() {
            val result = sanitizeFileName("  .CON<>:file;!name.txt.  ")
            assertNotEquals("CON", result.substringBefore("."))
            assertFalse(result.contains("<"))
            assertTrue(result.contains(";"))
            assertTrue(result.endsWith(".txt"))
        }
    }
}
