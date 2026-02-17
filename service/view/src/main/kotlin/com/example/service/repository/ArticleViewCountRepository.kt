package com.example.service.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountRepository(
    private val stringRedisTemplate: StringRedisTemplate
) {
    companion object {
        private const val KEY_FORMAT: String = "view::article::%s::view_count";
    }

    fun read(articleId: Long): Long {
        return stringRedisTemplate.opsForValue().get(generateKey(articleId))?.toLong()
            ?: 0L
    }

    fun increase(articleId: Long): Long {
        return stringRedisTemplate.opsForValue().increment(generateKey(articleId));
    }

    private fun generateKey(articleId: Long): String {
        return KEY_FORMAT.format(articleId)
    }
}