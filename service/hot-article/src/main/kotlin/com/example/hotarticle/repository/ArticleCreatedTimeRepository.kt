package com.example.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class ArticleCreatedTimeRepository(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private const val KEY_FORMAT = "hot-article::article::%s::created-time"
    }

    fun createOrUpdate(articleId: Long, createdTime: LocalDateTime, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), createdTime.toString(), ttl)
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): LocalDateTime? {
        val result = redisTemplate.opsForValue().get(generateKey(articleId)) ?: return null
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(result.toLong()), ZoneOffset.UTC)
    }

    fun generateKey(articleId: Long): String {
        return KEY_FORMAT.format(articleId)
    }
}