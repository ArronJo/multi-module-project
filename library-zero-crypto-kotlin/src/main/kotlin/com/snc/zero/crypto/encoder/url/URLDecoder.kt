package com.snc.zero.crypto.encoder.url

import java.net.URLDecoder

/**
 * URL Decoder
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object URLDecoder {

    private const val CHARSET = "utf-8"

    /**
     * javascript: decodeURI()
     */
    fun decodeURI(data: String, charset: String = CHARSET): String {
        return decodeURIComponent(data, charset)
    }

    /**
     * javascript: decodeURIComponent()
     */
    fun decodeURIComponent(data: String, charset: String = CHARSET): String {
        return URLDecoder.decode(data, charset)
    }

    /**
     * path 이하 Decoding
     * @Deprecated("Use decodeURI() instead")
     */
    fun decodeURIPath(uri: java.net.URI, charset: String = CHARSET): String {
        var s = "${uri.scheme}://${uri.host}"
        if (-1 != uri.port) {
            s += ":${uri.port}"
        }
        s += URLDecoder.decode(uri.path, charset)
        uri.query?.let {
            if (it.isNotEmpty()) {
                s += "?" + URLDecoder.decode(it, charset)
            }
        }
        return s
    }
}
