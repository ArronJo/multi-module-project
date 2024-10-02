package org.jlleitschuh.sandbox

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files

/**
 * 취약점: https://security.snyk.io/vuln/SNYK-JAVA-ORGJETBRAINSKOTLIN-2393744
 *
 * INTRODUCED: 4 FEB 2022,  CVE-2020-29582,  CWE-378
 *
 * 이 패키지의 영향을 받는 버전은 정보 노출에 취약합니다.
 * createTempDir 또는 createTempFile을 사용하고 민감한 정보를 이러한 위치 중 하나에 배치하는 Kotlin 애플리케이션은
 * 이 시스템의 다른 사용자에게 읽기 전용 방식으로 이 정보를 유출하게 됩니다.
 *
 * 참고: 버전 1.4.21부터 취약한 기능은 더 이상 사용되지 않는 것으로 표시되었습니다.
 *      아직 사용할 수 있기 때문에 이 권고는 "수정되지 않음"으로 유지됩니다.
 */
class KotlinTempDirectoryPermissionCheck {
    @Test
    fun `kotlin check default directory permissions`() {
        val dir = kotlin.io.path.createTempDirectory(prefix = "tmp").toFile() // public inline fun createTempDirectory(prefix: String? = null, vararg attributes: FileAttribute<*>)
        //val dir = createTempDir()   // public fun createTempDir(prefix: String = "tmp", suffix: String? = null, directory: File? = null): File
        println("dir: $dir")
        runLS(dir.parentFile, dir) // Prints drwxr-xr-x
    }

    @Test
    fun `Files check default directory permissions`() {
        val dir = Files.createTempDirectory("random-directory")
        println("dir: $dir")
        runLS(dir.toFile().parentFile, dir.toFile()) // Prints drwx------
    }

    @Test
    fun `kotlin check default file permissions`() {
        val file = kotlin.io.path.createTempFile(prefix = "tmp").toFile() // public inline fun createTempFile(prefix: String? = null, suffix: String? = null, vararg attributes: FileAttribute<*>)
        //val file = createTempFile() // public fun createTempFile(prefix: String = "tmp", suffix: String? = null, directory: File? = null)
        println("file: $file")
        runLS(file.parentFile, file) // (kotlin) Prints -rw-------  (java) Prints -rw-r--r--
    }

    @Test
    fun `Files check default file permissions`() {
        val file = Files.createTempFile("random-file", ".txt")
        println("file: $file")
        runLS(file.toFile().parentFile, file.toFile()) // Prints -rw-------
    }

    private fun runLS(file: File, lookingFor: File) {
        val processBuilder = ProcessBuilder()
        processBuilder.command("ls", "-l", file.absolutePath)
        try {
            val process = processBuilder.start()
            val output = StringBuilder()
            val reader = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            reader.lines().forEach { line ->
                if (line.contains("total")) {
                    output.append(line).append('\n')
                }
                if (line.contains(lookingFor.name)) {
                    output.append(line).append('\n')
                }
            }
            val exitVal = process.waitFor()
            if (exitVal == 0) {
                println("Success!")
                println(output)
            } else {
                //abnormal...
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}