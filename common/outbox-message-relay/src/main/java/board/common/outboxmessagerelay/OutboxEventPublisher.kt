package board.common.outboxmessagerelay

import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.snowflake.Snowflake
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class OutboxEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val outboxIdSnowflake = Snowflake()
    private val eventIdSnowflake = Snowflake()

    fun publish(type: EventType, payload: EventPayload, shardKey: Long) {
        val outbox = Outbox.create(
            outboxId = outboxIdSnowflake.nextId(),
            eventType = type,
            payload = Event.of(eventIdSnowflake.nextId(), type, payload).toJson()!!,
            shardKey = shardKey % MessageRelayConstants.SHARD_COUNT
        )
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox))
    }
}
