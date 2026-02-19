package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCommentCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.CommentCreatedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository
): EventHandler<CommentCreatedEventPayload> {

    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload = event.payload!!
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return event.type == EventType.COMMENT_CREATED
    }

    override fun findArticleId(event: Event<CommentCreatedEventPayload>): Long {
        return event.payload!!.articleId
    }
}