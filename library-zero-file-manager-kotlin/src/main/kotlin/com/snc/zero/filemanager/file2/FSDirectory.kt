package com.snc.zero.filemanager.file2

import java.io.File
import java.io.IOException

/**
 * 파일 매니저 (다시 만들기) - 미완성
 * 디렉토리, 파일로 관리 객체 분리
 *
 * @author mcharima5@gmail.com
 * @since 2023
 */
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

            // 하위 폴더는 수집, 파일은 삭제 한다.
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
            // 하위 폴더 재귀 삭제
            for (subDir in dirs) {
                delete(subDir)
            }
            return dir.delete()
        }

        fun deleteRecursively(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (!file.deleteRecursively()) {
                    println("삭제 실패: ${file.absolutePath}")
                }
            }
        }
    }
}
