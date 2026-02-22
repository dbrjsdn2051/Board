package board.common.outboxmessagerelay

import board.common.event.EventType
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object Outboxes : IdTable<Long>("outbox") {
    override val id: Column<EntityID<Long>> = long("outbox_id").entityId()
    val eventType = enumerationByName("event_type", 100, EventType::class)
    val payload = varchar("payload", 5000)
    val shardKey = long("shard_key")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

class Outbox(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Outbox>(Outboxes) {
        fun create(outboxId: Long, eventType: EventType, payload: String, shardKey: Long): Outbox {
            return Outbox.new(outboxId) {
                this.eventType = eventType
                this.payload = payload
                this.shardKey = shardKey
                this.createdAt = LocalDateTime.now()
            }
        }
    }

    var eventType by Outboxes.eventType
    var payload by Outboxes.payload
    var shardKey by Outboxes.shardKey
    var createdAt by Outboxes.createdAt
}
