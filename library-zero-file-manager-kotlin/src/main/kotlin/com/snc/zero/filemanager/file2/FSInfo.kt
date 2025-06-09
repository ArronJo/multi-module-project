package com.snc.zero.filemanager.file2

import java.text.DecimalFormat
import kotlin.math.abs

/**
 * 파일 정보 접근
 *
 * @author mcharima5@gmail.com
 * @since 2023
 */
class FSInfo private constructor() {

    companion object {

        fun getReadableFileSize(dataSize: Long, displayFloat: Boolean = false): String {
            if (dataSize <= 1) {
                return "$dataSize byte"
            }

            var count = 0
            var size: Double = dataSize.toDouble()
            while (true) {
                val tmp = size / 1024.0
                if (abs(tmp).toInt() <= 0) {
                    break
                }
                size = tmp
                count++
            }

            val formatSize = if (displayFloat) {
                DecimalFormat("#,###,##0.00").format(size)
            } else {
                DecimalFormat("#,###,###").format(size)
            }
            val unit = when (count) {
                0 -> {
                    "bytes"
                }

                1 -> {
                    "KB"
                }

                2 -> {
                    "MB"
                }

                3 -> {
                    "GB"
                }

                4 -> {
                    "TB"
                }

                5 -> {
                    "PB"
                }
                // 큰 용량: 6 -> { "EB" } // Long.MAX_VALUE
                // 큰 용량: 7 -> { "ZB" }
                // 큰 용량: 8 -> { "YB" }
                else -> {
                    "BigSize"
                }
            }
            return "$formatSize $unit"
        }
    }
}
