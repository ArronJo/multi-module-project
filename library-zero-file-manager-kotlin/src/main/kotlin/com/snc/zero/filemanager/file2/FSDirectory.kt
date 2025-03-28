package com.snc.zero.filemanager.file2

import java.io.File
import java.io.IOException

class FSDirectory private constructor() {

    companion object {

        fun create(dir: File, overwrite: Boolean = false): Boolean {
            if (dir.exists() && !overwrite) {
                return false
            }
            return dir.mkdirs()
        }

        fun delete(dir: File): Boolean {
            if (!dir.exists()) {
                return false
            }

            val dirs: MutableList<File> = ArrayList()
            dir.listFiles()?.let {
                for (file in it) {
                    if (file.isDirectory()) {
                        dirs.add(file)
                    } else if (!FSFile.delete(file)) {
                        throw IOException("The attempt to delete this file failed.")
                    }
                }
            }
            for (subDir in dirs) {
                delete(subDir)
            }
            return dir.delete()
        }
    }
}
