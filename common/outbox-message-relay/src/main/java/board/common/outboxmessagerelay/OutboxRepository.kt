package board.common.outboxmessagerelay

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class OutboxRepository {

    fun findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
        shardKey: Long,
        from: LocalDateTime,
        limit: Int
    ): List<Outbox> {
        return Outboxes
            .selectAll()
            .where { (Outboxes.shardKey eq shardKey) and (Outboxes.createdAt lessEq from) }
            .orderBy(Outboxes.createdAt, SortOrder.ASC)
            .limit(limit)
            .map { Outbox.wrapRow(it) }
    }
}
