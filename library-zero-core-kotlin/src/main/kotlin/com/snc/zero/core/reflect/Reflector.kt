package com.snc.zero.core.reflect

import com.snc.zero.logger.jvm.TLogging
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

private val logger = TLogging.logger { }

/**
 * Reflect 객체
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class Reflector private constructor() {

    companion object {

        @Throws(IllegalArgumentException::class)
        fun getMethod(instance: Any, methodName: String): Method? {
            val methods = instance.javaClass.declaredMethods
            logger.info { "methods = $methods, len = ${methods.size}" }
            for (method in methods) {
                if (methodName == method.name) {
                    return method
                }
            }
            return null
        }

        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        operator fun invoke(instance: Any, method: Method, vararg param: Any?) {
            method.isAccessible = true
            method.invoke(instance, *param)
            method.isAccessible = false
        }
    }
}
