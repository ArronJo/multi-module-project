package com.snc.test.extension.list

import com.snc.zero.etc.counter.Counter
import com.snc.zero.extensions.collection.toArrayList
import com.snc.zero.extensions.list.copy
import com.snc.zero.extensions.list.getOrDefault
import com.snc.zero.extensions.list.getShuffledItem
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Random
import java.util.SplittableRandom

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class ListExtTest : BaseJUnit5Test() {

    @Nested
    inner class ListTest {

        @Test
        fun `List get 테스트 1`() {
            // given
            val data = arrayListOf("a", "b", "c", "d")
            // when
            val v1 = data.getOrDefault(2, "만원")
            // then
            logger.debug { "List get: $v1" }
            assertEquals(v1.toString(), "c")

            val v2 = data.getOrDefault(20, "만원")
            logger.debug { "List get: $v2" }
            assertEquals(v2.toString(), "만원")
        }

        @Test
        fun `List get 테스트 - Empty`() {
            val arr = arrayListOf<String>()
            logger.debug { "List get: ${arr.getOrDefault(1, "XXX")}" }
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
            (1..100).forEach { _ ->
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
            val result = list.getOrDefault(1, "default")
            assertEquals("b", result)
        }

        @Test
        fun `get returns default when index is out of bounds`() {
            val list = listOf("a", "b", "c")
            val result = list.getOrDefault(5, "default")
            assertEquals("default", result)
        }

        @Test
        fun `get returns default when list is empty`() {
            val list = emptyList<String>()
            val result = list.getOrDefault(0, "default")
            assertEquals("default", result)
        }

        @Test
        fun `get returns null if defaultValue is not specified and index is invalid`() {
            val random = SplittableRandom()
            val list = emptyList<String>()
            var r = 0
            while (true) {
                if (0 != r) {
                    break
                }
                r = random.nextInt()
            }
            val result = list.getOrDefault(r, null)
            assertNull(result)
        }

        @Test
        fun `should return defaultValue when list is empty`() {
            val list = emptyList<String>()
            val result = list.getOrDefault(0, "default")
            assertEquals("default", result)
        }

        @Test
        fun `should return value at index when index is valid`() {
            val list = listOf("apple", "banana", "cherry")
            val result = list.getOrDefault(1, "default")
            assertEquals("banana", result)
        }

        @Test
        fun `should return defaultValue when index is out of bounds`() {
            val list = listOf("apple", "banana")
            val result = list.getOrDefault(5, "default")
            assertEquals("default", result)
        }

        @Test
        fun `should return null when defaultValue is not provided and index is invalid`() {
            val list = listOf("one", "two")
            val result = list.getOrDefault(10, null)
            assertNull(result)
        }
    }

    @Nested
    inner class ArrayListTest {

        @Test
        fun `ArrayList 객체 복사`() {
            val alist = arrayListOf(1, 2, 3)
            println(alist.copy())
            println(alist.toArrayList())
            @Suppress("UNCHECKED_CAST")
            val blist: ArrayList<Int> = alist.clone() as ArrayList<Int>
            println(blist)

            // 원본 리스트
            val numbers = arrayListOf(1, 2, 3, 4, 5)

            // 여러 복사 방법 테스트
            val copy1 = numbers.toMutableList()
            val copy2 = ArrayList(numbers)
            val copy3 = arrayListOf<Int>() + numbers

            // 원본 수정
            numbers.add(6)

            println("원본: $numbers") // [1, 2, 3, 4, 5, 6]
            println("복사본1: $copy1") // [1, 2, 3, 4, 5]
            println("복사본2: $copy2") // [1, 2, 3, 4, 5]
            println("복사본3: $copy3") // [1, 2, 3, 4, 5]
        }
    }
}
