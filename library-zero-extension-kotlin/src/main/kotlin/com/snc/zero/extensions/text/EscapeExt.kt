package com.snc.zero.extensions.text

fun String.escapeRegExp(): String {
    return this.replace("""[.*+?^$\\{}\[\]()|]""".toRegex(), "\\\\$0")
}
