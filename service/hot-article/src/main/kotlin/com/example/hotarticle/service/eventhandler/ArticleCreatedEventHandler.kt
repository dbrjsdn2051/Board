package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCreatedTimeRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleCreatedEventHandler(
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) : EventHandler<ArticleCreatedEventPayload> {

    override fun handle(event: Event<out EventPayload>) {
        val payload = event.payload!! as ArticleCreatedEventPayload
        articleCreatedTimeRepository.createOrUpdate(
            payload.articleId,
            payload.createdAt,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_CREATED == event.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload!! as ArticleCreatedEventPayload).articleId
    }
}