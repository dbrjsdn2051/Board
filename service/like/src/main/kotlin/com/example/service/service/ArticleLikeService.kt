package com.example.service.service

import com.example.service.entity.ArticleLike
import com.example.service.entity.ArticleLikeCount
import com.example.service.repository.ArticleLikeCountRepository
import com.example.service.repository.ArticleLikeRepository
import com.example.service.service.response.ArticleLikeResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ArticleLikeService(
    private val snowflake: Snowflake = Snowflake(),
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleLikeCountRepository: ArticleLikeCountRepository
) {

    @Transactional(readOnly = true)
    fun read(articleId: Long, userId: Long): ArticleLikeResponse {
        val articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?: throw NoSuchElementException("Article like not found: $articleId")
        return ArticleLikeResponse.from(articleLike)
    }

    @Transactional
    fun likePessimisticLock1(articleId: Long, userId: Long) {
        ArticleLike.new(snowflake.nextId()) {
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
    }

    @Transactional
    fun unlikePessimisticLock1(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?: return
        articleLikeCountRepository.decrease(articleId)
        articleLike.delete()
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
