package board.common.event.payload

import board.common.event.EventPayload
import java.time.LocalDateTime

class ArticleUnlikedEventPayload(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val articleLikeCount: Long,
) : EventPayload {
}