package com.snc.test.pdfbox.converter

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.pdfbox.converter.PdfToSvgConverter
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PdfToSvgConverterTest : BaseJUnit5Test() {

    private val inputPdf = "./docs/input/CMS00017.pdf"

    @Test
    fun `PDF 를 SVG 파일로 추출`() {
        val converter = PdfToSvgConverter()

        logger.debug { "inputPdf: $inputPdf" }
        converter.convert(inputPdf)
    }
}
