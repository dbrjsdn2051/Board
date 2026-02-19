package com.example.hotarticle.client

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value($$"${endpoints.board-article-service.url}") private val articleServiceUrl: String,
) {
    private val restClient = RestClient.create(articleServiceUrl)

    companion object {
        private val log = LoggerFactory.getLogger(ArticleClient::class.java)
    }

    fun read(articleId: Long): ArticleResponse? {
        return try {
            restClient.get()
                .uri("/v1/articles/$articleId")
                .retrieve()
                .body<ArticleResponse>()
        } catch (e: Exception) {
            log.error("[ArticleClient.read] articleId=$articleId e=$e")
            null
        }
    }

    data class ArticleResponse(
        val articleId: Long,
        val title: String,
        val createdAt: LocalDateTime,
    )
}