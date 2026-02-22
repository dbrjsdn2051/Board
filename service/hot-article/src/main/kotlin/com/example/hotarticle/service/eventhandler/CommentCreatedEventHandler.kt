package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCommentCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.event.payload.CommentCreatedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository
): EventHandler<CommentCreatedEventPayload> {

    override fun handle(event: Event<out EventPayload>) {
        val payload = event.payload!! as CommentCreatedEventPayload
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return event.type == EventType.COMMENT_CREATED
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload!! as CommentCreatedEventPayload).articleId
    }
}