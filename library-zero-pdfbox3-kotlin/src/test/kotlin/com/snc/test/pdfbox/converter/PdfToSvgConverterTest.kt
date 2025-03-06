package com.snc.test.pdfbox.converter

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.pdfbox.converter.PdfToSvgConverter
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.nio.file.Paths

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PdfToSvgConverterTest : BaseJUnit5Test() {

    @Test
    fun `PDF 를 SVG 파일로 추출 - 정상`() {
        // 에러: pdf2svg가 설치되지 않았습니다. 'brew install pdf2svg' 명령어로 설치해주세요.
        //val inputPdf = File(this::class.java.classLoader.getResource("input/CMS00017.pdf")!!.toURI())
        val inputPdf = Paths.get(ClassLoader.getSystemResource("input/CMS00017.pdf")!!.toURI())
        val converter = PdfToSvgConverter()
        logger.debug { "inputPdf: $inputPdf" }
        converter.convert(inputPdf.toString())
    }

    @Test
    fun `PDF 를 SVG 파일로 추출 - PDF 타입 아님`() {
        val inputPdf = Paths.get(ClassLoader.getSystemResource("input/text.txt").toURI())
        val converter = PdfToSvgConverter()
        converter.convert(inputPdf.toString())
    }

    @Test
    fun `PDF 를 SVG 파일로 추출 - 파일 미 존재`() {
        val converter = PdfToSvgConverter()
        converter.convert("./docs/input/t.pdf")
    }
}
