package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCommentCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.event.payload.CommentDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository
) : EventHandler<CommentDeletedEventPayload> {

    override fun handle(event: Event<out EventPayload>) {
        val payload = event.payload!! as CommentDeletedEventPayload
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.COMMENT_DELETED == event.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload!! as CommentDeletedEventPayload).articleId
    }
}