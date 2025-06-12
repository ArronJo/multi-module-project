package com.snc.zero.core.ua

import ua_parser.Client
import ua_parser.Parser

/**
 * UserAgent Parser
 * : https://github.com/ua-parser/uap-java
 */
class UAParser {

    private lateinit var client: Client

    fun parse(uaString: String): Client {
        client = Parser().parse(uaString)
        return client
    }
}
