package com.snc.zero.logger.jvm

import io.github.oshai.kotlinlogging.KLogger

@Suppress("unused")
class TLogger(private val logger: KLogger) {

    fun debug(msg: () -> Any?): Unit =
        logger.debug(msg)

    fun debug(t: Throwable?, msg: () -> Any?): Unit =
        logger.debug(t, msg)

    //@Deprecated("Use debug {} instead", replaceWith = ReplaceWith("debug { \"\$msg \$arg1 \$arg2\"}"))
    //fun debug(msg: String?, arg1: Any?, arg2: Any?): Unit =
    //    logger.debug("$msg", arg1, arg2)

    fun info(msg: () -> Any?): Unit =
        logger.info(msg)

    fun info(t: Throwable?, msg: () -> Any?): Unit =
        logger.info(t, msg)

    //@Deprecated("Use info {} instead", replaceWith = ReplaceWith("info { \"\$msg \$arg1 \$arg2\"}"))
    //fun info(msg: String?, arg1: Any?, arg2: Any?): Unit =
    //    logger.info("$msg", arg1, arg2)

    fun warn(msg: () -> Any?): Unit =
        logger.warn(msg)

    fun warn(t: Throwable?, msg: () -> Any?): Unit =
        logger.warn(t, msg)

    //@Deprecated("Use warn {} instead", replaceWith = ReplaceWith("warn { \"\$msg \$arg1 \$arg2\"}"))
    //fun warn(msg: String?, arg1: Any?, arg2: Any?): Unit =
    //    logger.warn("$msg", arg1, arg2)

    fun error(msg: () -> Any?): Unit =
        logger.error(msg)

    fun error(t: Throwable?, msg: () -> Any?): Unit =
        logger.error(t, msg)

    //@Deprecated("Use error {} instead", replaceWith = ReplaceWith("error { \"\$msg \$arg1 \$arg2\"}"))
    //fun error(msg: String?, arg1: Any?, arg2: Any?): Unit =
    //    logger.error(""$msg", arg1, arg2)
}