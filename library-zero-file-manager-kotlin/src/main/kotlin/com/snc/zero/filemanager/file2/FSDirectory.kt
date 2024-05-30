package com.snc.zero.filemanager.file2

import java.io.File
import java.io.IOException

class FSDirectory {

    companion object {

        @JvmStatic
        fun create(dir: File, overwrite: Boolean = false): Boolean {
            if (dir.exists() && !overwrite) {
                return false
            }
            return dir.mkdirs()
        }

        @JvmStatic
        fun delete(dir: File): Boolean {
            if (!dir.exists()) {
                return true
            }
            if (!dir.isDirectory()) {
                throw IOException("The file passed to the dir argument was not a directory.")
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