package com.snc.test.pdfbox.editor

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.pdfbox.editor.PDFEditor
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.io.File

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PDFEditorTest : BaseJUnit5Test() {

    //private val inputPdf = "./docs/input/CM090101.pdf"
    private val inputPdf = "./docs/input/CMS00017.pdf"
    private val outputPdf = "./build/output/output.pdf"

    private val textToFind = arrayOf(
        "이를 위반하는 경우 보험금 지급이 제한될 수 있습니다",
        "보험금 지급이 제한될 수 있습니다",
        "이 보험에는 ’보험금이 지급되지 않는 기간’을 의미하는 [면책기간]이 적용되는 담보가 포함되어 있습니다.",
        "보험료 감액, 정산금액 환급",
        "진단서, 진단사실 확인서류(검사결과지 등)",
        "영업일 이내",
        "제 2-12의",
        "보장하는"
    )

    @Test
    fun `object 좌표 추출`() {
        val locations = PDFEditor.extractTextLocations(inputPdf, textToFind)
        locations.forEach { location ->
            logger.debug { "Page: ${location.page}, X: ${location.x}, Y: ${location.y}, Width: ${location.width}, Height: ${location.height}" }
        }
    }

    @Test
    fun `텍스트가 포함되어 있는 문구 highlight 처리`() {
        // case 1
        //val inputPdf = "./docs/input/pldi-09.pdf"
        //val textToFind = arrayOf("graph", "also")

        // case 2
        val inputPdf = "./docs/input/phr-01.pdf"
        val textToFind = arrayOf("Ⅲ. 시사점", "지난해 비의료 건강관리서비스 가이드라인 제")

        if (!PDFEditor.extractHighlightPages(inputPdf, outputPdf, textToFind)) {
            logger.debug { "검색된 결과 없음" }
        }
    }

    @Test
    fun `텍스트가 포함되어 있는 문구 highlight 처리 - location empty`() {
        // case 1
        //val inputPdf = "./docs/input/pldi-09.pdf"
        //val textToFind = arrayOf("graph", "also")

        // case 2
        val inputPdf = "./docs/input/phr-01.pdf"
        val textToFind = arrayOf("ㅋㅋㅋㅋㅋㅋ")

        if (!PDFEditor.extractHighlightPages(inputPdf, outputPdf, textToFind)) {
            logger.debug { "검색된 결과 없음" }
        }
    }

    @Test
    fun `텍스트가 포함되어 있는 페이지 추출`() {
        //println("pwd: ${File(".").absolutePath}")
        PDFEditor.extractTextInPDF(inputPdf, outputPdf, textToFind)
    }

    @Test
    fun `특정 페이지 추출 1`() {
        val pagesToExtract = listOf(7, 10, 11, 17, 18) // 페이지를 추출 (1-based index)
        PDFEditor.extractPages(inputPdf, outputPdf, pagesToExtract)
    }

    @Test
    fun `특정 페이지 추출 2`() {
        val inputPdf = "./docs/input/CMS00017.pdf"
        val pagesToExtract = listOf(41, 44, 45, 46, 48, 57) // 페이지를 추출 (1-based index)
        PDFEditor.extractPages(inputPdf, outputPdf, pagesToExtract)
    }

    @Test
    fun `test mkdirs 1`() {
        // Case 1: 디렉토리가 없는 경우
        val newDirPath = "test/new/directory/file.txt"
        PDFEditor.mkdirs(newDirPath)
        println("Case 1: 새 디렉토리 생성 - ${File(newDirPath).parentFile.exists()}")
        File(newDirPath).parentFile.apply {
            mkdirs()
            File(this, "test1.txt").createNewFile()
            File(this, "test2.txt").createNewFile()
        }

        PDFEditor.mkdirs(newDirPath)
        println("Case 1: 새 디렉토리 한번 더 생성 - ${File(newDirPath).parentFile.exists()}")

        // Case 2: 디렉토리가 있고 내부가 비어있는 경우
        val emptyDirPath = "test/empty/directory/file.txt"
        File(emptyDirPath).parentFile.mkdirs()
        PDFEditor.mkdirs(emptyDirPath)
        println("Case 2: 빈 디렉토리 처리 완료")

        // Case 3: 디렉토리가 있고 삭제 가능한 파일이 있는 경우
        val dirWithFilesPath = "test/with/files/file.txt"
        val dirWithFiles = File(dirWithFilesPath).parentFile.apply {
            mkdirs()
            File(this, "test1.txt").createNewFile()
            File(this, "test2.txt").createNewFile()
        }
        PDFEditor.mkdirs(dirWithFilesPath)
        println("Case 3: 파일이 있는 디렉토리 처리 - 남은 파일 수: ${dirWithFiles.listFiles()?.size ?: 0}")

        // Case 4: 디렉토리가 있고 삭제 불가능한 파일이 있는 경우
        val dirWithReadOnlyPath = "test/with/readonly/file.txt"
        val dirWithReadOnly = File(dirWithReadOnlyPath).parentFile.apply {
            mkdirs()
            File(this, "readonly.txt").apply {
                createNewFile()
                setWritable(false)
            }
        }
        PDFEditor.mkdirs(dirWithReadOnlyPath)
        println("Case 4: 읽기 전용 파일이 있는 디렉토리 처리 - 남은 파일 수: ${dirWithReadOnly.listFiles()?.size ?: 0}")

        // 테스트 후 정리
        File("test").deleteRecursively()
    }

    @Test
    fun `test mkdirs 2`() {
        // Case 1: 디렉토리가 없는 경우
        val newDirPath = "test/new/directory/file1.txt"
        PDFEditor.mkdirs(newDirPath)
        File(newDirPath).parentFile.apply {
            mkdirs()
            File(this, "test1.txt").createNewFile()
            File(this, "test2.txt").createNewFile()
        }
        PDFEditor.mkdirs(newDirPath)
    }
}
