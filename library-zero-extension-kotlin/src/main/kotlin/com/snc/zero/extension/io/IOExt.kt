package com.snc.zero.extension.io

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

fun InputStream.closeQuietly() {
    try {
        this.close()
    } catch (_: Exception) {
        //
    }
}

fun OutputStream.closeQuietly() {
    try {
        this.flush()
    } catch (_: Exception) {
        //
    }
    try {
        this.close()
    } catch (_: Exception) {
        //
    }
}

fun Closeable.closeQuietly() {
    try {
        this.close()
    } catch (_: Exception) {
        //
    }
}
