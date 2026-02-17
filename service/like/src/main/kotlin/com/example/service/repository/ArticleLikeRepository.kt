package com.example.service.repository

import com.example.service.entity.ArticleLike
import com.example.service.entity.ArticleLikes
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ArticleLikeRepository {

    @Transactional(readOnly = true)
    fun findByArticleIdAndUserId(userId: Long, articleId: Long): ArticleLike? {
        return ArticleLike.find {
            (ArticleLikes.articleId eq articleId) and (ArticleLikes.userId eq userId)
        }.firstOrNull()
    }
}