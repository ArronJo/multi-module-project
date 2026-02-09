package com.snc.test.core.util

import com.snc.zero.core.util.DownloadFileNameUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
@DisplayName("DownloadFileNameUtil 테스트")
class DownloadFileNameUtilTest {

    @Nested
    @DisplayName("Content-Disposition 헤더에서 파일명 추출")
    inner class ContentDispositionTest {

        @Test
        fun `표준 filename 형식으로 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = "attachment; filename=\"report.pdf\"",
                mimeType = "application/pdf"
            )
            assertEquals("report.pdf", result)
        }

        @Test
        fun `UTF-8 인코딩 filename 형식으로 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = "attachment; filename*=UTF-8''%EB%B3%B4%EA%B3%A0%EC%84%9C.pdf",
                mimeType = "application/pdf"
            )
            assertEquals("보고서.pdf", result)
        }

        @Test
        fun `fileName 대소문자 구분 없이 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = "attachment; fileName=\"document.pdf\"",
                mimeType = "application/pdf"
            )
            assertEquals("document.pdf", result)
        }

        @Test
        fun `쌍따옴표 없는 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = "attachment; filename=simple.pdf",
                mimeType = "application/pdf"
            )
            assertEquals("simple.pdf", result)
        }
    }

    @Nested
    @DisplayName("URL 쿼리 파라미터에서 파일명 추출")
    inner class QueryParameterTest {

        @Test
        fun `clientFileName 파라미터로 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download?clientFileName=test.pdf",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertEquals("test.pdf", result)
        }

        @Test
        fun `filename 파라미터로 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download?filename=sample.pdf",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertEquals("sample.pdf", result)
        }

        @Test
        fun `URL 인코딩된 한글 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download?filename=%ED%85%8C%EC%8A%A4%ED%8A%B8.pdf",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertEquals("테스트.pdf", result)
        }

        @Test
        fun `clientFileName 우선순위가 filename보다 높음`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download?clientFileName=first.pdf&filename=second.pdf",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertEquals("first.pdf", result)
        }
    }

    @Nested
    @DisplayName("URL 경로에서 파일명 추출")
    inner class PathSegmentTest {

        @Test
        fun `경로의 마지막 세그먼트에서 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/files/data/report.pdf",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertEquals("report.pdf", result)
        }

        @Test
        fun `확장자가 없는 경로는 파일명으로 인식하지 않음`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/api/download/12345",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertTrue(result?.startsWith("download_") ?: false)
        }

        @Test
        fun `확장자가 있는 경로만 파일명으로 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/storage/file.zip",
                contentDisposition = null,
                mimeType = "application/zip"
            )
            assertEquals("file.zip", result)
        }
    }

    @Nested
    @DisplayName("우선순위 테스트")
    inner class PriorityTest {

        @Test
        fun `Content-Disposition이 쿼리 파라미터보다 우선순위가 높음`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download?filename=query.pdf",
                contentDisposition = "attachment; filename=\"header.pdf\"",
                mimeType = "application/pdf"
            )
            assertEquals("header.pdf", result)
        }

        @Test
        fun `쿼리 파라미터가 URL 경로보다 우선순위가 높음`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/files/path.pdf?filename=query.pdf",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertEquals("query.pdf", result)
        }

        @Test
        fun `모든 우선순위를 고려한 파일명 추출`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/files/path.pdf?filename=query.pdf",
                contentDisposition = "attachment; filename=\"disposition.pdf\"",
                mimeType = "application/pdf"
            )
            assertEquals("disposition.pdf", result)
        }
    }

    @Nested
    @DisplayName("기본 파일명 생성")
    inner class DefaultFilenameTest {

        @Test
        fun `파일명이 없으면 기본 파일명 생성`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertNotNull(result)
            assertTrue(result?.startsWith("download_") ?: false)
            assertTrue(result?.endsWith(".pdf") ?: false)
        }

        @Test
        fun `MIME 타입이 image-jpeg일 때 jpg 확장자 생성`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = null,
                mimeType = "image/jpeg"
            )
            assertTrue(result?.endsWith(".jpg") ?: false)
        }

        @Test
        fun `MIME 타입이 unknown일 때 bin 확장자 생성`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = null,
                mimeType = "unknown"
            )
            assertTrue(result?.endsWith(".bin") ?: false)
        }

        @Test
        fun `MIME 타입이 null일 때 bin 확장자 생성`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = null,
                mimeType = null
            )
            assertTrue(result?.endsWith(".bin") ?: false)
        }

        @Test
        fun `정의되지 않은 MIME 타입은 bin 확장자로 사용`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = null,
                mimeType = "application/custom"
            )
            assertTrue(result?.endsWith(".bin") ?: false)
        }

        @Test
        fun `기본 파일명에 타임스탬프 포함 확인`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "https://example.com/download",
                contentDisposition = null,
                mimeType = "text/plain"
            )
            assertNotNull(result)
            val pattern = "download_\\d{17}\\.txt".toRegex()
            assertTrue(pattern.matches(result ?: ""))
        }
    }

    @Nested
    @DisplayName("getQueryParameter 테스트")
    inner class GetQueryParameterTest {

        @Test
        fun `쿼리 파라미터 정상 추출`() {
            val uri = java.net.URI("https://example.com?key=value")
            val result = DownloadFileNameUtil.getQueryParameter(uri, "key")
            assertEquals("value", result)
        }

        @Test
        fun `존재하지 않는 파라미터는 null 반환`() {
            val uri = java.net.URI("https://example.com?key=value")
            val result = DownloadFileNameUtil.getQueryParameter(uri, "notexist")
            assertNull(result)
        }

        @Test
        fun `URL 인코딩된 값 디코딩`() {
            val uri = java.net.URI("https://example.com?name=%ED%85%8C%EC%8A%A4%ED%8A%B8")
            val result = DownloadFileNameUtil.getQueryParameter(uri, "name")
            assertEquals("테스트", result)
        }

        @Test
        fun `쿼리 문자열이 없으면 null 반환`() {
            val uri = java.net.URI("https://example.com")
            val result = DownloadFileNameUtil.getQueryParameter(uri, "key")
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("getPathSegments 테스트")
    inner class GetPathSegmentsTest {

        @Test
        fun `경로 세그먼트 정상 추출`() {
            val uri = java.net.URI("https://example.com/path/to/file.pdf")
            val result = DownloadFileNameUtil.getPathSegments(uri)
            assertEquals(listOf("path", "to", "file.pdf"), result)
        }

        @Test
        fun `빈 경로는 빈 리스트 반환`() {
            val uri = java.net.URI("https://example.com")
            val result = DownloadFileNameUtil.getPathSegments(uri)
            assertTrue(result.isEmpty())
        }

        @Test
        fun `루트 경로는 빈 리스트 반환`() {
            val uri = java.net.URI("https://example.com/")
            val result = DownloadFileNameUtil.getPathSegments(uri)
            assertTrue(result.isEmpty())
        }

        @Test
        fun `단일 세그먼트 추출`() {
            val uri = java.net.URI("https://example.com/file.pdf")
            val result = DownloadFileNameUtil.getPathSegments(uri)
            assertEquals(listOf("file.pdf"), result)
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    inner class ExceptionTest {

        @Test
        fun `잘못된 URL은 null 반환`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "not a valid url",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertNull(result)
        }

        @Test
        fun `빈 URL은 null 반환`() {
            val result = DownloadFileNameUtil.getFilenameFromDownloadContent(
                urlString = "",
                contentDisposition = null,
                mimeType = "application/pdf"
            )
            assertNull(result)
        }
    }
}
