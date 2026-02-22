package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleViewCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.event.payload.ArticleViewedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleViewEventHandler(
    private val articleViewCountRepository: ArticleViewCountRepository
): EventHandler<ArticleViewedEventPayload> {

    override fun handle(event: Event<out EventPayload>) {
        val payload = event.payload!! as ArticleViewedEventPayload
        articleViewCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleViewCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_VIEWED == event.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload!! as ArticleViewedEventPayload).articleId
    }
}