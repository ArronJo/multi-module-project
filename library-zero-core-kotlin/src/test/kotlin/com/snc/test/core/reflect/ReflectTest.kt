package com.snc.test.core.reflect

import com.snc.zero.core.reflect.Reflector
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ReflectTest : BaseJUnit5Test() {

    class SampleClass {
        private var count = 0

        fun increment(by: Int) {
            count += by
        }

        fun getCount(): Int = count
    }

    @Test
    fun `getMethod should return the correct method by name`() {
        val instance = SampleClass()
        val method = Reflector.getMethod(instance, "increment")

        assertNotNull(method)
        assertEquals("increment", method?.name)
    }

    @Test
    fun `getMethod should return null if method not found`() {
        val instance = SampleClass()
        val method = Reflector.getMethod(instance, "nonExistentMethod")

        assertNull(method)
    }

    @Test
    fun `invoke should successfully call private method`() {
        val instance = SampleClass()
        val method = Reflector.getMethod(instance, "increment")
        assertNotNull(method)

        Reflector(instance, method!!, 5)

        assertEquals(5, instance.getCount())
        instance.increment(1)
    }

    @Test
    fun `invoke should throw if method is incompatible`() {
        val instance = SampleClass()
        val method = Reflector.getMethod(instance, "increment")
        assertNotNull(method)

        assertThrows(IllegalArgumentException::class.java) {
            Reflector(instance, method!!, "invalidParam") // should be Int
        }
    }
}
