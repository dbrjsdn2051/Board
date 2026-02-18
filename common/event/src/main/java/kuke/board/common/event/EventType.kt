package kuke.board.common.event

import kuke.board.common.event.payload.*
import org.slf4j.LoggerFactory

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String
) {
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, Topic.BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, Topic.BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, Topic.BOARD_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.BOARD_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, Topic.BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, Topic.BOARD_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, Topic.BOARD_VIEW)
    ;

    companion object {
        private val log = LoggerFactory.getLogger(EventType::class.java)

        fun from(type: String): EventType? {
            return try {
                valueOf(type)
            } catch (e: Exception) {
                log.error("Error deserializing event", e)
                null
            }
        }
    }

    object Topic {
        const val BOARD_ARTICLE = "board-article"
        const val BOARD_COMMENT = "board-comment"
        const val BOARD_LIKE = "board-like"
        const val BOARD_VIEW = "board-view"
    }
}