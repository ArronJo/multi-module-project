package com.snc.zero.extension.format

import java.text.DecimalFormat
import kotlin.math.abs

fun Long.formatFileSize(displayFloat: Boolean = false): String {
    if (this <= 1) {
        return "$this byte"
    }

    var count = 0
    var size: Double = this.toDouble()
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
        else -> { "BigSize" }
    }
    return "$formatSize $unit"
}
