package com.snc.zero.core.ua

import com.snc.zero.extension.text.printJSON
import ua_parser.Client
import ua_parser.Parser

/**
 * UserAgent Parser
 * : https://github.com/ua-parser/uap-java
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class UAParser {

    private lateinit var client: Client

    fun parse(uaString: String): Client {
        client = Parser().parse(uaString)
        return client
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            //val uaString =
            //    "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3"
            //    "Mozilla/5.0 (Linux; Android 7.0; SM-P585N0 Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/56.0.2924.87 Safari/537.36"
            //val c: Client = UAParser().parse(uaString)
            //println(c.userAgent)
            //println(c.os)
            //println(c.device)
            //println(c)

            val str =
                "{ \"user_agent\": { \"family\": \"Chrome Mobile WebView\", \"major\": \"56\", \"minor\": \"0\", \"patch\": \"2924\" }, \"os\": { \"Android\", \"major\": \"7\", \"minor\": \"0\", \"patch\": \"\", \"patch_minor\": \"\" }, \"device\": { \"family\": \"Samsung SM-P585N0\" } }"
            println(str.printJSON(2))

            //println(c.userAgent.family) // => "Mobile Safari"
            //println(c.userAgent.major) // => "5"
            //println(c.userAgent.minor) // => "1"
            //println(c.os.family) // => "iOS"
            //println(c.os.major) // => "5"
            //println(c.os.minor) // => "1"
            //println(c.device.family) // => "iPhone"
        }
    }
}
