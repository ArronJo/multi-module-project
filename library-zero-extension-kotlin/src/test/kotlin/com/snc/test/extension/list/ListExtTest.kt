package com.snc.test.extension.list

import com.snc.zero.etc.counter.Counter
import com.snc.zero.extension.list.get
import com.snc.zero.extension.list.getShuffledItem
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class ListExtTest : BaseJUnit5Test() {

    @Test
    fun `List get 테스트 1`() {
        // given
        val data = arrayListOf("a", "b", "c", "d")
        // when
        val v1 = data.get(2, "만원")
        // then
        logger.debug { "List get: $v1" }
        assertEquals(v1.toString(), "c")

        val v2 = data.get(20, "만원")
        logger.debug { "List get: $v2" }
        assertEquals(v2.toString(), "만원")
    }

    @Test
    fun `List get 테스트 - Empty`() {
        val arr = arrayListOf<String>()
        logger.debug { "List get: ${arr.get(1, "XXX")}" }
    }

    @Test
    fun `List random 테스트`() {
        // given
        val c1 = Counter()
        val c2 = Counter()
        val c3 = Counter()
        val c4 = Counter()
        val c5 = Counter()
        val list = listOf("A", "B", "C", "D", "E")
        // when
        (1..100).forEach { i ->
            val random1 = list.random()
            val random2 = list[Random().nextInt(list.size)]
            val random3 = list.shuffled()[0]
            val random4 = list.asSequence().shuffled().find { true }
            val random5 = list.getShuffledItem()
            c1.put(random1)
            c2.put(random2)
            c3.put(random3)
            random4?.let {
                c4.put(it)
            }
            c5.put(random5)
        }
        // then
        logger.debug { "random1 result: ${c1.get()}" }
        logger.debug { "random2 result: ${c2.get()}" }
        logger.debug { "random3 result: ${c3.get()}" }
        logger.debug { "random4 result: ${c4.get()}" }
        logger.debug { "random5 result: ${c5.get()}" }
    }

    @Test
    fun `get returns value at valid index`() {
        val list = listOf("a", "b", "c")
        val result = list.get(1, "default")
        assertEquals("b", result)
    }

    @Test
    fun `get returns default when index is out of bounds`() {
        val list = listOf("a", "b", "c")
        val result = list.get(5, "default")
        assertEquals("default", result)
    }

    @Test
    fun `get returns default when list is empty`() {
        val list = emptyList<String>()
        val result = list.get(0, "default")
        assertEquals("default", result)
    }

    @Test
    fun `get returns null if defaultValue is not specified and index is invalid`() {
        val random = SplittableRandom()
        val list = emptyList<String>()
        assertThrows(IndexOutOfBoundsException::class.java) {
            var r = 0
            while (true) {
                if (0 != r) {
                    break
                }
                r = random.nextInt()
            }
            val result = list[r]
            assertNull(result)
        }
    }
}
