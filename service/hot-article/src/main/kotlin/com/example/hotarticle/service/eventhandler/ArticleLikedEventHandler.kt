package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleLikeCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.event.payload.ArticleLikedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository
) : EventHandler<ArticleLikedEventPayload> {

    override fun handle(event: Event<out EventPayload>) {
        val payload = event.payload!! as ArticleLikedEventPayload
        articleLikeCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleLikeCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_LIKED == event.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload!! as ArticleLikedEventPayload).articleId
    }
}