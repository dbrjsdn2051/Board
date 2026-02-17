package com.example.service.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewDistributedLockRepository(
    private val stringRedisTemplate: StringRedisTemplate
) {

    companion object {
        private const val KEY_FORMAT = "view::article::%s::user::%s::lock"
    }

    fun lock(articleId: Long, userId: Long, ttl: Duration): Boolean {
        val key = generatedKey(articleId, userId)
        return stringRedisTemplate.opsForValue().setIfAbsent(key, "", ttl)
    }

    fun generatedKey(articleId: Long, userId: Long): String {
        return KEY_FORMAT.format(articleId, userId)
    }
}