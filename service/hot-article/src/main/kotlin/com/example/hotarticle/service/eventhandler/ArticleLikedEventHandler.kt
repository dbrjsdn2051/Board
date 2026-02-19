package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleLikeCountRepository
import com.example.hotarticle.utils.TimeCalculatorUtils
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleLikedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository
) : EventHandler<ArticleLikedEventPayload> {

    override fun handle(event: Event<ArticleLikedEventPayload>) {
        val payload = event.payload!!
        articleLikeCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleLikeCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_LIKED == event.type
    }

    override fun findArticleId(event: Event<ArticleLikedEventPayload>): Long {
        return event.payload!!.articleId
    }
}