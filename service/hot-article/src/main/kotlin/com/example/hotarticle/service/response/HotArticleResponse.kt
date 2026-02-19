package com.example.hotarticle.service.response

import com.example.hotarticle.client.ArticleClient
import java.time.LocalDateTime

data class HotArticleResponse(
    val articleId: Long,
    val title: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(articleResponse: ArticleClient.ArticleResponse): HotArticleResponse {
            return HotArticleResponse(
                articleId = articleResponse.articleId,
                title = articleResponse.title,
                createdAt = articleResponse.createdAt
            )
        }
    }
}