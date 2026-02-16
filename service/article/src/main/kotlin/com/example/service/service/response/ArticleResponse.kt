package com.example.service.service.response

import com.example.service.entity.Article
import java.time.LocalDateTime

data class ArticleResponse(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(article: Article): ArticleResponse = ArticleResponse(
            articleId = article.id.value,
            title = article.title,
            content = article.content,
            boardId = article.boardId,
            writerId = article.writerId,
            createdAt = article.createdAt,
            modifiedAt = article.modifiedAt,
        )
    }
}
