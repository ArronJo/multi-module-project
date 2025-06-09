package com.snc.zero.markdown

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.AttributeProvider
import com.vladsch.flexmark.html.AttributeProviderFactory
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory
import com.vladsch.flexmark.html.renderer.AttributablePart
import com.vladsch.flexmark.html.renderer.LinkResolverContext
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.html.MutableAttributes

/**
 * https://github.com/vsch/flexmark-java/tree/master
 *
 * Syntax: https://anys4udoc.readthedocs.io/en/latest/attach/doc-markdown.html
 */
class MarkdownConverter {
    private val parser: Parser
    private val renderer: HtmlRenderer

    init {
        val options = MutableDataSet().apply {
            // TablesExtension, AutolinkExtension 을 추가합니다
            set(Parser.EXTENSIONS, listOf(TablesExtension.create(), AutolinkExtension.create()))
            // 링크를 새 탭에서 열도록 설정 (선택사항)
            //set(HtmlRenderer.LINKS_AS_HTTPS, true)
            //set(HtmlRenderer.RENDER_HEADER_ID, true)    // 헤더에 ID를 생성
            //set(HtmlRenderer.GENERATE_HEADER_ID, true)  // ???
        }
        parser = Parser.builder(options).build()
        renderer = HtmlRenderer.builder(options)
            .attributeProviderFactory(CustomTableAttributeProvider.factory())
            .build()

    }

    fun convertMarkdownToHtml(markdown: String): String {
        val document: Node = parser.parse(markdown)
        return renderer.render(document)
    }
}

class CustomTableAttributeProvider private constructor() : AttributeProvider {
    override fun setAttributes(node: Node, part: AttributablePart, attributes: MutableAttributes) {
        if (part == AttributablePart.NODE) {
            when (node.nodeName) {
                "Heading" -> attributes.addValue("class", "custom-heading")
                "Paragraph" -> attributes.addValue("class", "custom-paragraph")
                "Emphasis" -> attributes.addValue("class", "custom-emphasis")
                "StrongEmphasis" -> attributes.addValue("class", "custom-strong")
                "BulletList" -> attributes.addValue("class", "custom-bullet-list")
                "ThematicBreak" -> attributes.addValue("class", "custom-thematic-break")
                "TableBlock" -> attributes.addValue("class", "custom-table")
                "TableHead" -> attributes.addValue("class", "custom-table-head")
                "TableBody" -> attributes.addValue("class", "custom-table-body")
                "TableRow" -> attributes.addValue("class", "custom-table-row")
                "TableCell" -> attributes.addValue("class", "custom-table-cell")
                "FencedCodeBlock" -> attributes.addValue("class", "custom-code-block")
                "BlockQuote" -> attributes.addValue("class", "custom-quote")
                "OrderedList" -> attributes.addValue("class", "custom-ordered-list")
                // else -> println(node.nodeName)
            }
        }
    }

    companion object {
        fun factory(): AttributeProviderFactory {
            return object : IndependentAttributeProviderFactory() {
                override fun apply(context: LinkResolverContext): AttributeProvider {
                    return CustomTableAttributeProvider()
                }
            }
        }
    }
}
