package com.example.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewCountRepository(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private const val KEY_FORMAT = "hot-article::article::{articleId}::view-count"
    }

    fun createOrUpdate(articleId: Long, viewCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), viewCount.toString(), ttl)
    }

    fun read(articleId: Long): Long {
        return redisTemplate.opsForValue().get(generateKey(articleId))?.toLong() ?: 0L
    }

    private fun generateKey(articleId: Long): String {
        return KEY_FORMAT.format(articleId)
    }


}