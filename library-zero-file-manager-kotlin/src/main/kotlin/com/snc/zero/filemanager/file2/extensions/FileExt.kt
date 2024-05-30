package com.snc.zero.filemanager.file2.extensions

import java.io.File

fun String.toFile(parent: String = ""): File {
    return File(parent, this)
}