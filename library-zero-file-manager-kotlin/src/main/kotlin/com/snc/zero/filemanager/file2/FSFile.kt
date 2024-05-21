package com.snc.zero.filemanager.file2

import com.snc.zero.filemanager.file2.extensions.mkdirsOrNot
import com.snc.zero.logger.jvm.TLogging
import java.io.*
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.*

private val logger = TLogging.logger { }

class FSFile {

    companion object {

        private const val DEFAULT_BUFFER_SIZE = 8 * 1024    // 8kb
        private const val BUFFER_SIZE_1_MB = 1 * 1024 * 1024    // 1MB = 1024 * 1024 = 1048576

        @JvmStatic
        @Throws(IOException::class)
        fun create(file: File, overwrite: Boolean = false): Boolean {
            val parentFile = file.parentFile
            if (parentFile != null) {
                if (!parentFile.mkdirsOrNot()) {
                    throw IOException("Unable to create parent directory.. $parentFile")
                }
            }
            overwrite(file, overwrite)
            return file.createNewFile()
        }

        @JvmStatic
        private fun overwrite(file: File, overwrite: Boolean = false) {
            if (file.exists()) {
                if (!overwrite) {
                    throw IOException("The source file already exists. $file")
                } else if (!delete(file)) {
                    throw IOException("Tried to overwrite the destination, but failed to delete it. $file")
                }
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun delete(file: File): Boolean {
            return file.delete()
        }

        @JvmStatic
        @Throws(IOException::class)
        fun read(
            file: File,
            charset: Charset = charset("UTF-8"),
        ): ByteArray {
            val buf = CharArray(DEFAULT_BUFFER_SIZE)
            var br: BufferedReader? = null
            val sb = StringBuilder()

            var length = getLength(file)
            try {
                br = BufferedReader(InputStreamReader(FileInputStream(file), charset))
                while (length > 0) {
                    val amt = br.read(buf, 0, buf.size)
                    if (amt == -1) {
                        break //throw new IOException("ERROR : " + file + ": unexpected EOF");
                    }
                    sb.appendRange(buf, 0, amt)
                    length -= amt
                }
            } catch (e: Exception) {
                throw IOException("ERROR : $file: trouble reading", e)
            } finally {
                closeQuietly(br)
            }
            return sb.toString().toByteArray()
        }

        @JvmStatic
        @Throws(IOException::class)
        fun write(data: ByteArray, out: File, overwrite: Boolean = false): Int {
            val parentFile = out.getParentFile() ?: return -1
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    return -2
                }
            }
            create(out, overwrite)

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
            } catch (e: IOException) {
                throw IOException("ERROR : trouble writing $out")
            } finally {
                closeQuietly(os)
            }
            return offset
        }

        @JvmStatic
        @Throws(IOException::class)
        fun copy(src: File, dst: File, overwrite: Boolean = false): Boolean {
            try {
                src.copyTo(target = dst, overwrite = overwrite)
            } catch (e: IOException) {
                logger.error(e) { }
                return false
            }
            return true
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
        fun toBytes(chars: CharArray): ByteArray {
            val charBuffer = CharBuffer.wrap(chars)
            val byteBuffer = Charset.forName("UTF-8").encode(charBuffer)
            val bytes = Arrays.copyOfRange(
                byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit()
            )
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

        private fun closeQuietly(os: OutputStream?) {
            try {
                os?.flush()
            } catch (e: Exception) {
                logger.error(e) { "Exception" }
            }
            try {
                os?.close()
            } catch (e: Exception) {
                logger.error(e) { "Exception" }
            }
        }

        private fun closeQuietly(c: Closeable?) {
            try {
                c?.close()
            } catch (e: Exception) {
                logger.error(e) { "Exception" }
            }
        }
    }
}