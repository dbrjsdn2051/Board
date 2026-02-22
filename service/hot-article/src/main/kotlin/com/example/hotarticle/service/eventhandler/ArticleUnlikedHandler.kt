package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleLikeCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.event.payload.ArticleUnlikedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository
) : EventHandler<ArticleUnlikedEventPayload> {

    override fun handle(event: Event<out EventPayload>) {
        val payload = event.payload!! as ArticleUnlikedEventPayload
        articleLikeCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleLikeCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_UNLIKED == event.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload!! as ArticleUnlikedEventPayload).articleId
    }
}