package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCreatedTimeRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleCreatedEventHandler(
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) : EventHandler<ArticleCreatedEventPayload> {

    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload!!
        articleCreatedTimeRepository.createOrUpdate(
            payload.articleId,
            payload.createdAt,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_CREATED == event.type
    }

    override fun findArticleId(event: Event<ArticleCreatedEventPayload>): Long {
        return event.payload!!.articleId
    }
}