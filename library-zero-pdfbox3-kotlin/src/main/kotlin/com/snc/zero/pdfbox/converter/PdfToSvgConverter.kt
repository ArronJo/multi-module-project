package com.snc.zero.pdfbox.converter

import org.apache.pdfbox.Loader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.writeText

class PdfToSvgConverter {
    data class OutputFolders(
        val pdfDir: Path,
        val htmlDir: Path,
        val svgDir: Path
    )

    companion object {
        private var htmlTemplate = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>PDF to SVG Viewer</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 20px;
                        background-color: #f0f0f0;
                    }
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                    }
                    .header {
                        background-color: #fff;
                        padding: 20px;
                        border-radius: 8px;
                        margin-bottom: 20px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .navigation {
                        display: flex;
                        gap: 10px;
                        margin-bottom: 20px;
                    }
                    .page-select {
                        padding: 8px;
                        font-size: 16px;
                        border-radius: 4px;
                    }
                    .svg-container {
                        background-color: #fff;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        margin-bottom: 20px;
                    }
                    .svg-frame {
                        width: 100%;
                        height: 800px;
                        border: none;
                    }
                    button {
                        padding: 8px 16px;
                        font-size: 16px;
                        border: none;
                        border-radius: 4px;
                        background-color: #007bff;
                        color: white;
                        cursor: pointer;
                    }
                    button:hover {
                        background-color: #0056b3;
                    }
                    button:disabled {
                        background-color: #cccccc;
                        cursor: not-allowed;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>PDF to SVG Viewer</h1>
                        <div class="navigation">
                            <button id="prevBtn" onclick="prevPage()">이전</button>
                            <select id="pageSelect" class="page-select" onchange="goToPage()">
                                %s
                            </select>
                            <button id="nextBtn" onclick="nextPage()">다음</button>
                        </div>
                    </div>
                    <div class="svg-container">
                        <iframe id="svgFrame" class="svg-frame"></iframe>
                    </div>
                </div>
                <script>
                    let currentPage = 1;
                    const totalPages = %d;
                    
                    function updateButtons() {
                        document.getElementById('prevBtn').disabled = currentPage === 1;
                        document.getElementById('nextBtn').disabled = currentPage === totalPages;
                        document.getElementById('pageSelect').value = currentPage;
                        updateSvgFrame();
                    }
                    
                    function updateSvgFrame() {
                        const svgPath = '../svg/page_' + String(currentPage).padStart(3, '0') + '.svg';
                        document.getElementById('svgFrame').src = svgPath;
                    }
                    
                    function prevPage() {
                        if (currentPage > 1) {
                            currentPage--;
                            updateButtons();
                        }
                    }
                    
                    function nextPage() {
                        if (currentPage < totalPages) {
                            currentPage++;
                            updateButtons();
                        }
                    }
                    
                    function goToPage() {
                        currentPage = parseInt(document.getElementById('pageSelect').value);
                        updateButtons();
                    }
                    
                    // 초기 페이지 로드
                    updateButtons();
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun getOutputFolders(pdfPath: Path, baseOutputDir: String): OutputFolders {
        val pdfFileName = pdfPath.nameWithoutExtension
        val pdfDir = Paths.get(baseOutputDir, pdfFileName)
        val htmlDir = pdfDir.resolve("html")
        val svgDir = pdfDir.resolve("svg")
        return OutputFolders(pdfDir, htmlDir, svgDir)
    }

    private fun validatePaths(pdfPath: Path, folders: OutputFolders): Boolean {
        // pdf2svg 명령어 존재 확인
        try {
            ProcessBuilder("pdf2svg", "--version")
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
        } catch (e: Exception) {
            println("에러: pdf2svg가 설치되지 않았습니다. 'brew install pdf2svg' 명령어로 설치해주세요.")
            return false
        }

        // PDF 파일 검증
        if (!pdfPath.exists()) {
            println("에러: PDF 파일을 찾을 수 없습니다: $pdfPath")
            return false
        }

        if (!pdfPath.toString().lowercase().endsWith(".pdf")) {
            println("에러: 입력 파일이 PDF 형식이 아닙니다: $pdfPath")
            return false
        }

        // 기존 디렉토리 삭제
        if (folders.pdfDir.exists()) {
            println("기존 출력 디렉토리 삭제 중: ${folders.pdfDir}")
            folders.pdfDir.toFile().deleteRecursively()
        }

        // 디렉토리 생성
        try {
            Files.createDirectories(folders.htmlDir)
            Files.createDirectories(folders.svgDir)
            println("새 출력 디렉토리 생성됨: ${folders.pdfDir}")
        } catch (e: Exception) {
            println("에러: 출력 디렉토리를 생성할 수 없습니다: ${e.message}")
            return false
        }

        return true
    }

    private fun convertPdfPageToSvg(pdfPath: Path, outputPath: Path, pageNumber: Int): Boolean {
        return try {
            val process = ProcessBuilder(
                "pdf2svg",
                pdfPath.toString(),
                outputPath.toString(),
                pageNumber.toString()
            )
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            println("페이지 변환 중 에러 발생: ${e.message}")
            false
        }
    }

    private fun createHtmlViewer(htmlDir: Path, totalPages: Int) {
        // 페이지 옵션 생성
        val pageOptions = buildString {
            for (i in 1..totalPages) {
                append("<option value=\"$i\">페이지 $i</option>\n")
            }
        }

        // HTML 콘텐츠 생성
        // String.format 대신 replace 사용
        val htmlContent = htmlTemplate
            .replace("%s", pageOptions)
            .replace("%d", totalPages.toString())

        // HTML 파일 저장
        try {
            htmlDir.resolve("viewer.html").writeText(htmlContent)
        } catch (e: Exception) {
            println("HTML 파일 생성 중 에러 발생: ${e.message}")
            throw e
        }
    }

    // 모듈 루트 경로를 얻는 함수
    private fun getModuleRootPath(): Path {
        return Paths.get("").toAbsolutePath()
    }

    // 입력 파일 경로를 모듈 기준으로 처리하는 함수
    private fun resolveInputPath(relativePath: String): Path {
        val moduleRoot = getModuleRootPath()
        println("모듈 루트 경로: $moduleRoot")

        return moduleRoot.resolve(relativePath).normalize()
    }

    fun convert(pdfPath: String) {
        val startTime = Instant.now()

        // 모듈 기준 경로로 변환
        val pdfFilePath = resolveInputPath(pdfPath)
        println("변환할 PDF 파일 경로: $pdfFilePath")

        // 입력 파일 존재 여부 확인
        if (!Files.exists(pdfFilePath)) {
            println("에러: PDF 파일을 찾을 수 없습니다: $pdfFilePath")
            return
        }

        // 출력 디렉토리도 모듈 기준으로 설정
        val outputBase = resolveInputPath("build/output_svgs")
        val folders = getOutputFolders(pdfFilePath, outputBase.toString())

        try {
            if (!validatePaths(pdfFilePath, folders)) {
                return
            }

            println("PDF 파일 로딩 중... ($pdfFilePath)")

            // PDF 페이지 수 확인
            val document = Loader.loadPDF(pdfFilePath.toFile())
            val totalPages = document.numberOfPages
            println("총 $totalPages 페이지 변환 시작")

            // 각 페이지 변환
            for (pageNum in 1..totalPages) {
                print("\r페이지 변환 중: $pageNum/$totalPages")

                val outputPath = folders.svgDir.resolve("page_%03d.svg".format(pageNum))
                if (convertPdfPageToSvg(pdfFilePath, outputPath, pageNum)) {
                    val fileSize = Files.size(outputPath) / 1024 // KB로 변환
                    println(" (${fileSize}KB)")
                }
            }

            // HTML 뷰어 생성
            createHtmlViewer(folders.htmlDir, totalPages)

            val duration = Duration.between(startTime, Instant.now())
            println("\n변환 완료!")
            println("PDF 출력 폴더: ${folders.pdfDir.toAbsolutePath()}")
            println("HTML 뷰어 경로: ${folders.htmlDir.resolve("viewer.html").toAbsolutePath()}")
            println("SVG 파일 경로: ${folders.svgDir.toAbsolutePath()}")
            println("총 소요 시간: ${duration.seconds}.${duration.nano / 1_000_000}초")
            println("\n브라우저에서 viewer.html 파일을 열어 SVG 파일들을 확인할 수 있습니다.")

        } catch (e: Exception) {
            println("\n에러 발생: ${e.message}")
            throw e
        }
    }
}
