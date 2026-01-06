package com.snc.zero.core.util

import java.net.URI
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class DownloadFileNameUtil {
    companion object {

        /**
         * 다운로드 컨텐츠 파일이름 가져오기
         * @param urlString - url 경로
         * @param contentDisposition - 컨텐츠 정보
         * @return - 파일이름
         */
        fun getFilenameFromDownloadContent(
            urlString: String,
            contentDisposition: String?,
            mimeType: String?
        ): String? {
            if (urlString.isBlank()) {
                return null
            }

            try {
                val uri = URI(urlString)
                var fileName: String? = null

                // 1순위: Content-Disposition 헤더에서 파일명 추출
                if (contentDisposition?.isNotEmpty() == true) {
                    fileName = extractFilenameFromContentDisposition(contentDisposition)
                }

                // 2순위: URL 쿼리 파라미터에서 파일명 추출
                if (fileName == null) {
                    fileName = getQueryParameter(uri, "clientFileName")
                    if (fileName == null) {
                        fileName = getQueryParameter(uri, "filename")
                    }
                }

                // 3순위: URL 경로에서 파일명 추출
                if (fileName == null) {
                    val pathSegments: List<String> = getPathSegments(uri)
                    if (!pathSegments.isEmpty()) {
                        val lastSegment = pathSegments.get(pathSegments.size - 1)
                        // 경로가 파일명처럼 보이는지 확인 (확장자가 있는지)
                        if (lastSegment.contains(".")) {
                            fileName = lastSegment
                        }
                    }
                }

                // 4. 파일명이 없으면 기본 파일명 생성
                if (fileName.isNullOrEmpty()) {
                    fileName = generateDefaultFilename(mimeType)
                }

                // 앞뒤 쌍따옴표 제거
                fileName = FileNameUtil.sanitizeFileName(fileName)

                // URL 디코딩
                return URLDecoder.decode(fileName, "UTF-8")
            } catch (e: Exception) {
                println("에러: $e")
            }
            return null
        }

        private fun extractFilenameFromContentDisposition(contentDisposition: String?): String? {
            // RFC 6266 형식: filename="example.pdf" 또는 filename*=UTF-8''example.pdf
            val patterns: Array<Pattern> = arrayOf<Pattern>(
                Pattern.compile("filename\\*=UTF-8''([^;]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("filename=\"?([^\";]+)\"?", Pattern.CASE_INSENSITIVE),
                Pattern.compile("fileName=\"?([^\";]+)\"?", Pattern.CASE_INSENSITIVE)
            )

            contentDisposition?.let {
                for (pattern in patterns) {
                    val matcher: Matcher = pattern.matcher(contentDisposition)
                    if (matcher.find()) {
                        return matcher.group(1).trim()
                    }
                }
            }

            return null
        }

        private fun generateDefaultFilename(mimeType: String?): String {
            val sdf = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
            val timestamp = sdf.format(Date())
            val extension = getExtensionFromMimeType(mimeType)
            return "download_$timestamp$extension"
        }

        private fun getExtensionFromMimeType(mimeType: String?): String {
            if (mimeType.isNullOrEmpty() || mimeType.equals("unknown", ignoreCase = true)) {
                return ".bin"
            }

            // MIME 타입에서 확장자 매핑
            when (mimeType.lowercase(Locale.getDefault())) {
                "image/jpeg", "image/jpg" -> return ".jpg"
                "image/png" -> return ".png"
                "image/gif" -> return ".gif"
                "application/pdf" -> return ".pdf"
                "text/plain" -> return ".txt"
                "application/zip" -> return ".zip"
                "audio/mp3" -> return ".mp3"
                "video/mp4" -> return ".mp4"
                else -> {
                    // MIME 타입에서 확장자 추출 시도 (예: image/png -> .png)
                    val slashIndex = mimeType.indexOf('/')
                    if (slashIndex != -1 && slashIndex < mimeType.length - 1) {
                        return "." + mimeType.substring(slashIndex + 1)
                    }
                    return ".bin"
                }
            }
        }

        fun getQueryParameter(uri: URI, paramName: String): String? {
            val query = uri.query ?: return null

            return query.split("&")
                .map { it.split("=", limit = 2) }
                .firstOrNull { it.size == 2 && it[0] == paramName }
                ?.get(1)
                ?.let { URLDecoder.decode(it, "UTF-8") }
        }

        // 경로 세그먼트 추출
        fun getPathSegments(uri: URI): List<String> {
            return uri.path?.split("/")?.filter { it.isNotEmpty() } ?: emptyList()
        }
    }
}
