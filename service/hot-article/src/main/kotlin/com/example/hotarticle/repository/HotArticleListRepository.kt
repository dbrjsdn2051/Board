package com.example.hotarticle.repository

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.DefaultStringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class HotArticleListRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    companion object {
        private val log = LoggerFactory.getLogger(HotArticleListRepository::class.java)
        private const val KEY_FORMAT = "hot-article::list::%s"
        private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    fun add(articleId: Long, time: LocalDateTime, score: Long, limit: Long, ttl: Duration) {
        redisTemplate.executePipelined(RedisCallback<Any?> { action ->
            val conn = DefaultStringRedisConnection(action)
            val key = generatedKey(time)
            conn.zAdd(key, score.toDouble(), articleId.toString())
            conn.zRemRange(key, 0, -limit - 1)
            conn.expire(key, ttl.toSeconds())
            null
        })
    }

    fun remove(articleId: Long, time: LocalDateTime) {
        redisTemplate.opsForZSet().remove(generatedKey(time), articleId.toString())
    }

    private fun generatedKey(time: LocalDateTime): String {
        return generatedKey(TIME_FORMATTER.format(time))
    }

    private fun generatedKey(dataStr: String): String {
        return KEY_FORMAT.format(dataStr)
    }

    fun readAll(dateStr: String): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(generatedKey(dateStr), 0, -1)
            ?.map { tuple ->
                log.info("[HotArticleListRepository.readAll] articleId=${tuple.value}, score=${tuple.score}")
                tuple.value!!.toLong()
            } ?: emptyList()
    }
}