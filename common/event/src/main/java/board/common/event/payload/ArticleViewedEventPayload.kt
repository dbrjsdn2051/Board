package board.common.event.payload

import board.common.event.EventPayload

class ArticleViewedEventPayload(
    val articleId: Long,
    val articleViewCount: Long
) : EventPayload {
}