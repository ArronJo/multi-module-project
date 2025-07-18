package com.snc.zero.filemanager.file2

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.Arrays

/**
 * 파일 매니저 (다시 만들기) - 미완성
 * 디렉토리, 파일로 관리 객체 분리
 *
 * @author mcharima5@gmail.com
 * @since 2023
 */
class FSFile private constructor() {

    companion object {

        private const val DEFAULT_BUFFER_SIZE = 8 * 1024 // 8kb
        private const val BUFFER_SIZE_1_MB = 1 * 1024 * 1024 // 1MB = 1024 * 1024 = 1048576

        @Throws(IOException::class)
        fun create(file: File, overwrite: Boolean = false): Boolean {
            file.parentFile?.let {
                if (!it.exists() && !it.mkdirs()) {
                    throw IOException("Unable to create parent directory. $it")
                }
            }
            overwrite(file, overwrite)
            return file.createNewFile()
        }

        @Throws(IOException::class)
        private fun overwrite(file: File, overwrite: Boolean) {
            if (file.exists()) {
                if (!overwrite) {
                    throw IOException("The source file already exists. $file")
                }
                if (!delete(file)) {
                    throw IOException("Tried to overwrite the destination, but failed to delete it. $file")
                }
            }
        }

        @Throws(IOException::class)
        fun delete(file: File, ignore: Boolean = false): Boolean {
            if (!file.exists()) {
                if (ignore) {
                    return false
                }
                throw IOException("Target file does not exist.")
            }
            return file.delete()
        }

        /**
         * (보안취약점) TOCTOU(Time-of-Check-Time-of-Use) 경쟁 조건은 파일 시스템에서 발생하는 중요한 보안 취약점입니다.
         * TOCTOU 경쟁 조건이란?
         * TOCTOU는 파일의 상태를 **확인하는 시점(Time-of-Check)**과 실제로 사용하는 시점(Time-of-Use) 사이에
         * 시간 차이가 있을 때 발생하는 문제입니다. 이 짧은 시간 동안 다른 프로세스가 파일을 변경할 수 있어 예상치 못한 결과가 발생할 수 있습니다.
         *
         * fun safeRead(filename: String): String {
         *   try {
         *     // 파일 존재 확인과 읽기를 동시에 수행
         *     val content = Files.readString(Paths.get(filename))
         *     // 파일 처리 로직
         *     return content
         *   } catch (_: NoSuchFileException) {
         *     // 파일이 없는 경우 처리
         *     return ""
         *   }
         * }
         *
         * fun safeReadWithDescriptor(filename: String): String {
         *   try {
         *     Files.newInputStream(Paths.get(filename)).use { inputStream ->
         *       // 파일 디스크립터가 고정되어 안전
         *       val content = inputStream.readBytes()
         *       // 처리 로직
         *       return String(content, Charsets.UTF_8)
         *     }
         *   } catch (_: IOException) {
         *     // 에러 처리
         *     return ""
         *   }
         * }
         */
        @Throws(IOException::class)
        fun read(
            file: File,
            charset: String = "UTF-8"
        ): ByteArray {
            // TOCTOU 대응: 파일 존재 확인과 스트림 생성을 동시에 수행
            var fileInputStream: FileInputStream? = null
            var br: BufferedReader? = null

            try {
                fileInputStream = FileInputStream(file)
                br = BufferedReader(InputStreamReader(fileInputStream, charset(charset)))

                val sb = StringBuilder()
                val buf = CharArray(DEFAULT_BUFFER_SIZE)
                var amt: Int

                while (br.read(buf, 0, buf.size).also { amt = it } != -1) {
                    sb.appendRange(buf, 0, amt)
                }

                return sb.toString().toByteArray(charset(charset))

            } catch (e: FileNotFoundException) {
                throw IOException("파일을 찾을 수 없습니다: ${file.absolutePath}", e)
            } catch (e: SecurityException) {
                throw IOException("파일 접근 권한이 없습니다: ${file.absolutePath}", e)
            } finally {
                closeQuietly(br)
                closeQuietly(fileInputStream)
            }
        }

        // 더 안전한 대안 - NIO 사용
        // 가장 안전한 방법은 readWithNIO() 함수를 사용하는 것입니다.
        @Throws(IOException::class)
        fun readWithNIO(file: File): ByteArray {
            return try {
                // NIO를 사용한 원자적 파일 읽기
                Files.readAllBytes(file.toPath())
            } catch (e: NoSuchFileException) {
                throw IOException("파일을 찾을 수 없습니다: ${file.absolutePath}", e)
            } catch (e: AccessDeniedException) {
                throw IOException("파일 접근 권한이 없습니다: ${file.absolutePath}", e)
            }
        }

        @Throws(IOException::class)
        fun write(out: File, data: ByteArray): Int {
            var os: FileOutputStream? = null
            var offset = 0
            try {
                os = FileOutputStream(out)
                val bufSize = if (data.size > BUFFER_SIZE_1_MB) { // 1 MB 이상일 경우
                    BUFFER_SIZE_1_MB
                } else {
                    DEFAULT_BUFFER_SIZE
                }
                val buffer = ByteArray(bufSize)
                val bis = ByteArrayInputStream(data)
                var len: Int
                while (bis.read(buffer, 0, buffer.size).also { len = it } >= 0) {
                    os.write(buffer, 0, len)
                    offset += len
                }

            } finally {
                closeQuietly(os)
            }
            return offset
        }

        @Throws(IOException::class)
        fun copy(src: File, dst: File, overwrite: Boolean = false) {
            src.copyTo(target = dst, overwrite = overwrite)
        }

        @Throws(IOException::class)
        fun copy(input: InputStream, output: OutputStream): Long {
            var bytesCopied: Long = 0
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = input.read(buffer)
            while (bytes >= 0) {
                output.write(buffer, 0, bytes)
                bytesCopied += bytes.toLong()
                bytes = input.read(buffer)
            }
            return bytesCopied
        }

        fun toBytes(chars: CharArray, charsetName: String = "UTF-8"): ByteArray {
            val charBuffer = CharBuffer.wrap(chars)
            val byteBuffer = Charset.forName(charsetName).encode(charBuffer)
            // 이전 코드: val bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit())
            val bytes = byteBuffer.array().copyOfRange(byteBuffer.position(), byteBuffer.limit())
            Arrays.fill(byteBuffer.array(), 0.toByte()) // clear sensitive data
            return bytes
        }

        fun getLength(file: File): Int {
            val longLength = file.length()
            val length = longLength.toInt()
            if (length.toLong() != longLength) {
                throw IOException("ERROR : $file: file too long")
            }
            return length
        }

        fun closeQuietly(os: OutputStream?) {
            try {
                os?.flush()
            } catch (_: IOException) {
                //
            }
            try {
                os?.close()
            } catch (_: IOException) {
                //
            }
        }

        fun closeQuietly(c: Closeable?) {
            try {
                c?.close()
            } catch (_: IOException) {
                //
            }
        }
    }
}
