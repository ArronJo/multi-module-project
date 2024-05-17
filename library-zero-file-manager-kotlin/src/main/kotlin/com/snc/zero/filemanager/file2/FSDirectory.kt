package com.snc.zero.filemanager.file2

import com.snc.zero.filemanager.file2.extensions.mkdirsOrNot
import com.snc.zero.logger.jvm.TLogging
import java.io.File
import java.io.IOException

private val logger = TLogging.logger { }

class FSDirectory {

    companion object {

        @JvmStatic
        fun create(dir: File, overwrite: Boolean = false): Boolean {
            //if (dir.isFile) {
            //    throw IOException("The source file is not directory.")
            //}
            return dir.mkdirsOrNot(overwrite)
        }

        @JvmStatic
        fun delete(dir: File, verbose:Boolean = false): Boolean {
            if (!dir.exists()) {
                return true
            }
            if (!dir.isDirectory()) {
                throw IOException("The file passed to the dir argument was not a directory.")
            }

            // 하위 폴더는 수집, 파일은 삭제 한다.
            val dirs: MutableList<File> = ArrayList()
            val lists = dir.listFiles()
            if (null != lists) {
                for (file in lists) {
                    if (file.isDirectory()) {
                        dirs.add(file)
                    } else {
                        if (verbose) {
                            logger.info { "delete file : $file" }
                        }
                        if (!FSFile.delete(file, verbose = verbose)) {
                            throw IOException("The attempt to delete this file failed.")
                        }
                    }
                }
            }
            // 하위 폴더 재귀 삭제
            for (subDir in dirs) {
                delete(subDir)
            }
            // 현재 폴더 삭제
            if (verbose) {
                logger.info { "delete directory : $dir" }
            }
            return dir.delete()
        }
    }
}