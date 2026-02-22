package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCreatedTimeRepository
import com.example.hotarticle.repository.HotArticleListRepository
import board.common.event.Event
import board.common.event.EventPayload
import board.common.event.EventType
import board.common.event.payload.ArticleDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val hotArticleListRepository: HotArticleListRepository,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) : EventHandler<ArticleDeletedEventPayload> {

    override fun handle(event: Event<out EventPayload>) {
        val payload = event.payload!! as ArticleDeletedEventPayload
        articleCreatedTimeRepository.delete(payload.articleId)
        hotArticleListRepository.remove(payload.articleId, payload.createdAt)
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_DELETED == event.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload!! as ArticleDeletedEventPayload).articleId
    }
}