package com.example.hotarticle.service.eventhandler

import com.example.hotarticle.repository.ArticleCreatedTimeRepository
import com.example.hotarticle.repository.HotArticleListRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val hotArticleListRepository: HotArticleListRepository,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) : EventHandler<ArticleDeletedEventPayload> {

    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload!!
        articleCreatedTimeRepository.delete(payload.articleId)
        hotArticleListRepository.remove(payload.articleId, payload.createdAt)
    }

    override fun supports(event: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_DELETED == event.type
    }

    override fun findArticleId(event: Event<ArticleDeletedEventPayload>): Long {
        return event.payload!!.articleId
    }
}