package board.common.outboxmessagerelay

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

@Component
class MessageRelay(
    private val outboxRepository: OutboxRepository,
    private val messageRelayCoordinator: MessageRelayCoordinator,
    private val messageRelayKafkaTemplate: KafkaTemplate<String, String>
) {

    companion object {
        private val log = Logger.getLogger(MessageRelay::class.java.name)
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun createOutbox(outboxEvent: OutboxEvent) {
        log.info("[MessageRelay.createOutbox] outboxEvent: $outboxEvent")
        val outbox = outboxEvent.outbox
        Outbox.create(outbox.id.value, outbox.eventType, outbox.payload, outbox.shardKey)
    }

    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishEvent(outboxEvent: OutboxEvent) {
        log.info("[MessagePublisher.publishEvent] outboxEvent: $outboxEvent")
        publishEvent(outboxEvent.outbox)
    }

    private fun publishEvent(outbox: Outbox) {
        try {
            messageRelayKafkaTemplate.send(
                outbox.eventType.topic,
                outbox.shardKey.toString(),
                outbox.payload
            ).get(1, TimeUnit.SECONDS)
            Outboxes.deleteWhere { Outboxes.id eq outbox.id }
        } catch (e: Exception) {
            log.info { "[MessagePublisher.publishEvent] exception: $e, outbox: $outbox" }
        }
    }

    @Scheduled(
        fixedDelay = 10,
        initialDelay = 5,
        timeUnit = TimeUnit.SECONDS,
        scheduler = "messageRelayPublishPendingEventExecutor"
    )
    fun publishPendingEvent() {
        val assignShards = messageRelayCoordinator.assignShards()
        log.info("[MessagePublisher.publishPendingEvent] assignShards: $assignShards size: ${assignShards.shards.size}")
        assignShards.shards.forEach { shard ->
            val outboxes =
                outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                    shard, LocalDateTime.now().minusSeconds(10),
                    100
                )

            outboxes.forEach {
                publishEvent(it)
            }
        }
    }

}