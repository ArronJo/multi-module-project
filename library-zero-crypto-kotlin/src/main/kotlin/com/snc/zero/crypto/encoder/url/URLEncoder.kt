package com.snc.zero.crypto.encoder.url

import java.net.URLEncoder

/**
 * URL Encoder
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object URLEncoder {

    private const val CHARSET = "utf-8"

    /**
     * 전체 URL 인코딩
     * javascript: encodeURI()
     * : encodeURI() 함수는 전체 URI(Uniform Resource Identifier)를 인코딩하기 위해 사용됩니다.
     * : 이 함수는 URI를 구성하는 주요 구성 요소 사이의 구분자로 사용되는 특정 문자(예: :, /, ?, &, =)를 인코딩하지 않습니다.
     *
     * ex) 'https://confluence.hanwhalife.com/pages/viewpage.action?pageId=68972232%EC%95%88'
     */
    fun encodeURI(str: String, charset: String = CHARSET): String {
        val sb = StringBuilder()
        for (element in str) {
            when (element) {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '-', '_', '.', '!', '~', '*', '\'', '(', ')',
                ';', ',', '/', '?', ':', '@', '&', '=', '+', '$', '#',
                '%', '[', ']',
                -> sb.append(element)

                else -> sb.append(encodeURIComponent(element.toString(), charset))
            }
        }
        return sb.toString()
    }

    /**
     * URL 파라미터 값 인코딩
     * javascript: encodeURIComponent()
     * : https://www.w3schools.com/tags/ref_urlencode.ASP
     * : encodeURIComponent() 함수는 URI 구성 요소(예: 쿼리스트링 파라미터 값)를 인코딩하기 위해 사용됩니다.
     * : 이 함수는 URI를 구성하는 데 사용되는 거의 모든 특수 문자를 인코딩합니다. 따라서, URI의 일부인 개별적인 쿼리스트링 파라미터나 경로 세그먼트를 인코딩할 때 적합합니다.
     *
     * ex) 'https%3A%2F%2Fconfluence.hanwhalife.com%2Fpages%2Fviewpage.action%3FpageId%3D68972232'
     */
    fun encodeURIComponent(data: String, charset: String = CHARSET): String {
        return URLEncoder.encode(data, charset)
    }

    /**
     * path 이하 Encoding
     * @Deprecated("Use encodeURI() instead")
     * ex) 'https://confluence.hanwhalife.com%2Fpages%2Fviewpage.action%3FpageId%3D68972232'
     */
    fun encodeURIPath(uri: java.net.URI, charset: String = CHARSET): String {
        var s = "${uri.scheme}://${uri.host}"
        if (-1 != uri.port) {
            s += ":${uri.port}"
        }

        val encodedPath = URLEncoder.encode(uri.path, charset)
        s += encodedPath

        if (null != uri.query && uri.query.isNotEmpty()) {
            s += "%3F" + URLEncoder.encode(uri.query, charset) // `?` 인코딩 후 추가
        }

        return s
    }
}
