package com.example.hotarticle.service

import com.example.hotarticle.client.ArticleClient
import com.example.hotarticle.repository.HotArticleListRepository
import com.example.hotarticle.service.eventhandler.EventHandler
import com.example.hotarticle.service.response.HotArticleResponse
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import org.springframework.stereotype.Service

@Service
class HotArticleService(
    private val articleClient: ArticleClient,
    private val eventHandlers: List<EventHandler<*>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater,
    private val hotArticleListRepository: HotArticleListRepository
) {
    fun handleEvent(event: Event<EventPayload>) {
        val handler = findEventHandler(event) ?: return

        if (isArticleCreatedOrDeleted(event)) {
            handler.handle(event)
            return
        }
        hotArticleScoreUpdater.update(event, handler)
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<*>? {
        return eventHandlers.firstOrNull { it.supports(event) }
    }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return event.type == EventType.ARTICLE_CREATED || event.type == EventType.ARTICLE_DELETED
    }

    fun readAll(dateStr: String): List<HotArticleResponse> {
        return hotArticleListRepository.readAll(dateStr)
            .mapNotNull { articleClient.read(it) }
            .map { HotArticleResponse.from(it) }
    }
}
