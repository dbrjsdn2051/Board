package board.common.outboxmessagerelay

import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import java.time.Instant
import java.util.*
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class MessageRelayCoordinator(
    private val redisTemplate: StringRedisTemplate
) {

    @Value("\${spring.application.name}")
    private lateinit var applicationName: String

    companion object {
        private val APP_ID: String = UUID.randomUUID().toString()
        private const val PING_INTERVAL_SECONDS = 3L
        private const val PING_FAILURE_THRESHOLD = 3L
    }

    fun assignShards(): AssignedShard {
        return AssignedShard.of(APP_ID, findAppIds(), MessageRelayConstants.SHARD_COUNT)
    }

    fun findAppIds(): List<String> {
        return redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1)!!.sorted().toList()
    }

    @Scheduled(fixedDelay = PING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    fun ping() {
        redisTemplate.executePipelined {
            val conn = it as StringRedisConnection
            val key = generateKey()
            conn.zAdd(key, Instant.now().toEpochMilli().toDouble(), APP_ID)
            conn.zRemRangeByScore(
                key,
                Double.NEGATIVE_INFINITY,
                Instant.now().minusSeconds(PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD).toEpochMilli().toDouble()
            )
        }
    }

    @PreDestroy
    fun leave() {
        redisTemplate.opsForZSet().remove(generateKey(), APP_ID)
    }

    fun generateKey(): String {
        return "message-relay-coordinator::app-list::$applicationName"
    }
}