package com.example.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleLikeCountRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    companion object {
        private const val KEY_FORMAT = "hot-article::article::%s::like-count"
    }

    fun createOrUpdate(articleId: Long, likeCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), likeCount.toString(), ttl)
    }

    fun read(articleId: Long): Long {
        return redisTemplate.opsForValue().get(generateKey(articleId))?.toLong() ?: 0L
    }

    fun generateKey(articleId: Long): String {
        return KEY_FORMAT.format(articleId)
    }
}