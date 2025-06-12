package com.snc.test.core.storage

import com.snc.zero.core.storage.CacheStorage
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

class CacheStorageTest : BaseJUnit5Test() {

    companion object {
        private val max = 10 * 10000

        @JvmStatic
        @BeforeAll
        fun beforeClass() {
            logger.info { "beforeClass" }
            // given
            for (i in 1..max) {
                CacheStorage.putString(key = "test", value = i.toString())
            }
        }
    }

    @BeforeEach
    fun setup() {
        // 모든 테스트 전 캐시 초기화 (리플렉션으로 접근)
        val cacheField = CacheStorage::class.java.getDeclaredField("cache")
        cacheField.isAccessible = true
        val cacheMap = cacheField.get(CacheStorage) as MutableMap<*, *>
        cacheMap.clear()
    }

    @Test
    fun `putString and getString without group should store and return value`() {
        CacheStorage.putString(key = "foo", value = "bar")
        val result = CacheStorage.getString(key = "foo")
        assertEquals("bar", result)
    }

    @Test
    fun `putString and getString with group should isolate values`() {
        CacheStorage.putString(group = "g1", key = "foo", value = "bar1")
        CacheStorage.putString(group = "g2", key = "foo", value = "bar2")

        val val1 = CacheStorage.getString(group = "g1", key = "foo")
        val val2 = CacheStorage.getString(group = "g2", key = "foo")

        assertEquals("bar1", val1)
        assertEquals("bar2", val2)
    }

    @Test
    fun `getString should return default value if key not found`() {
        val result = CacheStorage.getString(key = "unknown", defaultValue = "default")
        assertEquals("default", result)
    }

    @Test
    fun `putInt and getInt should store and return int value`() {
        CacheStorage.putInt("grp", "age", 30)
        val result = CacheStorage.getInt("grp", "age", 0)
        assertEquals(30, result)
    }

    @Test
    fun `getInt should return default if key is missing`() {
        val result = CacheStorage.getInt("grp", "missing", 99)
        assertEquals(99, result)
    }

    @Test
    fun `remove should delete a key`() {
        CacheStorage.putString(key = "foo", value = "bar")
        val removed = CacheStorage.remove("foo")
        val result = CacheStorage.getString(key = "foo", defaultValue = "none")

        assertTrue(removed)
        assertEquals("none", result)
    }

    @Test
    fun `remove should return false for non-existent key`() {
        val removed = CacheStorage.remove("not_existing_key")
        assertFalse(removed)
    }
}
