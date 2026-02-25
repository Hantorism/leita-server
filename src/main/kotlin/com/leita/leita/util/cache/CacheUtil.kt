package com.leita.leita.util.cache

import org.springframework.stereotype.Component

@Component
class CacheUtil {
    private val cache = mutableMapOf<String, String>()

    fun get(key: String): String? {
        return cache[key]
    }

    fun set(key: String, value: String) {
        cache[key] = value
    }

    fun remove(key: String) {
        cache.remove(key)
    }
}

