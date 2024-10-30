package com.snc.test.cache.memory

import com.snc.zero.cache.memory.CacheManager
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

private val logger = TLogging.logger { }

class CacheManagerTest : BaseJUnit5Test() {
    @Test
    fun `Caffeine Cache - Test 1`() {
        // 캐시에 데이터 저장
        CacheManager.put("user:1", "John Doe")
        CacheManager.put("user:2", "Jane Doe")

        // 캐시에서 데이터 가져오기
        val user1 = CacheManager.get("user:1")
        val user2 = CacheManager.get("user:2")
        val user3 = CacheManager.get("user:3") // 없는 키를 요청

        // 출력
        logger.debug { "User 1: $user1" } // User 1: John Doe
        logger.debug { "User 2: $user2" } // User 2: Jane Doe
        logger.debug { "User 3: $user3" } // User 3: null

        // 캐시에서 값 삭제
        CacheManager.delete("user:1")

        // 삭제 후 캐시에서 가져오기
        val user1AfterEvict = CacheManager.get("user:1")
        logger.debug { "User 1 after eviction: $user1AfterEvict" } // User 1 after eviction: null

        // 모두 삭제
        CacheManager.deleteAll()
    }

    @Test
    fun `Caffeine Cache - timeout`() {
        CacheManager.put("user:1", "John Doe", 5, TimeUnit.SECONDS) // 5초 유효
        CacheManager.put("user:2", "Jane Doe", 10, TimeUnit.SECONDS) // 10초 유효

        // 캐시에서 데이터 가져오기
        logger.debug { "User 1: ${CacheManager.get("user:1")}" } // User 1: John Doe
        logger.debug { "User 2: ${CacheManager.get("user:2")}" } // User 2: Jane Doe

        // 6초 후 다시 가져오기
        Thread.sleep(6000) // 6초 대기

        logger.debug { "User 1 after 6 seconds: ${CacheManager.get("user:1")}" } // User 1 after 6 seconds: null
        logger.debug { "User 2 after 6 seconds: ${CacheManager.get("user:2")}" } // User 2 after 6 seconds: Jane Doe

        // 5초 후 캐시에서 삭제 확인
        Thread.sleep(5000) // 5초 대기

        logger.debug { "User 2 after 11 seconds: ${CacheManager.get("user:2")}" } // User 2 after 11 seconds: null
    }
}
