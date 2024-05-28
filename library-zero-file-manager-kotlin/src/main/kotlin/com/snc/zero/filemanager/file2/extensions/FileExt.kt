package com.snc.zero.filemanager.file2.extensions

import java.io.File
import java.io.IOException

fun String.toFile(parent: String = ""): File {
    return File(parent, this)
}

fun File.mkdirsOrNot(overwrite: Boolean = false): Boolean {
    if (this.exists()) {
        if (overwrite) {
            if (!this.delete()) {
                throw IOException("The source file can't delete.")
            }
        } else {
            return true
        }
    }
    return this.mkdirs()
}