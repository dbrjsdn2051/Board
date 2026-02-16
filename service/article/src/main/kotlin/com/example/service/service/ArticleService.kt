package com.example.service.service

import com.example.service.entity.Article
import com.example.service.repository.ArticleRepository
import com.example.service.service.request.ArticleCreateRequest
import com.example.service.service.request.ArticleUpdateRequest
import com.example.service.service.response.ArticlePageResponse
import com.example.service.service.response.ArticleResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ArticleService (
    private val articleRepository: ArticleRepository,
){
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = Article.new(snowflake.nextId()) {
            title = request.title
            content = request.content
            boardId = request.boardId
            writerId = request.writerId
            createdAt = LocalDateTime.now()
            modifiedAt = LocalDateTime.now()
        }
        return ArticleResponse.from(article)
    }

    @Transactional(readOnly = true)
    fun read(articleId: Long): ArticleResponse {
        val article = Article.findById(articleId)
            ?: throw NoSuchElementException("Article not found: $articleId")
        return ArticleResponse.from(article)
    }

    @Transactional
    fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = Article.findById(articleId)
            ?: throw NoSuchElementException("Article not found: $articleId")
        article.update(request.title, request.content)
        return ArticleResponse.from(article)
    }

    @Transactional
    fun delete(articleId: Long) {
        val article = Article.findById(articleId)
            ?: throw NoSuchElementException("Article not found: $articleId")
        article.delete()
    }

    @Transactional(readOnly = true)
    fun readAll(boardId: Long, page: Long, pageSize: Long): ArticlePageResponse {
        val articles = articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize)
            .map { ArticleResponse.from(it) }
        val articleCount = articleRepository.count(
            boardId,
            PageLimitCalculator.calculatePageLimit(page, pageSize, 10L),
        )
        return ArticlePageResponse(articles, articleCount)
    }
}
