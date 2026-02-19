package com.example.hotarticle.service

import com.example.hotarticle.repository.ArticleCreatedTimeRepository
import com.example.hotarticle.repository.HotArticleListRepository
import com.example.hotarticle.service.eventhandler.EventHandler
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class HotArticleScoreUpdater(
    private val hotArticleListRepository: HotArticleListRepository,
    private val hotArticleScoreCalculator: HotArticleScoreCalculator,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) {
    companion object {
        private const val HOT_ARTICLE_COUNT = 10L
        private val HOT_ARTICLE_TTL = Duration.ofDays(10)
    }

    fun update(event: Event<EventPayload>, eventHandler: EventHandler<*>) {
        val articleId = eventHandler.findArticleId(event)
        val createdTime = articleCreatedTimeRepository.read(articleId)

        if (!isArticleCreatedToday(createdTime)){
            return
        }

        val score = hotArticleScoreCalculator.calculate(articleId)
        hotArticleListRepository.add(articleId, createdTime!!, score, HOT_ARTICLE_COUNT, HOT_ARTICLE_TTL)
    }

    fun isArticleCreatedToday(createdTime: LocalDateTime?): Boolean {
        return createdTime!= null && createdTime.toLocalDate().equals(LocalDateTime.now())
    }
}