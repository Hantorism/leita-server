package com.leita.leita.port.cache

import org.springframework.stereotype.Component

@Component
class CacheAdapter : CachePort {
    private val cache = mutableMapOf<String, String>()

    override fun get(key: String): String? {
        return cache[key]
    }

    override fun set(key: String, value: String) {
        cache[key] = value
    }

    override fun remove(key: String) {
        cache.remove(key)
    }
}