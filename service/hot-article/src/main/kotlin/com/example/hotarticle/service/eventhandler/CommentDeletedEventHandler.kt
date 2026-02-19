package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCommentCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.CommentDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository
) : EventHandler<CommentDeletedEventPayload> {

    override fun handle(event: Event<CommentDeletedEventPayload>) {
        val payload = event.payload!!
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.COMMENT_DELETED == event.type
    }

    override fun findArticleId(event: Event<CommentDeletedEventPayload>): Long {
        return event.payload!!.articleId
    }
}