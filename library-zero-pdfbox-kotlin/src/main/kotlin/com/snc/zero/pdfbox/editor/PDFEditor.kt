package com.snc.zero.pdfbox.editor

import com.snc.zero.logger.jvm.TLogging
import org.apache.pdfbox.multipdf.Splitter
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.color.PDColor
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.PDFTextStripperByArea
import org.apache.pdfbox.text.TextPosition
import java.awt.geom.Rectangle2D
import java.io.File
import javax.annotation.processing.Generated

/**
 * Kotlin을 사용하여 PDF 문서 내 특정 위치를 찾아 하이라이트 처리하는 방법을 알려드리겠습니다.
 * Kotlin에서는 Java 라이브러리를 사용할 수 있으므로, Apache PDFBox 라이브러리를 사용하여 이 작업을 수행할 수 있습니다.
 * implementation("org.apache.pdfbox:pdfbox:2.0.24")
 */
class PDFEditor private constructor() {

    companion object {

        private val logger = TLogging.logger { }

        fun mkdirs(filepath: String) {
            // output 디렉토리 경로 생성
            val outputDir = File(filepath).parentFile

            // output 디렉토리가 없으면 생성
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            } else {
                // output 디렉토리가 있으면 내부 파일들 삭제
                outputDir.listFiles()?.forEach { file ->
                    @Generated
                    if (file.isFile && !file.delete()) {
                        println("delete failed...")
                    }
                }
            }
        }

        /**
         * 검색어가 있는 페이지 추출 + 하이라이트 처리
         */
        fun extractHighlightPages(inputPath: String, outputPath: String, textToFind: Array<String>): Boolean {
            // 좌표 추출
            val locations = extractTextLocations(inputPath, textToFind)
            locations.forEach { location ->
                logger.info { "Page: ${location.page}, X: ${location.x}, Y: ${location.y}, Width: ${location.width}, Height: ${location.height}" }
            }
            if (locations.isEmpty()) {
                return false
            }
            // PDF 편집
            PDDocument.load(File(inputPath)).use { document ->
                val highlightedPages = mutableSetOf<Int>()
                locations.forEach { location ->
                    makeHighlight(document, location.page, location.x, location.y, location.width, location.height)
                    highlightedPages.add(location.page)
                }

                // 대상 페이지만 추출
                writePDF(outputPath, document, highlightedPages)
                return true
            }
        }

        /**
         * 텍스트가 있는 페이지 추출
         */
        fun extractTextInPDF(inputPath: String, outputPath: String, textToHighlight: Array<String>) {
            PDDocument.load(File(inputPath)).use { document ->
                val splitPages = mutableSetOf<Int>()
                val stripper = PDFTextStripper()

                for (pageNo in 0 until document.numberOfPages) {
                    stripper.startPage = pageNo + 1
                    stripper.endPage = pageNo + 1
                    val content = stripper.getText(document)
                    val tmp = content.replace(Regex("\\r?\\n"), "")

                    textToHighlight.forEach {
                        if (tmp.indexOf(it, ignoreCase = true) != -1) {
                            logger.info { ">>>>> FIND: page = ${pageNo + 1},  textToHighlight = $textToHighlight" }
                            splitPages.add(pageNo)
                        }
                    }
                }

                // 하이라이트된 페이지만 포함하는 새 문서 생성
                writePDF(outputPath, document, splitPages)
            }
        }

        data class TextLocation(val page: Int, val x: Float, val y: Float, val width: Float, val height: Float)

        /**
         * 검색어에 해당하는 컴포넌트 좌표 위치 찾기
         */
        fun extractTextLocations(inputPath: String, textToFind: Array<String>): List<TextLocation> {
            val textLocations = mutableListOf<TextLocation>()
            val highlightedPages = mutableSetOf<Int>()

            PDDocument.load(File(inputPath)).use { document ->
                for (pageIndex in 0 until document.numberOfPages) {
                    val stripper = object : PDFTextStripperByArea() {
                        override fun writeString(text: String, textPositions: List<TextPosition>) {
                            textToFind.forEach {
                                if (text.contains(it, ignoreCase = true)) {
                                    val matchStart = text.indexOf(it, ignoreCase = true)
                                    val matchEnd = matchStart + it.length
                                    val startPosition = textPositions[matchStart]
                                    val endPosition = textPositions[matchEnd - 1]

                                    textLocations.add(
                                        TextLocation(
                                            pageIndex, // 페이지 번호는 0부터 시작
                                            startPosition.x,
                                            startPosition.y,
                                            endPosition.endX - startPosition.x,
                                            startPosition.height
                                        )
                                    )
                                    highlightedPages.add(pageIndex)
                                }
                            }
                            super.writeString(text, textPositions)
                        }
                    }

                    stripper.sortByPosition = true

                    val page = document.getPage(pageIndex)
                    val rect = Rectangle2D.Float(
                        0f,
                        0f,
                        page.mediaBox.width,
                        page.mediaBox.height
                    )
                    stripper.addRegion("region", rect)
                    stripper.extractRegions(page)
                }
            }

            return textLocations
        }

        /**
         * 특정 좌표에 하이라이트 처리
         */
        private fun makeHighlight(document: PDDocument, pageNo: Int, x: Float, y: Float, width: Float, height: Float) {
            val pdPage = document.getPage(pageNo)
            val annotation =
                PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT)
            annotation.color = PDColor(
                floatArrayOf(1f, 1f, 0f),
                PDDeviceRGB.INSTANCE
            )
            // Highlight 좌표 계산하기...이게 문제네....
            logger.info { "PDRectangle: $pageNo, X: $x, Y: $y, Width: $width, Height: $height" }
            val position = PDRectangle(x, pdPage.mediaBox.height - y, width, height)
            annotation.rectangle = position
            annotation.quadPoints = position.toQuadPoints()
            pdPage.annotations.add(annotation)
        }

        /**
         * 특정 페이지 추출
         * @param pagesToKeep - 추출 페이지 번호
         */
        fun extractPages(inputPath: String, outputPath: String, pagesToKeep: List<Int>) {
            mkdirs(outputPath)

            PDDocument.load(File(inputPath)).use { document ->
                val splitter = Splitter()
                val pages = splitter.split(document)

                PDDocument().use { newDocument ->
                    // 페이지 추출 (1-based index)
                    for (pageNum in pagesToKeep) {
                        // if (pageNum in 0 until pages.size) { // 0-based index
                        if (pageNum in 1..pages.size) { // 1-based index
                            val page = pages[pageNum - 1].getPage(0)
                            newDocument.addPage(page)
                        }
                    }
                    newDocument.save(outputPath)
                }
            }
        }

        /**
         * 특정 페이지를 PDF 로 저장
         */
        private fun writePDF(outputPath: String, document: PDDocument, cropPages: Set<Int>) {
            mkdirs(outputPath)

            //++ 하이라이트된 페이지만 포함하는 새 문서 생성
            PDDocument().use { newDocument ->
                cropPages.sorted().forEach { pageNum ->
                    val page = document.getPage(pageNum)
                    val newPage = PDPage(page.mediaBox)
                    newDocument.addPage(newPage)

                    // 페이지의 내용과 리소스를 복사
                    @Suppress("DEPRECATION") // Kotlin에서는 @Suppress 사용
                    PDPageContentStream(
                        newDocument,
                        newPage,
                        PDPageContentStream.AppendMode.APPEND,
                        true
                    ).use { contentStream ->
                        page.contents.use { contents ->
                            val bytes = contents.readBytes()
                            contentStream.appendRawCommands(bytes)
                        }
                    }

                    newPage.resources = page.resources
                    // 주석 복사
                    page.annotations.forEach { annotation ->
                        newPage.annotations.add(annotation)
                    }
                }
                newDocument.save(outputPath)
            }
            //||
            /*
            PDDocument().use { newDocument ->
                val layerUtility = LayerUtility(newDocument)

                highlightedPages.sorted().forEach { pageNum ->
                    val page = document.getPage(pageNum)
                    val newPage = PDPage(page.mediaBox)
                    newDocument.addPage(newPage)

                    // 페이지의 내용을 복사
                    layerUtility.importPageAsForm(document, pageNum)
                    //layerUtility.importPageAsForm(document, pageNum, newPage)

                    // 주석 복사
                    page.annotations.forEach { annotation ->
                        newPage.annotations.add(annotation)
                    }
                }
                newDocument.save(outputPath)
            }
             */
            //||
            /*
            // 원본 그대로 저장
            document.save(outputPath)
             */
            //--
        }

        private fun PDRectangle.toQuadPoints(): FloatArray {
            return floatArrayOf(
                lowerLeftX,
                lowerLeftY,
                upperRightX,
                lowerLeftY,
                lowerLeftX,
                upperRightY,
                upperRightX,
                upperRightY
            )
        }
    }
}
