package com.example.service.service

import com.example.service.entity.ArticleLike
import com.example.service.entity.ArticleLikeCount
import com.example.service.repository.ArticleLikeCountRepository
import com.example.service.repository.ArticleLikeRepository
import com.example.service.service.response.ArticleLikeResponse
import board.common.event.EventType
import board.common.event.payload.ArticleLikedEventPayload
import board.common.event.payload.ArticleUnlikedEventPayload
import board.common.outboxmessagerelay.OutboxEventPublisher
import board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ArticleLikeService(
    private val snowflake: Snowflake = Snowflake(),
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleLikeCountRepository: ArticleLikeCountRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
) {

    @Transactional(readOnly = true)
    fun read(articleId: Long, userId: Long): ArticleLikeResponse {
        val articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?: throw NoSuchElementException("Article like not found: $articleId")
        return ArticleLikeResponse.from(articleLike)
    }

    @Transactional
    fun likePessimisticLock1(articleId: Long, userId: Long) {
        val articleLike = ArticleLike.new(snowflake.nextId()) {
            this.articleId = articleId
            this.userId = userId
            this.createdAt = LocalDateTime.now()
        }

        val result = articleLikeCountRepository.increase(articleId)
        if (result == 0) {
            ArticleLikeCount.new(articleId) {
                likeCount = 1L
            }
        }

        outboxEventPublisher.publish(
            EventType.ARTICLE_LIKED,
            ArticleLikedEventPayload(
                articleLikeId = articleLike.id.value,
                articleId = articleId,
                userId = userId,
                createdAt = articleLike.createdAt,
                articleLikeCount = count(articleId)
            ),
            articleId
        )
    }

    @Transactional
    fun unlikePessimisticLock1(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?: return
        articleLikeCountRepository.decrease(articleId)
        articleLike.delete()

        outboxEventPublisher.publish(
            EventType.ARTICLE_UNLIKED,
            ArticleUnlikedEventPayload(
                articleLikeId = articleLike.id.value,
                articleId = articleId,
                userId = userId,
                createdAt = articleLike.createdAt,
                articleLikeCount = count(articleId)
            ),
            articleId
        )
    }

    @Transactional
    fun likePessimisticLock2(articleId: Long, userId: Long) {
        ArticleLike.new(snowflake.nextId()) {
            this.articleId = articleId
            this.userId = userId
            this.createdAt = LocalDateTime.now()
        }

        val articleLikeCount =
            articleLikeCountRepository.findLockedByArticleId(articleId) ?: ArticleLikeCount.new(articleId) {
                likeCount = 0L
            }
        articleLikeCount.increase()
    }

    @Transactional
    fun unlikePessimisticLock2(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?: return
        val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
            ?: throw NoSuchElementException("Article like count not found: $articleId")
        articleLikeCount.decrease()
        articleLike.delete()
    }

    @Transactional
    fun likeOptimisticLock(articleId: Long, userId: Long) {
        ArticleLike.new(snowflake.nextId()) {
            this.articleId = articleId
            this.userId = userId
            this.createdAt = LocalDateTime.now()
        }
        val articleLikeCount =
            ArticleLikeCount.findById(articleId) ?: ArticleLikeCount.new(articleId) { likeCount = 0L }
        articleLikeCount.increase()
    }

    @Transactional
    fun unlikeOptimisticLock(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?: return
        val articleLikeCount =
            ArticleLikeCount.findById(articleId) ?: throw NoSuchElementException("Article like count not found: $articleId")
        articleLikeCount.decrease()
        articleLike.delete()
    }

    @Transactional(readOnly = true)
    fun count(articleId: Long): Long {
        return ArticleLikeCount.findById(articleId)?.likeCount ?: 0L
    }
}
