package com.snc.zero.logger.jvm

import io.github.oshai.kotlinlogging.KLogger

class TLogger(private val logger: KLogger) {

    fun debug(msg: () -> Any?): Unit =
        logger.debug(msg)

    fun debug(t: Throwable?, msg: () -> Any?): Unit =
        logger.debug(t, msg)

    fun info(msg: () -> Any?): Unit =
        logger.info(msg)

    fun info(t: Throwable?, msg: () -> Any?): Unit =
        logger.info(t, msg)

    fun warn(msg: () -> Any?): Unit =
        logger.warn(msg)

    fun warn(t: Throwable?, msg: () -> Any?): Unit =
        logger.warn(t, msg)

    fun error(msg: () -> Any?): Unit =
        logger.error(msg)

    fun error(t: Throwable?, msg: () -> Any?): Unit =
        logger.error(t, msg)
}