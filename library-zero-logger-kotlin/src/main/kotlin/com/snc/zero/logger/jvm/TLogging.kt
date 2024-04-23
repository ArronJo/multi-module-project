package com.snc.zero.logger.jvm

import io.github.oshai.kotlinlogging.KotlinLogging

object TLogging {

    fun logger(func: () -> Unit): TLogger {
        val logger = KotlinLogging.logger(func)
        return TLogger(logger)
    }

    fun logger(name: String): TLogger {
        val logger = KotlinLogging.logger(name)
        return TLogger(logger)
    }
}