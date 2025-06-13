package com.snc.zero.core.io

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

/**
 * IO 유틸리티
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class IOUtil private constructor() {

    companion object {

        fun closeQuietly(`is`: InputStream?) {
            try {
                `is`?.close()
            } catch (_: Exception) {
                //
            }
        }

        fun closeQuietly(os: OutputStream?) {
            try {
                os?.flush()
            } catch (_: Exception) {
                //
            }
            try {
                os?.close()
            } catch (_: Exception) {
                //
            }
        }

        fun closeQuietly(c: Closeable?) {
            try {
                c?.close()
            } catch (_: Exception) {
                //
            }
        }

        fun gc() {
            try {
                System.gc()
            } catch (_: Exception) {
                //
            }
        }
    }
}
