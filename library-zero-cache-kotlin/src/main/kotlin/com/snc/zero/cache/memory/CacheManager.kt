package com.snc.zero.cache.memory

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

/**
 * Caffeine은 Java 8 이상에서 사용할 수 있는 고성능 캐시 라이브러리입니다.
 * Guava Cache 를 대체를 목적으로 만들어졌다.
 * Caffeine은 특히 메모리 캐시와 관련된 여러 기능을 제공하여, 높은 성능과 효율성을 제공합니다.
 */
object CacheManager {

    data class CacheValue<T>(val value: T, val expiryTime: Long)

    private val cache = Caffeine.newBuilder()
        //.expireAfterWrite(10, TimeUnit.MINUTES) // 10분 후 만료
        .maximumSize(100) // 최대 100개의 항목 저장
        .build<String, CacheValue<Any>>()

    // 값 저장 (유효 기간 설정)
    fun put(key: String, value: Any, duration: Long = 10 * 60, timeUnit: TimeUnit = TimeUnit.SECONDS) {
        val expiryTime = System.currentTimeMillis() + timeUnit.toMillis(duration)
        cache.put(key, CacheValue(value, expiryTime))
    }

    // 값 가져오기
    fun get(key: String): Any? {
        val cacheValue = cache.getIfPresent(key)
        return if (cacheValue != null && System.currentTimeMillis() < cacheValue.expiryTime) {
            cacheValue.value // 유효 기간이 아직 남아있으면 값 반환
        } else {
            cache.invalidate(key) // 유효 기간이 만료되었으면 캐시에서 삭제
            null // 만료된 값은 null 반환
        }
    }

    // 값 삭제
    fun delete(key: String) {
        cache.invalidate(key)
    }

    fun deleteAll() {
        cache.invalidateAll()
    }
}
