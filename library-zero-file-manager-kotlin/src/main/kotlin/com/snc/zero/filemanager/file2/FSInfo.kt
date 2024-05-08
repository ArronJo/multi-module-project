package com.snc.zero.filemanager.file2

import com.snc.zero.logger.jvm.TLogging
import java.text.DecimalFormat
import kotlin.math.abs

private val logger = TLogging.logger { }

class FSInfo {

    companion object {

        @JvmStatic
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
                0 -> { "bytes" }
                1 -> { "KB" }
                2 -> { "MB" }
                3 -> { "GB" }
                4 -> { "TB" }
                5 -> { "PB" }
                6 -> { "EB" }
                7 -> { "ZB" }
                8 -> { "YB" }
                else -> { "BigSize" }
            }
            return "$formatSize $unit"
        }
    }
}