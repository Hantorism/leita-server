package com.leita.leita.port.cache

interface CachePort {
    fun get(key: String): String?
    fun set(key: String, value: String)
    fun remove(key: String)
}