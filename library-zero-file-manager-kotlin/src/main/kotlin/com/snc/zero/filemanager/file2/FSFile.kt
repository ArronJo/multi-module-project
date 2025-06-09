package com.snc.zero.filemanager.file2

import java.io.*
import java.nio.CharBuffer
import java.nio.charset.Charset
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

        @JvmStatic
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

        @JvmStatic
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

        @JvmStatic
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

        @JvmStatic
        @Throws(IOException::class)
        fun read(
            file: File,
            charset: String = "UTF-8"
        ): ByteArray {
            val buf = CharArray(DEFAULT_BUFFER_SIZE)
            var br: BufferedReader? = null
            val sb = StringBuilder()

            var length = file.length()
            try {
                br = BufferedReader(InputStreamReader(FileInputStream(file), charset(charset)))
                while (length > 0) {
                    val amt = br.read(buf, 0, buf.size)
                    sb.appendRange(buf, 0, amt)
                    length -= amt
                }

            } finally {
                closeQuietly(br)
            }
            return sb.toString().toByteArray()
        }

        @JvmStatic
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

        @JvmStatic
        @Throws(IOException::class)
        fun copy(src: File, dst: File, overwrite: Boolean = false) {
            src.copyTo(target = dst, overwrite = overwrite)
        }

        @JvmStatic
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

        @JvmStatic
        fun toBytes(chars: CharArray, charsetName: String = "UTF-8"): ByteArray {
            val charBuffer = CharBuffer.wrap(chars)
            val byteBuffer = Charset.forName(charsetName).encode(charBuffer)
            // 이전 코드: val bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit())
            val bytes = byteBuffer.array().copyOfRange(byteBuffer.position(), byteBuffer.limit())
            Arrays.fill(byteBuffer.array(), 0.toByte()) // clear sensitive data
            return bytes
        }

        @JvmStatic
        fun getLength(file: File): Int {
            val longLength = file.length()
            val length = longLength.toInt()
            if (length.toLong() != longLength) {
                throw IOException("ERROR : $file: file too long")
            }
            return length
        }

        @JvmStatic
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

        @JvmStatic
        fun closeQuietly(c: Closeable?) {
            try {
                c?.close()
            } catch (_: IOException) {
                //
            }
        }
    }
}
