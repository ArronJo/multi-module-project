package com.snc.zero.test.extensions.debug

import java.util.Locale

fun Long.toSecDisplay(): String {
    return String.format(Locale.getDefault(), "%.3f", this.toDouble() / 1000)
}
