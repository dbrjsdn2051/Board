package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleViewCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleViewedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleViewEventHandler(
    private val articleViewCountRepository: ArticleViewCountRepository
): EventHandler<ArticleViewedEventPayload> {

    override fun handle(event: Event<ArticleViewedEventPayload>) {
        val payload = event.payload!!
        articleViewCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleViewCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_VIEWED == event.type
    }

    override fun findArticleId(event: Event<ArticleViewedEventPayload>): Long {
        return event.payload!!.articleId
    }
}